package fr.umlv.andex.view;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import fr.umlv.andex.R;
import fr.umlv.andex.controller.QuizController;
import fr.umlv.andex.data.Answer;
import fr.umlv.andex.data.AnswerCheck;
import fr.umlv.andex.data.AnswerPhoto;
import fr.umlv.andex.data.AnswerRadio;
import fr.umlv.andex.data.AnswerSchema;
import fr.umlv.andex.data.AnswerText;
import fr.umlv.andex.data.Option;
import fr.umlv.andex.data.Question;
import fr.umlv.andex.data.Quiz;

public class QuestionActivity extends Activity implements OnClickListener {

	private  final static int VIEW_ID_TEXT = Integer.MAX_VALUE;
	private  final static int VIEW_ID_RADIO = Integer.MAX_VALUE/2;
	
	private Quiz quiz;
	private Question question;
	private Answer answerImage;
	private long idUser;
	private final int CAMERA = 1;
	
	private DeadlineTask deadline;
	
	private Button buttonScheme;
	private Button buttonPhoto;
	private Button save;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		idUser = (Long)getIntent().getExtras().get("userId");
		//Long idQuestion = (Long)getIntent().getExtras().get("idQuestion");
		
		QuizController quizController = new QuizController();
        question = (Question)getIntent().getExtras().get("question");
        quiz = (Quiz)getIntent().getExtras().get("quiz");
        		
