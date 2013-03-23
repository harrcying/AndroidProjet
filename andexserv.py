#! /usr/bin/env python
"""
A basic RESTful server for communicating with Andex clients.
The server is started with the command:
andexserv.py interface-ip:port directory (e.g.: 127.0.0.1:12345)

The directory follows this layout:
= Subdirectories
- exams/: contains for each exam one directory with its data (exams/androidexam1/..., exams/mathexam/...); must be already populated
	The examination paper file is made by packaging into a zip file all the files in the exam directory
- submissions/: contains the student submissions (example of a submission file: submissions/mathexam/johndoe)
- updates/: the examiner adds in the updates/exam directory JSON files containing the updates to transmit to the examinees during the exam
- messages/: contains the messages transmitted by the examinees (one log file for each exam, e.g. messages/mathexam)
= Files (at the root of the directory)
- users.json: JSON formatted file containing the users with their password and courses
	{"foo1": {"password":"xyz", "courses": ["math", "java"]}, "foo2": {"password": "abc", "courses": ["math"]}, ...}
- courses.json: JSON formatted file linking the courses to the exams
	{"math": ["mathexam1", "mathexam2"], "java": ["javapartial", "javafinal"], ...}
The users.json and courses.json allow to deduce which exams each student can join (for example foo2 can join mathexam1 and mathexam2).

Updates:
2013/02/26: /connect can return a 403 error if the (user, password) are incorrect
2013/03/18: we don't open several times the temporary file of the zip archive for a better Windows compatibility

@author chilowi at univ-mlv.fr
"""

import os, os.path, sys, cgi
import BaseHTTPServer

LOCK_SLEEP_TIME = 0.01 # Waiting time to acquire the directory lock

class AutoDeletedReadableFile(object):
	def __init__(self, path): 
		self.path = path
		self.f = open(path, 'r')
	def read(self): 
		return self.f.read()
	def close(self):
		self.f.close()
		os.remove(self.path)

class HttpException(Exception):
	def __init__(self, code, message):
		self.code = code
		self.message = message


class RESTHandlerFactory(object):
	def __init__(self):
		self.methods = {}
		for method in ['GET','POST']:
			for m in filter(lambda x: x.startswith(method + '_'), dir(self)):
				self.methods[m] = getattr(self, m)
		self.tokens = {}
	def _handle(self, f):
		parsed = cgi.parse(f)
	def get_handler(self):
		methods = self.methods
		tokens = self.tokens
		class Handler(BaseHTTPServer.BaseHTTPRequestHandler):
			def treat(self):
				# Format path
				from urlparse import urlparse, parse_qs
				url = urlparse(self.path)
				path = url.path[1:] # Remove the starting slash
				try:
					method = methods["%s_%s" % (self.command, path)]
				except KeyError:
					self.send_error(404, '%s not found' % self.path)
					return
				# args = parse_qs(url.query)
				env = {"REQUEST_METHOD": self.command, "QUERY_STRING": url.query, 
					"CONTENT_LENGTH": self.headers.get('Content-Length', -1),
					"CONTENT_TYPE": self.headers.get('Content-Type', None) }
				parsed = cgi.parse(self.rfile, env)
				print >> sys.stderr, "Parsed: %s, %s" % (str(parsed), str(self.headers))
				args = {}
				for (k, v) in parsed.iteritems():
					print >> sys.stderr, "Found %s=%s" % (k, v)
					args[k] = v[0]
				if args.has_key('token'):
					# Check the authentication token
					try:
						t = tokens[args['token']]
						args['token'] = t
					except KeyError:
						self.send_error(403, 'The authentication token is invalid')
						return
				try:
					r = method(**args)
				except HttpException, e:
					self.send_response(e.code, e.message)
				else:
					if hasattr(r, 'read'):
						# r.seek(0)
						r2 = r.read() # It could be memory-costly for large files
						r.close()
					elif isinstance(r, basestring):
						r2 = r
					elif isinstance(r, dict) or isinstance(r, list):
						import json
						r2 = json.dumps(r)
					else:
						self.send_error(500, 'Type of result not supported')
						return
					self.send_response(200, 'OK')
					self.send_header('Content-Type', 'application/octet-stream')
					self.send_header('Content-Length', str(len(r2)))
					self.end_headers()
					self.wfile.write(r2)
			def do_GET(self): return self.treat()
			def do_POST(self): return self.treat()
		return Handler
				

