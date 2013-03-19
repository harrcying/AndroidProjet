package fr.umlv.andex.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.AsyncTask;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class DeadlineTask  extends AsyncTask{

	private TextView timeView;
	private QuestionActivity activity;
	
	public QuestionActivity getActivity() {
		return activity;
	}

	public void setActivity(QuestionActivity activity) {
		this.activity = activity;
	}

	public TextView getTimeView() {
		return timeView;
	}

	public void setTimeView(TextView timeView) {
		this.timeView = timeView;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object doInBackground(Object... args) {
		
		long time = (Long)args[0];
		long deadline = System.currentTimeMillis() + time; 
			
		while(time>1000){			
			time = deadline - System.currentTimeMillis();
			publishProgress(time);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
		
		//Hola
		SimpleDateFormat format = new SimpleDateFormat("mm:ss");
		long time = (Long)values[0];
		Date date = new Date(time);
		timeView.setText(format.format(date));
	}

	@Override
	protected void onPostExecute(Object result) {
		timeView.setText("Fin");
		activity.toReadOnly();
	}
}
