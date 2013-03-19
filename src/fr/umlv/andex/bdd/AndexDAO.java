package fr.umlv.andex.bdd;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.umlv.andex.data.Answer;
import fr.umlv.andex.data.AnswerCheck;
import fr.umlv.andex.data.AnswerPhoto;
import fr.umlv.andex.data.AnswerRadio;
import fr.umlv.andex.data.AnswerSchema;
import fr.umlv.andex.data.AnswerText;
import fr.umlv.andex.data.Question;
import fr.umlv.andex.data.TypeAnswer;

public class AndexDAO {
	
	private SQLiteDatabase bdd;
	private AndexBaseSQLite maBaseSQLite;
	
	private static final int VERSION_BDD = 1;
	private static final String NOM_BDD = "Andex.db";
	
	public AndexDAO(Context context){
		maBaseSQLite = new AndexBaseSQLite(context, NOM_BDD, null, VERSION_BDD);
	}
	
	private void open() {
		bdd = maBaseSQLite.getWritableDatabase();
	}

	private void close() {
		bdd.close();
	}

	/*
	private SQLiteDatabase getBDD() {
		return bdd;
	}*/
	
	public void insertExam(long idUser, long idQuiz){
		
		ContentValues values = new ContentValues();
		values.put(AndexBaseSQLite.EXAM_ID, idQuiz);
		values.put(AndexBaseSQLite.EXAM_ID_USER, idUser);
		
		open();
		bdd.insert(AndexBaseSQLite.TABLE_EXAM, null, values);
		close();
	}
	
	
	public void updateQuestion(Question question, long idUser){
		open();
		bdd.delete(AndexBaseSQLite.TABLE_ANSWER_DETAIL, 
				AndexBaseSQLite.QUESTION_ID+"=? AND "+AndexBaseSQLite.EXAM_ID_USER+"=?", 
					new String[]{question.getIdQuestion()+"", idUser+""});
		
		insertAnswers(question.getAnswers(), question.getIdQuestion(), idUser);
		close();
	}
	
	public void insertQuestion(Question question, long idUser){
		
		System.out.println("Holaaaaa");
		
		ContentValues values = new ContentValues();
		values.put(AndexBaseSQLite.EXAM_ID, question.getIdQuiz());
		values.put(AndexBaseSQLite.QUESTION_ID, question.getIdQuestion());
		values.put(AndexBaseSQLite.EXAM_ID_USER, idUser);
		
		open();
		bdd.insert(AndexBaseSQLite.TABLE_QUESTION, null, values);
		insertAnswers(question.getAnswers(), question.getIdQuestion(), idUser);
		close();
	}
	
	private void insertAnswers(List<Answer> answers, long idQuestion, long idUser){
		
		for(Answer answer: answers){
			switch(answer.getTypeAnswer()){
				case TYPE_ANSWER_CHECK:{
					insertDetailAnswerCheck((AnswerCheck)answer,idQuestion, idUser);
				}break;
				case TYPE_ANSWER_PHOTO:{
					insertDetailAnswerPhoto((AnswerPhoto)answer, idQuestion, idUser);
				}break;
				case TYPE_ANSWER_RADIO:{
					insertDetailAnswerRadio((AnswerRadio)answer, idQuestion, idUser);
				}break;
				case TYPE_ANSWER_SCHEMA:{
					insertDetailAnswerSchema((AnswerSchema)answer, idQuestion, idUser);
				}break;
				default:{
					insertDetailAnswerText((AnswerText)answer, idQuestion, idUser);
				}
			}	
		}
		
	}
	
	public void insertDetailAnswerPhoto(AnswerPhoto answerPhoto, long idQuestion, long idUser){
		insertDetailAnswer(answerPhoto.getTypeAnswer().ordinal(), answerPhoto.getPath(), idQuestion, idUser);
	}
	
	public void insertDetailAnswerSchema(AnswerSchema answerSchema, long idQuestion, long idUser){
		insertDetailAnswer(answerSchema.getTypeAnswer().ordinal(), answerSchema.getPath(), idQuestion, idUser);
	}
	
