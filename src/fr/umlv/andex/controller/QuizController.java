package fr.umlv.andex.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import fr.umlv.andex.bdd.AndexDAO;
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

public class QuizController {
	
	private static HashMap<Long, Question> server = new HashMap<Long, Question>();

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
		
		if(idQuiz == 1){
			
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
			
			TreeQuestion tree = new TreeQuestion();
			tree.setNodes(new ArrayList<NodeQuestion>());
			tree.getNodes().add(question1);
			tree.getNodes().add(question1A);
			
			Quiz quiz = new Quiz();
			quiz.setTree(tree);
			quiz.setDescription("Examen d'algorithmique II.");
			quiz.setState(StateQuiz.DONE);
			return quiz;
	
		}else
		{
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
			question1A.setTime(20000);
			
			question1.setNodes(new ArrayList<NodeQuestion>());
			question1.getNodes().add(question2);
			
			question2.setNodes(new ArrayList<NodeQuestion>());
			question2.getNodes().add(question3);
			
			TreeQuestion tree = new TreeQuestion();
			tree.setNodes(new ArrayList<NodeQuestion>());
			tree.getNodes().add(question1);
			tree.getNodes().add(question1A);
			
			Quiz quiz = new Quiz();
			quiz.setTree(tree);
			quiz.setDescription("Examen d'algorithmique II.");
			quiz.setState(StateQuiz.IN_PROGRESS);
			return quiz;
		}
	}
	
	public Question findQuestion(Context context, long idQuestion, long idUser){
		
		//TODO
		Question questionSaved = server.get(idQuestion);
		System.out.println("Holaa:"+idQuestion);
		if(questionSaved==null){
			
			Question question = new Question();
			question.setIdQuestion(idQuestion);
			question.setTitle("Titulo Uno");
			question.setText("Questionnnnnnnnnnnnn shhehnfofm shdgr" +
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
			
			server.put(idQuestion, question);
			questionSaved = question;
		}
		
		
		//FIN TODO
		AndexDAO dao = new AndexDAO(context);
		
		for(Answer answer: questionSaved.getAnswers()){
			
			switch(answer.getTypeAnswer()){
				
				case TYPE_ANSWER_CHECK:{
					AnswerCheck answerCheck = (AnswerCheck)answer;
					List<Integer> res = dao.getAnswersValuesNumber(idQuestion, idUser, answerCheck.getTypeAnswer());
					answerCheck.setValues(res);
				}break;
				case TYPE_ANSWER_PHOTO:{
					AnswerPhoto answerPhoto = (AnswerPhoto)answer;
					List<String> res = dao.getAnswersValuesString(idQuestion, idUser, answerPhoto.getTypeAnswer()); 
					if(res.size()>0)
						answerPhoto.setPath(res.get(0));
				}break;
				case TYPE_ANSWER_RADIO:{
					AnswerRadio answerRadio = (AnswerRadio)answer;
					List<Integer> res = dao.getAnswersValuesNumber(idQuestion, idUser, answerRadio.getTypeAnswer());
					
					if(res.size()>0)
						answerRadio.setValue(res.get(0));
				}break;
				case TYPE_ANSWER_SCHEMA:{
					AnswerSchema answerSchema = (AnswerSchema)answer;
					List<String> res = dao.getAnswersValuesString(idQuestion, idUser, answerSchema.getTypeAnswer()); 
					if(res.size()>0)
						answerSchema.setPath(res.get(0));
				}break;
				case TYPE_ANSWER_TEXT:{
					AnswerText answerText = (AnswerText)answer;
					List<String> res = dao.getAnswersValuesString(idQuestion, idUser, answerText.getTypeAnswer()); 
					if(res.size()>0)
						answerText.setValue(res.get(0));
				}
			}
		}
		
		return questionSaved;
	}
	
	public long findNextQuestion(long idQuestion){
		return 2L;
	}
	
	public long findPreviousQuestion(long id){
		return 1L;
	}
	
	public boolean examExist(Context context, long idQuiz, long idUser){
		AndexDAO dao = new AndexDAO(context);
		return dao.examExist(idQuiz, idUser);
	}
	
	public void saveExam(Context context, long idQuiz, long idUser){
		AndexDAO dao = new AndexDAO(context);
		dao.insertExam(idUser, idQuiz);
	}
	
	public void saveQuestion(Context context, Question question, long idUser){
		
		//TODO
		Question questionSaved = server.get(question.getIdQuestion());
		System.out.println(question.getIdQuestion());
		questionSaved.setReadOnly(question.isReadOnly());
		//TODO
		
		AndexDAO dao = new AndexDAO(context);
		
		if(!question.isReadOnly()){
			if(!dao.questionExist(question.getIdQuestion(), idUser, question.getIdQuiz())){
				dao.insertQuestion(question, idUser);
			}else{
				dao.updateQuestion(question, idUser);
			}
		}
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
