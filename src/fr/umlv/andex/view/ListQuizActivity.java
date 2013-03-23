package fr.umlv.andex.view;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.umlv.andex.R;
import fr.umlv.andex.controller.QuizController;
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
		
    	try{
    		
    		QuizDescription quizDescription = table[position];
    		idQuizSelected = quizDescription.getIdQuiz();
    		
    		if(quizDescription.isInProgress()){
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setMessage(R.string.text_confirm)
    			       .setCancelable(false)
    			       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			        	   
    			        	   ListQuizActivity.this.takeExam();
    			        	   ListQuizActivity.this.finish();
    			           }
    			       })
    			       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			                dialog.cancel();
    			           }
    			       });
    			AlertDialog alert = builder.create();
    			alert.show();
    		}else{
    			viewExam();
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	} 	
	}
	
	private void takeExam(){
		
	    QuizController quizController = new QuizController();
 	    String quizFileName = "examC.xml"; // TODO (a partir du serveur)
 	    quizController.saveExam(this, quizFileName);
 	    
 	   String message = 
       	String.format(getResources().getString(R.string.log_exam_take)+" %s", idUser+"", idQuizSelected+"");
       quizController.addLogUser(this, message, idUser);
		
		Intent preIntent = new Intent(this, TreeActivity.class);
		preIntent.putExtra("idQuiz", idQuizSelected);
		preIntent.putExtra("userId", idUser);
		startActivity(preIntent);
	}
	
	private void viewExam(){
		
		QuizController quizController = new QuizController();
		String message = 
	       	String.format(getResources().getString(R.string.log_exam_view)+" %s", idUser+"", idQuizSelected+"");
	    quizController.addLogUser(this, message, idUser);
			
		Intent preIntent = new Intent(this,
				TreeActivity.class);
		preIntent.putExtra("idQuiz", idQuizSelected);
		preIntent.putExtra("userId", idUser);
		startActivity(preIntent);
	}
}
