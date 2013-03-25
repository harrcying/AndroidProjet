package fr.umlv.andex.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.JDOMException;

import android.content.Context;
import android.os.Environment;
import fr.umlv.andex.data.Answer;
import fr.umlv.andex.data.Question;
import fr.umlv.andex.data.Quiz;
import fr.umlv.andex.data.QuizDescription;
import fr.umlv.andex.parser.XMLEncoder;
import fr.umlv.andex.parser.XMLException;
import fr.umlv.andex.parser.XMLParser;

public class QuizController {
	Quiz q;

	public List<QuizDescription> findAllQuiz(){

		QuizDescription des = new QuizDescription();
		des.setIdQuiz(1);
		des.setTitleQuiz("Algorithmique I");
		des.setInProgress(true);

		QuizDescription des2 = new QuizDescription();
		des2.setIdQuiz(2);
		des2.setTitleQuiz("Algorithmique II");

		QuizDescription des3 = new QuizDescription();
		des3.setIdQuiz(3);
		des3.setTitleQuiz("Algorithmique 3");
		des3.setInProgress(true);

		QuizDescription des4 = new QuizDescription();
		des4.setIdQuiz(4);
		des4.setTitleQuiz("Algorithmique 3");

		QuizDescription des5 = new QuizDescription();
		des5.setIdQuiz(5);
		des5.setTitleQuiz("Algorithmique 3");
		des5.setInProgress(true);

		QuizDescription des6 = new QuizDescription();
		des6.setIdQuiz(6);
		des6.setTitleQuiz("Algorithmique 3");

		ArrayList<QuizDescription> list = new ArrayList<QuizDescription>();
		list.add(des);
		list.add(des2);
		list.add(des3);
		list.add(des4);
		list.add(des5);
		list.add(des6);

		return list;
	}

	public Quiz getQuiz(long idQuiz){ //TODO Si possible, ajouter le fichier WML dans les attributs du quizz.
		
		final String fileName;
		if(idQuiz%2 != 0){
			fileName = "examC.xml";
		}else {
			fileName = "examCAnnale.xml";
		}
		
		Thread thd = new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(Environment.getExternalStorageDirectory(), fileName);
				XMLParser parser = new XMLParser(file);
				try {
					q = parser.getInfo();
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
		try {
			thd.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return q;
	}

	public Question findNextQuestion(Quiz q, Question question){
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

	public void saveExam(Context context, Quiz quiz, long idUser){
		 // TODO : ludo
	}

	public void saveQuestion(Context context, Quiz quiz, Question question, long idUser){ 
		// La sauvegarde du fichier a l'air de fonctionner mais pas l'ajout de la réponse
		// L'ajout de la réponse marche hors android
		// Voir pour coriger les bugs
		
		System.out.println("SAVE HERE");

		final String fileName;
		if(quiz.getIdQuiz()%2 != 0){
			fileName = "examC.xml";
		}else {
			fileName = "examCAnnale.xml";
		}
		
		File file = new File(Environment.getExternalStorageDirectory(), fileName);
		
//		file.createNewFile();
		
		XMLEncoder encoder = new XMLEncoder(file);
		
		System.out.println("file : " + file.getAbsolutePath());
		
		for(Answer a: question.getAnswers()) {
			try {
				encoder.putAnswer(question.getIdQuestion(), a);
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		//TODO : Ludo
		/* Ce qui suit sera a supprimer :*/
//		String str = "Question " + question.getIdQuestion() + " : ";
	/*	for (Answer a : question.getAnswers()) {
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
		} */
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
