package fr.umlv.andex.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AndexBaseSQLite extends SQLiteOpenHelper{

	public static final String TABLE_EXAM ="EXAM";
	public static final String TABLE_QUESTION = "QUESTIONS";
	public static final String TABLE_ANSWER_DETAIL = "ANSWERS_DETAIL";
	
	public static final String EXAM_ID = "EXAM_ID";
	public static final String EXAM_ID_USER = "EXAM_ID_USER";
	
	public static final String QUESTION_ID = "QUESTION_ID";

	public static final String ANSWER_TYPE = "ANSWER_TYPE";
	
	public static final String ANSWER_DETAIL_ID = "ANSWER_DETAIL_ID";
	public static final String ANSWER_DETAIL_VALUE = "ANSWER_DETAIL_V";	
	
	private static final String CREATE_EXAM = 
			"CREATE TABLE "+TABLE_EXAM + " ("+EXAM_ID+" INTEGER PRIMARY KEY, "+EXAM_ID_USER+" INTEGER)";
	
	private static final String CREATE_QUESTION = 
			"CREATE TABLE "+TABLE_QUESTION+" ("+EXAM_ID+" INTEGER, "+QUESTION_ID+" INTEGER  PRIMARY KEY, "+EXAM_ID_USER+" INTEGER)";
		
	private static final String CREATE_ANSWER_DETAIL = 
			"CREATE TABLE "+TABLE_ANSWER_DETAIL+" ("+QUESTION_ID+" INTEGER, "+ANSWER_TYPE+" INTEGER,"+ANSWER_DETAIL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ANSWER_DETAIL_VALUE+" TEXT, "+EXAM_ID_USER+" INTEGER)";
	
	public AndexBaseSQLite(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		//onUpgrade(getReadableDatabase(), 0, 0);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_EXAM);
		db.execSQL(CREATE_QUESTION);
		db.execSQL(CREATE_ANSWER_DETAIL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE " + TABLE_ANSWER_DETAIL + ";");
		db.execSQL("DROP TABLE " + TABLE_EXAM + ";");
		db.execSQL("DROP TABLE " + TABLE_QUESTION + ";");
		onCreate(db);
	}

}
