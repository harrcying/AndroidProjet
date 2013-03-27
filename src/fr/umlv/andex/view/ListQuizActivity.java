package fr.umlv.andex.view;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.umlv.andex.R;
import fr.umlv.andex.controller.QuizController;
import fr.umlv.andex.controller.ShiftQuizService;
import fr.umlv.andex.data.QuizDescription;

public class ListQuizActivity extends Activity implements OnItemClickListener{

	private QuizDescription[] table;
	private long idQuizSelected;
	private long idUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listquiz);

		idUser = (Long)getIntent().getExtras().get("userId");

		ListView listView = (ListView) findViewById(R.id.listquiz);
		QuizController quizController = new QuizController();

		String message = 
				String.format(getResources().getString(R.string.log_list_view), idUser+"");
		quizController.addLogUser(this, message, idUser);

		List<QuizDescription> listQuiz = quizController.findAllQuiz();

		table = (QuizDescription[]) listQuiz.toArray(new QuizDescription[listQuiz.size()]); 

		ArrayAdapter<QuizDescription> adapter = new ArrayAdapter<QuizDescription>(this,
				R.layout.list_custom_item, R.id.list_item, table);

		listView.setAdapter(adapter); 
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

		QuizDescription quizDescription = table[position];
		idQuizSelected = quizDescription.getIdQuiz();
		viewExam();
	}

	private void viewExam(){

		QuizController quizController = new QuizController();
		String message = 
				String.format(getResources().getString(R.string.log_exam_view)+" %s", idUser+"", idQuizSelected+"");
		quizController.addLogUser(this, message, idUser);

		Intent preIntent = new Intent(this,TreeActivity.class);
		preIntent.putExtra("idQuiz", idQuizSelected);
		preIntent.putExtra("userId", idUser);
		startActivity(preIntent);
		
		Intent intentService = new Intent(this, ShiftQuizService.class);
		intentService.putExtra("userId", idUser);
		intentService.putExtra("idQuiz", idQuizSelected);
		startService(intentService); 
	}
}
