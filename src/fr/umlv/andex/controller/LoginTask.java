package fr.umlv.andex.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import fr.umlv.andex.data.User;

public class LoginTask extends AsyncTask<String, Void, User>{
	User user=null;

	@Override
	protected User doInBackground(String... params) {

		try {
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost("http://192.168.0.18:12345/connect");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username",params[0]));
			nameValuePairs.add(new BasicNameValuePair("password",params[1]));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

			httpPost.getRequestLine();

			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				user = new User();
				user.setUser(params[0]);
				user.setPassword(params[1]);
				
				String responseBody = EntityUtils.toString(resEntity);
				try {
					JSONObject jobj = new JSONObject(responseBody);
					user.setToken(jobj.getString("token"));
					JSONArray exams = jobj.getJSONArray("exams");
					
					for(int i =0;i<exams.length();i++){
						user.addExam(exams.getString(i));
					}

					System.out.println(user.getToken());
					System.out.println(user.getExamList());
					
					
					System.out.println();

					/*	
					HttpGet httpGet = new HttpGet("http://"+Property.SERVER_ADRESS+":"+Property.SERVER_PORT+"/exam");


					List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
					nameValuePairs1.add(new BasicNameValuePair("token",jobj.optString("token")));
					nameValuePairs1.add(new BasicNameValuePair("exam","mathexam1"));
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs1)); 

					httpPost.getRequestLine();

					 response = httpclient.execute(httpGet);
					 resEntity = response.getEntity();

					if (resEntity != null) {
						 responseBody = EntityUtils.toString(resEntity);
						 System.out.println(responseBody);
					}
					 */
					System.out.println("LOGINTASK4");

				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("ERROR");

				}
			}
		}catch(IOException e){

		}

		return user;
	}

	@Override
	protected void onPostExecute(User result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
	

}
