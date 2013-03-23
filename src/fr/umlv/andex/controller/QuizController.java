package fr.umlv.andex.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.JDOMException;

import android.content.Context;
import android.os.Environment;
import fr.umlv.andex.data.Answer;
import fr.umlv.andex.data.AnswerCheck;
import fr.umlv.andex.data.AnswerPhoto;
import fr.umlv.andex.data.AnswerRadio;
import fr.umlv.andex.data.AnswerSchema;
import fr.umlv.andex.data.AnswerText;
import fr.umlv.andex.data.NodeQuestion;
import fr.umlv.andex.data.Option;
import fr.umlv.andex.data.Question;
import fr.umlv.andex.data.Quiz;
import fr.umlv.andex.data.QuizDescription;
import fr.umlv.andex.data.StateQuiz;
import fr.umlv.andex.data.TreeQuestion;
import fr.umlv.andex.parser.XMLException;
import fr.umlv.andex.parser.XMLParser;

public class QuizController {

	public List<QuizDescription> findAllQuiz(){

		QuizDescription des = new QuizDescription();
		des.setIdQuiz(1);
		des.setTitleQuiz("Algorithmique I");

		QuizDescription des2 = new QuizDescription();
		des2.setIdQuiz(2);
		des2.setTitleQuiz("Algorithmique II");
		des2.setInProgress(true);

		QuizDescription des3 = new QuizDescription();
		des3.setIdQuiz(2);
		des3.setTitleQuiz("Algorithmique 3");
		des3.setInProgress(true);

		QuizDescription des4 = new QuizDescription();
		des4.setIdQuiz(2);
		des4.setTitleQuiz("Algorithmique 3");
		des4.setInProgress(true);

		QuizDescription des5 = new QuizDescription();
		des5.setIdQuiz(2);
		des5.setTitleQuiz("Algorithmique 3");
		des5.setInProgress(true);

		QuizDescription des6 = new QuizDescription();
		des6.setIdQuiz(2);
		des6.setTitleQuiz("Algorithmique 3");
		des6.setInProgress(true);

		ArrayList<QuizDescription> list = new ArrayList<QuizDescription>();
		list.add(des);
		list.add(des2);
		list.add(des3);
		list.add(des4);
		list.add(des5);
		list.add(des6);

		return list;
	}

	public Quiz getQuiz(long idQuiz){

		NodeQuestion question1 = new NodeQuestion();
		question1.setId(1);
		question1.setTitle("Niveau 0");
		NodeQuestion question2 = new NodeQuestion();
		question2.setId(2);
		question2.setTitle("Niveau 1");
		NodeQuestion question3 = new NodeQuestion();
		question3.setId(3);
		question3.setTitle("Niveau 2");

		NodeQuestion question1A = new NodeQuestion();
		question1A.setId(4);
		question1A.setTitle("Niveau 0");

		question1.setNodes(new ArrayList<NodeQuestion>());
		question1.getNodes().add(question2);

		question2.setNodes(new ArrayList<NodeQuestion>());
		question2.getNodes().add(question3);

		Question question = new Question();
		question.setIdQuestion(5);
		question.setTitle("Titulo Uno");
		question.setText("Question 1 : " +
				"bnekndfmm shhgvrvjslldm shahyve hhshshshshsn dnnnd");

		Option option = new Option();
		option.setDescription("Option 1");
		option.setId(1);

		Option option2 = new Option();
		option2.setDescription("Option 2");
		option2.setId(2);

		AnswerText answerText2 = new AnswerText();
		AnswerRadio answerRadio2 = new AnswerRadio();

		answerRadio2.getOptions().add(option);
		answerRadio2.getOptions().add(option2);

		question.setImage("Hola".getBytes());
		question.getAnswers().add(answerText2);
		question.getAnswers().add(answerRadio2);
		question.setTime(20000);
		LinkedList<Question> l = new LinkedList<Question>();
		l.add(question);
		question3.setQuestion(question);
		
		Question questionA = new Question();
		questionA.setIdQuestion(5);
		questionA.setTitle("Titulo Uno");
		questionA.setText("Question 2 : " +
				"bnekndfmm shhgvrvjslldm shahyve hhshshshshsn dnnnd");

		Option option1 = new Option();
		option1.setDescription("Option 1");
		option1.setId(1);

		Option option21 = new Option();
		option21.setDescription("Option 2");
		option21.setId(2);

		AnswerText answerText21 = new AnswerText();
		AnswerRadio answerRadio21 = new AnswerRadio();

		answerRadio21.getOptions().add(option1);
		answerRadio21.getOptions().add(option21);

		questionA.setImage("Hola".getBytes());
		questionA.getAnswers().add(answerText21);
		questionA.getAnswers().add(answerRadio21);
		questionA.setTime(20000);
		l.add(questionA);
		question1A.setQuestion(questionA);

		TreeQuestion tree = new TreeQuestion();
		tree.setNodes(new ArrayList<NodeQuestion>());
		tree.getNodes().add(question1);
		tree.getNodes().add(question1A);
		
		Quiz quiz = new Quiz();
		quiz.setTree(tree);
		quiz.setQuestionsByOrder(l);
		quiz.setDescription("Examen d'algorithmique II.");
		
		if(idQuiz == 1){
			quiz.setState(StateQuiz.DONE);
		}else {
			quiz.setState(StateQuiz.IN_PROGRESS);
		}
		return quiz;
	}