	public void insertDetailAnswerCheck(AnswerCheck answerCheck, long idQuestion, long idUser){
		for(Integer value:answerCheck.getValues()){
			insertDetailAnswer(answerCheck.getTypeAnswer().ordinal(), value+"", idQuestion, idUser);
		}
	}
	
	public void insertDetailAnswerRadio(AnswerRadio answerRadio, long idQuestion, long idUser){
		insertDetailAnswer(answerRadio.getTypeAnswer().ordinal(), answerRadio.getValue()+"", idQuestion, idUser);
	}
	
	public void insertDetailAnswerText(AnswerText answerText, long idQuestion, long idUser){
		insertDetailAnswer(answerText.getTypeAnswer().ordinal(), answerText.getValue(), idQuestion, idUser);
	}
	
	public void insertDetailAnswer(long idType, String value, long idQuestion, long idUser){
		
		ContentValues values = new ContentValues();
		values.put(AndexBaseSQLite.QUESTION_ID, idQuestion);
		values.put(AndexBaseSQLite.EXAM_ID_USER, idUser);
		values.put(AndexBaseSQLite.ANSWER_TYPE, idType);
		values.put(AndexBaseSQLite.ANSWER_DETAIL_VALUE, value);
		bdd.insert(AndexBaseSQLite.TABLE_ANSWER_DETAIL, null, values);
	}
	
	public boolean examExist(long idQuiz, long idUser){
		
		open();
		Cursor c = bdd.query(AndexBaseSQLite.TABLE_EXAM,
				new String[] { AndexBaseSQLite.EXAM_ID}, AndexBaseSQLite.EXAM_ID+"=? AND "+AndexBaseSQLite.EXAM_ID_USER +"=?", new String[]{idQuiz+"", idUser+""}, null, null, null);
		
		boolean result = c.moveToNext();
		close();
		
		return result;
	}
	
	public boolean questionExist(long idQuestion, long idUser, long idQuiz){
		
		open();
		Cursor c = bdd.query(AndexBaseSQLite.TABLE_QUESTION,
				new String[] { AndexBaseSQLite.QUESTION_ID}, AndexBaseSQLite.QUESTION_ID+"=? AND "+AndexBaseSQLite.EXAM_ID_USER +"=? AND "+AndexBaseSQLite.EXAM_ID+"=?", new String[]{idQuestion+"", idUser+"", idQuiz+""}, null, null, null);
		
		boolean result = c.moveToNext();
		close();
		
		return result;
	}

	public List<Integer> getAnswersValuesNumber(long idQuestion, long idUser,
			TypeAnswer typeAnswer) {
		
		open();
		
		Cursor c = bdd.query(AndexBaseSQLite.TABLE_ANSWER_DETAIL,
				new String[] { AndexBaseSQLite.ANSWER_DETAIL_VALUE}, AndexBaseSQLite.QUESTION_ID+"=? AND "+AndexBaseSQLite.EXAM_ID_USER +"=? AND "+AndexBaseSQLite.ANSWER_TYPE+"=?", new String[]{idQuestion+"", idUser+"", typeAnswer.ordinal()+""}, null, null, null);
		
		List<Integer> res = new ArrayList<Integer>();		
		
		while(c.moveToNext()){
			res.add(c.getInt(0));
		}
		close();
		return res;
	}

	public List<String> getAnswersValuesString(long idQuestion, long idUser,
			TypeAnswer typeAnswer) {
		open();
		
		Cursor c = bdd.query(AndexBaseSQLite.TABLE_ANSWER_DETAIL,
				new String[] { AndexBaseSQLite.ANSWER_DETAIL_VALUE}, AndexBaseSQLite.QUESTION_ID+"=? AND "+AndexBaseSQLite.EXAM_ID_USER +"=? AND "+AndexBaseSQLite.ANSWER_TYPE+"=?", new String[]{idQuestion+"", idUser+"", typeAnswer.ordinal()+""}, null, null, null);
		
		List<String> res = new ArrayList<String>();
		
		while(c.moveToNext()){
			res.add(c.getString(0));
		}
		
		close();
		return res;
	}
	
}