class AndexServerHandlerFactory(RESTHandlerFactory):
	def _load(self):
		import json
		# Load the users
		with open(self._get_path('users.json'), 'r') as f:
			self.users = json.load(f)
		# Load the courses
		with open(self._get_path('courses.json'), 'r') as f:
			self.courses = json.load(f)
		# Load the exams
		for exam in os.listdir(self._get_path('exams')):
			self.exams.append(exam)
	def __init__(self, directory):
		super(AndexServerHandlerFactory, self).__init__()
		self.directory = directory
		self.users = []
		self.courses = {}
		self.exams = []
		self._load()
	def _get_path(self, *e):
		e = (self.directory,) + e
		return os.path.join(*e)
	def POST_connect(self, username, password):
		if not password: raise HttpException(403, "Blank password.")
		from uuid import uuid4
		exams = []
		if username in self.users and self.users[username].get('password', None) == password:
			for course in self.users[username]['courses']:
				try:
					exams += self.courses[course]
				except KeyError:
					print >> sys.stderr, "The course %s does not exist" % course
		else:
			raise HttpException(403, "Failed authentication")
		# Generate a random token
		token = str(uuid4())
		self.tokens[token] = {'username': username}
		return {'token': token, 'exams': exams}
	def GET_exam(self, token, exam):
		# Get the directory of the exam and compress it to a zip file
		d = self._get_path('exams', exam)
		if not os.path.exists(d):
			raise HttpException(404, "Exam %s does not exist" % exam)
		from tempfile import NamedTemporaryFile
		from zipfile import ZipFile, ZIP_DEFLATED
		tmpfile = NamedTemporaryFile(suffix='andexserv', delete=False)
		tmpfile.close()
		with ZipFile(tmpfile.name, 'w', compression=ZIP_DEFLATED) as z:
			for e in os.listdir(d):
				z.write(os.path.join(d, e), e)
		return AutoDeletedReadableFile(tmpfile.name)
	def GET_updates(self, token, exam, lastModified):
		# Read all the update files of the exam since lastModified and merge them into a JSON sequence
		lastModified = int(lastModified)
		import json
		r = []
		d = self._get_path('updates', exam)
		for f in os.listdir(d):
			mtime = int(os.path.getmtime(os.path.join(d, f)))
			if mtime > lastModified:
				with open(os.path.join(d, f), 'r') as fd:
					try:
						r.append({"date": mtime, "content": json.load(fd)})
					except:
						print >> sys.stderr, "Error when loading the update %s" % os.path.join(d, f)
		return r
	def POST_message(self, token, exam, content):
		from time import ctime, sleep
		examfile = self._get_path('messages', exam)
		# Use a directory as a lock; dirty but it should work everywhere
		locked = False
		while not locked:
			try:
				os.mkdir(examfile + ".lock")
				locked = True
			except OSError:
				sleep(LOCK_SLEEP_TIME)
		with open(examfile, 'a') as f: # Append the message in the exam log file
			f.write("%s:%s: %s\n" % (ctime(), token['username'], content))
		# Remove the lock
		os.rmdir(examfile + ".lock")
		return {'result': True}
	def POST_submit(self, token, exam, data):
		# Submit the student answers file
		d = self._get_path('submissions', exam, token['username'])
		if not os.path.exists(d): os.makedirs(d)
		i = len(os.listdir(d))
		with open(os.path.join(d, str(i+1)), 'w') as f:
			f.write(data)
		return {'result': True, 'version': i+1}
	def GET_fetch(self, token, exam, version):
		# Fetch the student answers file
		version = int(version)
		d = self._get_path('submissions', exam, token['username'])
		if version < 0: version = len(os.listdir(d))
		f = open(os.path.join(d, str(version)))
		return f

def serve(address, directory):
	h = BaseHTTPServer.HTTPServer(address, AndexServerHandlerFactory(directory).get_handler())
	return h.serve_forever()

if __name__ == "__main__":
	if len(sys.argv) < 3:
		print >> sys.stdout, __doc__
		sys.exit(-1)
	else:
		(iface, port) = sys.argv[1].split(':',1)
		serve((iface, int(port)), sys.argv[2])
