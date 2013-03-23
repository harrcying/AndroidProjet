package fr.umlv.andex.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.andex.R;
import fr.umlv.andex.controller.QuizController;
import fr.umlv.andex.data.NodeQuestion;
import fr.umlv.andex.data.Quiz;
import fr.umlv.andex.data.StateQuiz;

public class TreeActivity extends Activity implements View.OnClickListener{

	private Quiz quiz;
	private long idUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		QuizController quizController = new QuizController();
		idUser = (Long)getIntent().getExtras().get("userId");
		Long idQuiz = (Long)getIntent().getExtras().get("idQuiz");
		quiz = quizController.getQuiz(idQuiz);

		String message = getResources().getString(R.string.log_exam_question_list);
		String messageFormat = String.format(message, idUser+"");
		quizController.addLogUser(this, messageFormat, idUser);

		if (quiz.getState()==StateQuiz.IN_PROGRESS) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			System.out.println("coucou");
			builder.setMessage(R.string.text_confirm)
			.setCancelable(false)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					LinearLayout layout = makeLayout();
					addTree(layout, quiz.getTree().getNodes(), "-");
				}
			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//dialog.cancel();
					System.out.println("4");
					Intent preIntent = new Intent(TreeActivity.this,ListQuizActivity.class);
					preIntent.putExtra("userId", idUser);
					startActivity(preIntent);
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			System.out.println("2");
		} else {
			LinearLayout layout = makeLayout();
			addTree(layout, quiz.getTree().getNodes(), "-");
		}

	}

	private void addTree(LinearLayout layout, List<NodeQuestion> nodes, String prefix){

		System.out.println("3");

		for(NodeQuestion node :nodes){

			TextView item = new TextView(this);
			item.setText(prefix + node.getTitle());
			item.setId(node.getId());
			item.setOnClickListener(this);
			item.setTextSize(22);
			item.setTextColor(Color.BLACK);
			item.setBackgroundColor(Color.WHITE);
			item.setPadding(7, 7, 7, 7);

			@SuppressWarnings("deprecation")
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 0, 1);
			layout.addView(item, layoutParams);

			if(node.isOpen()){
				addTree(layout, node.getNodes(), "	" + prefix);
			}
		} 
		Button save = new Button(this);
		save.setText(R.string.save);
		save.setTextColor(Color.BLACK);
		save.setOnClickListener(this);
		layout.addView(save);
	}

	@SuppressLint("SimpleDateFormat")
	private void searchItem(List<NodeQuestion> list, int id){

		boolean find = false;
		Iterator<NodeQuestion> it = list.iterator();
		while(!find && it.hasNext()){

			NodeQuestion item = it.next();
			if(item.getId() == id){
				find = true;
				if(!item.isLeaf()){
					item.setOpen(!item.isOpen());
				}else{

					if(item.getTime()>0){

						SimpleDateFormat format = new SimpleDateFormat("mm:ss");
						Date date = new Date(item.getTime());

						String message = 
								getResources().getString(R.string.text_time) + " "+format.format(date);

						Toast messageView = Toast.makeText(this, message, Toast.LENGTH_SHORT);
						messageView.show();
					}

					System.out.println("item");
					System.out.println(item.getTitle());
					System.out.println(item.getQuestion());

					Intent preIntent = new Intent(this, QuestionActivity.class);
					preIntent.putExtra("question", item.getQuestion());
					preIntent.putExtra("quiz", quiz);
					preIntent.putExtra("userId", idUser);
					startActivity(preIntent);
				}

			}else{
				searchItem(item.getNodes(), id);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v instanceof Button) {
			QuizController quizController = new QuizController();
		    quizController.saveExam(this, quiz, idUser);
		    Intent preIntent = new Intent(TreeActivity.this,ListQuizActivity.class);
			preIntent.putExtra("userId", idUser);
			startActivity(preIntent);
		} else {
			searchItem(quiz.getTree().getNodes(), v.getId());
			LinearLayout layout = makeLayout();
			addTree(layout, quiz.getTree().getNodes(), "-");
		}
	}

	private LinearLayout makeLayout(){

		LinearLayout layout = new LinearLayout(this); 
		layout.setBackgroundColor(Color.WHITE);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);

		TextView title = new TextView(this);
		title.setText(R.string.title_quiz);
		title.setTextColor(Color.BLACK);
		title.setTextSize(30);
		title.setPadding(0, 5, 0, 5);

		layout.addView(title);

		TextView description = new TextView(this);
		description.setText(quiz.getDescription());
		description.setTextColor(Color.BLACK);
		description.setTextSize(22);
		description.setPadding(0, 5, 0, 5);

		layout.addView(description);

		GridLayout gridStatus = new GridLayout(this);
		gridStatus.setColumnCount(2);

		TextView statusTitle = new TextView(this);
		statusTitle.setText(R.string.quiz_status);
		statusTitle.setTextColor(Color.BLACK);
		statusTitle.setTypeface(null,Typeface.BOLD);
		statusTitle.setPadding(0, 0, 5, 0);
		gridStatus.addView(statusTitle);

		TextView status = new TextView(this);
		status.setText(quiz.getState().name());
		status.setTextColor(Color.BLACK);
		gridStatus.addView(status);

		layout.addView(gridStatus);

		TextView subtitle = new TextView(this);
		subtitle.setText(R.string.subtitle_questions);
		subtitle.setTextColor(Color.BLACK);
		subtitle.setTextSize(22);
		subtitle.setTypeface(null,Typeface.BOLD);
		subtitle.setPadding(0, 5, 0, 5);

		layout.addView(subtitle);

		LinearLayout layoutList = new LinearLayout(this);
		layoutList.setBackgroundColor(Color.LTGRAY);
		layoutList.setOrientation(LinearLayout.VERTICAL);
		layoutList.setGravity(Gravity.CENTER);

		layout.addView(layoutList);
		return layoutList;
	}
}