		LinearLayout layout = new LinearLayout(this); 
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        LinearLayout layoutIntern = new LinearLayout(this);
        layoutIntern.setBackgroundColor(Color.WHITE);
        layoutIntern.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
         	     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layoutIntern);
        layout.addView(scroll, layoutParams);
        
        String message = 
	       	String.format(getResources().getString(R.string.log_question_view)+" %s", idUser+"", question.getIdQuestion()+"");
	    quizController.addLogUser(this, message, idUser);
	    
        createView(layoutIntern);
        setContentView(layout);

	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void createView(LinearLayout layout){
		
		System.out.println("coucou");
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		int screenWidth = size.x;
		int halfScreenWidth = (int)(screenWidth *0.5);
		
		GridLayout titleLayout = new GridLayout(this);
		titleLayout.setColumnCount(2);
			
		GridLayout.LayoutParams firstCol = new GridLayout.LayoutParams(GridLayout.spec(2), GridLayout.spec(0));
		firstCol.width = halfScreenWidth;
		
		TextView title = new TextView(this);
		title.setText(question.getTitle());
		title.setTextColor(Color.BLACK);
        title.setTextSize(30);
        title.setPadding(5, 5, 5, 5);
        title.setTypeface(null,Typeface.BOLD);
        
        title.setLayoutParams(firstCol);
        titleLayout.addView(title, firstCol);
        
        GridLayout.LayoutParams secondCol = new GridLayout.LayoutParams(GridLayout.spec(2), GridLayout.spec(1));
        secondCol.width = halfScreenWidth;
        
		TextView timeView = new TextView(this);
		timeView.setTextColor(Color.RED);
		timeView.setTextSize(22);
		timeView.setGravity(Gravity.CENTER);
		
		timeView.setLayoutParams(secondCol);
        titleLayout.addView(timeView, secondCol);
        
		layout.addView(titleLayout);
				
		TextView description = new TextView(this);
		description.setText(question.getText());
		description.setTextColor(Color.BLACK);
		description.setPadding(0, 0, 0, 10);
		description.setTextSize(22);
		layout.addView(description);
		
		System.out.println("2");
		
		byte[] blob = question.getImage();
		if(blob!=null && blob.length>0){
			Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_launcher);
			//TODO
			//Bitmap bmp = BitmapFactory.decodeByteArray(blob,0,blob.length);
			ImageView image = new ImageView(this);
			image.setImageBitmap(icon);
			//view.setImageBitmap(bmp);
			layout.addView(image);
		}
		
		System.out.println("3");
		
		TextView questionSubtitle = new TextView(this);
		questionSubtitle.setText(R.string.subtitle_question);
		questionSubtitle.setTextColor(Color.BLACK);
		questionSubtitle.setPadding(0, 0, 0, 10);
		questionSubtitle.setTextSize(22);
		questionSubtitle.setTypeface(null,Typeface.BOLD);
		layout.addView(questionSubtitle);
		
		for(Answer answer:question.getAnswers()){
			
			switch(answer.getTypeAnswer()){
				case TYPE_ANSWER_RADIO:{
					createAnswerRadio(layout, answer);
				};break;
				case TYPE_ANSWER_TEXT:{
					createAnswerText(layout, answer);
				};break;
				case TYPE_ANSWER_SCHEMA:{
					createAnswerSchema(layout, answer);
				};break;
				case TYPE_ANSWER_PHOTO:{
					createAnswerPhoto(layout, answer);
				};break;
				case TYPE_ANSWER_CHECK:{
					createAnswerCheck(layout, answer);
				};break;
			}
		}
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 0, 5, 0);
		
        
        if(!question.isReadOnly()){
        	
    		if(question.getTime()>0){
    			deadline = new DeadlineTask();
    			deadline.setTimeView(timeView);
    			deadline.setActivity(this);
    			deadline.execute(question.getTime());
    			deadline.cancel(true);
    		}
        	
        	save = new Button(this);
    		save.setText(R.string.save);
    		save.setOnClickListener(this);
    		save.setTextColor(Color.BLACK);
    		layout.addView(save, layoutParams);
        }
		
		GridLayout grid = new GridLayout(this); 
		grid.setColumnCount(2);
		
		Button previous = new Button(this);
		previous.setText(R.string.previous);
		previous.setTextColor(Color.BLACK);
		previous.setOnClickListener(this);
		
		previous.setLayoutParams(firstCol);
		grid.addView(previous, firstCol);
		
		Button next = new Button(this);
		next.setText(R.string.next);
		next.setTextColor(Color.BLACK);
		next.setOnClickListener(this);
		next.setLayoutParams(secondCol);
		grid.addView(next, secondCol);
		
		layout.addView(grid);
	}
	
	@SuppressWarnings("deprecation")
	private void createAnswerRadio(LinearLayout layout, Answer answer){
		
		AnswerRadio answerRadio = (AnswerRadio)answer;
		List<Option> options  = answerRadio.getOptions();
		
		RadioGroup radioGroup = new RadioGroup(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        	     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 0, 5, 0);   
        
	   for(Option option: options){
			RadioButton radio = new RadioButton(this);
			radio.setText(option.getDescription());
			radio.setTextColor(Color.BLACK);
			radio.setId(option.getId()+VIEW_ID_RADIO);
			radio.setChecked(option.getId() == answerRadio.getValue());
			radio.setButtonDrawable(R.drawable.radiobutton_selector);
			radio.setEnabled(!question.isReadOnly());
			radioGroup.addView(radio);
		}
		layout.addView(radioGroup, layoutParams);
	}
	
	@SuppressWarnings("deprecation")
	private void createAnswerCheck(LinearLayout layout, Answer answer){
		
		AnswerCheck answerCheck = (AnswerCheck)answer;
		List<Option> options  = answerCheck.getOptions();
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
       	     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(5, 0, 5, 0);
		
		for(Option option: options){
			CheckBox check = new CheckBox(this);
			check.setText(option.getDescription());
			check.setTextColor(Color.BLACK);
			check.setId(option.getId());
			check.setEnabled(!question.isReadOnly());
			check.setChecked(answerCheck.getValues().contains(option.getId()));
			check.setButtonDrawable(R.drawable.checkbox_selector);
			layout.addView(check, layoutParams);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void createAnswerText(LinearLayout layout, Answer answer){
		
		AnswerText answerText = (AnswerText)answer;
		
		EditText answerField = new EditText(this);
		answerField.setId(VIEW_ID_TEXT);
		answerField.setLines(3);
		answerField.setBackgroundColor(Color.LTGRAY);
		answerField.setTextColor(Color.BLACK);
		answerField.setText(answerText.getValue());
		answerField.setEnabled(!question.isReadOnly());
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
          	     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
		layout.addView(answerField, layoutParams);
	}
	
	@SuppressWarnings("deprecation")
	private void createAnswerPhoto(LinearLayout layout, Answer answer){
		
		answerImage = answer;
		
		buttonPhoto = new Button(this);
		buttonPhoto.setText(R.string.take_photo);
		buttonPhoto.setOnClickListener(this);
		buttonPhoto.setTextColor(Color.BLACK);
		buttonPhoto.setEnabled(!question.isReadOnly());
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
         	     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
		layout.addView(buttonPhoto, layoutParams);
	}
	
	@SuppressWarnings("deprecation")
	private void createAnswerSchema(LinearLayout layout, Answer answer){
		
		answerImage = answer;
		
		buttonScheme = new Button(this);
		buttonScheme.setText(R.string.take_schema);
		buttonScheme.setOnClickListener(this);
		buttonScheme.setTextColor(Color.BLACK);
		buttonScheme.setEnabled(!question.isReadOnly());
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        	     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(10, 10, 10, 10);
		layout.addView(buttonScheme, layoutParams);
	}

	@Override
	public void onClick(View v) {
		
		Button button = (Button)v;
		String text = button.getText().toString();
		
		if(text.equals(getResources().getString(R.string.take_photo))){
			Intent i = new Intent(this, PhotoActivity.class);
			startActivityForResult(i, CAMERA);
		}else if(text.equals(getResources().getString(R.string.take_schema))){
			//TODO
		}else if(text.equals(getResources().getString(R.string.save))){
			
			if(deadline != null){
				deadline.cancel(true);
			}
			
			save(false);
			
		}else if(text.equals(getResources().getString(R.string.next))){
			Intent preIntent = new Intent(this,
    				QuestionActivity.class);
			QuizController quizController = new QuizController();
			Question next = quizController.findNextQuestion(quiz, question);
			preIntent.putExtra("question", next);
			preIntent.putExtra("quiz", quiz);
			preIntent.putExtra("userId", idUser);
    		startActivity(preIntent);
		}else if(text.equals(getResources().getString(R.string.previous))){
			Intent preIntent = new Intent(this,
    				QuestionActivity.class);
			QuizController quizController = new QuizController();
			Question previous = quizController.findPreviousQuestion(quiz, question);
			preIntent.putExtra("question", previous);
			preIntent.putExtra("quiz", quiz);
			preIntent.putExtra("userId", idUser);
    		startActivity(preIntent);
		}
	}
	
	private void getValueText(Answer answer){
		AnswerText answerText = (AnswerText)answer;
		TextView view = (TextView)findViewById(VIEW_ID_TEXT);
		answerText.setValue(view.getText().toString());
	}
	
	
	private void getValueRadio(Answer answer){
		
		AnswerRadio answerRadio = (AnswerRadio)answer;
		boolean find = false;
		long value = 0;
		Iterator<Option> it = answerRadio.getOptions().iterator();
		while(!find && it.hasNext()){
			
			Option option = it.next();
			RadioButton radio = (RadioButton)findViewById(option.getId()+VIEW_ID_RADIO);
			find = radio.isChecked();
			value = radio.getId()-VIEW_ID_RADIO;
		}
		
		answerRadio.setValue((int)value);
	}
	
	private void getValueCheck(Answer answer){
		
		AnswerCheck answerCheck = (AnswerCheck)answer;
		answerCheck.getOptions().clear();
		
		for(Option option: answerCheck.getOptions()){
			
			CheckBox checkBox = (CheckBox)findViewById(option.getId());
			if(checkBox.isChecked()){
				answerCheck.getValues().add(option.getId());
			}
		}
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			 if (requestCode == CAMERA) {
				  String path = data.getExtras().getString("PATH_PHOTO");
				  
				  if(answerImage instanceof AnswerSchema){
					  ((AnswerSchema)answerImage).setPath(path);
				  }else{
					  ((AnswerPhoto)answerImage).setPath(path);
				  }  
			}
		}
	}
	
	public void toReadOnly(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.timeover)
		       .setCancelable(false)
		       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   QuestionActivity.this.save(true);
		        	   QuestionActivity.this.disableButtons();
		           }
		       })
		       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		                QuestionActivity.this.disableButtons();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void disableButtons(){
		
		if(buttonPhoto!=null){
			buttonPhoto.setEnabled(false);
			buttonPhoto.setTextColor(Color.LTGRAY);
		}
		
		if(buttonScheme!=null){
			buttonScheme.setEnabled(false);
			buttonScheme.setTextColor(Color.LTGRAY);
		}
		
		if(save!=null){
			save.setEnabled(false);
			save.setTextColor(Color.LTGRAY);
		}
	}
	
	public void save(boolean readOnly){
		QuizController quizController = new QuizController();
		question.setReadOnly(readOnly);
		
		String message = 
	       	String.format(getResources().getString(R.string.log_question_answer)+" %s", idUser+"", question.getIdQuestion()+"");
	    quizController.addLogUser(this, message, idUser);
		
		for(Answer answer: question.getAnswers()){
			switch(answer.getTypeAnswer()){
				case TYPE_ANSWER_RADIO:{
					getValueRadio(answer);
				};break;
				case TYPE_ANSWER_TEXT:{
					getValueText(answer);
				};break;
				case TYPE_ANSWER_CHECK:{
					getValueCheck(answer);
				};break;
			}
		}
		//if (quiz.getState()==StateQuiz.IN_PROGRESS) {
			quizController.saveQuestion(this, quiz, question, idUser);
		//}
	}
}