	public Question findNextQuestion(Quiz q, Question question){
		System.out.println(question);
		int index = q.getQuestionsByOrder().indexOf(question);
		if (index<q.getQuestionsByOrder().size()-1) {
			return q.getQuestionsByOrder().get(index+1);
		} else {
			return question;
		}
	}

	public Question findPreviousQuestion(Quiz q, Question question){
		int index = q.getQuestionsByOrder().indexOf(question);
		if (index>0) {
			return q.getQuestionsByOrder().get(index-1);
		} else {
			return question;
		}
	}

	public void saveExam(Context context, final String fileName){
		 Thread thd = new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(Environment.getExternalStorageDirectory(), fileName);
				XMLParser parser = new XMLParser(file);
				try {
					parser.getInfo();
				} catch (XMLException e) {
					System.err.println("Fichier XML eronne a la balise d'id " + e.getMessage() + "! \nVeuillez verifier que votre fichier corresponde au format demande.");
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("FNF : " + fileName);
				}
			}
		});
		thd.start();
	}

	public void saveQuestion(Context context, Question question, long idUser){

		//TODO : Ludo
		/* Ce qui suit sera a supprimer :*/
		String str = "Question " + question.getIdQuestion() + " : ";
		for (Answer a : question.getAnswers()) {
			switch (a.getTypeAnswer()) {
			case TYPE_ANSWER_CHECK :
				AnswerCheck ac = (AnswerCheck)a;
				for (int id : ac.getValues()) {
					str = str + id + ", ";
				}
				break;
			case TYPE_ANSWER_PHOTO:
				AnswerPhoto ap = (AnswerPhoto)a;
				str = str + ap.getPath();
				break;
			case TYPE_ANSWER_RADIO:
				AnswerRadio ar = (AnswerRadio)a;
				str = str + ar.getValue();
				break;
			case TYPE_ANSWER_SCHEMA:
				AnswerSchema as = (AnswerSchema)a;
				str = str + as.getPath();
				break;
			case TYPE_ANSWER_TEXT:
				AnswerText at = (AnswerText)a;
				str = str + at.getValue();
				break;
			default:
				break;
			}
			str = str + " / ";
		}
		System.out.println(str);
	}

	public void addLogUser(Context context, String message, long idUser){

		FileOutputStream fOut = null; 
		OutputStreamWriter osw = null; 

		try{ 

			fOut = context.openFileOutput("logUser"+idUser+".txt", Context.MODE_APPEND);       
			osw = new OutputStreamWriter(fOut); 
			osw.write(message+"\n"); 
			osw.flush(); 

		}catch (Exception e) {       
			e.printStackTrace();
		} finally { 
			try { 
				osw.close(); 
				fOut.close(); 
			} catch (IOException e) { 
				e.printStackTrace();   
			} 
		}	
	}

	public String getLogUser(Context context, long idUser){

		FileInputStream fIn = null; 
		InputStreamReader isr = null; 

		char[] inputBuffer = new char[1000]; 
		String data = null; 

		try{ 
			fIn = context.openFileInput("logUser"+idUser+".txt");       
			isr = new InputStreamReader(fIn); 
			isr.read(inputBuffer); 
			data = new String(inputBuffer); 
		}catch (Exception e) {       
			e.printStackTrace();
		}finally { 
			try { 
				isr.close(); 
				fIn.close(); 
			} catch (IOException e) { 
				e.printStackTrace();
			} 
		} 
		return data; 
	}


}
