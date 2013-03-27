package fr.umlv.andex.controller;

import java.util.Timer;
import java.util.TimerTask;

import fr.umlv.andex.R;
import fr.umlv.andex.view.ListQuizActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ShiftQuizService extends Service {

	private Timer timer ;
	private long idUser;
	private long idQuiz;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
	    super.onCreate();
	    timer = new Timer();
	    Log.d(this.getClass().getName(), "onCreate");
	} 

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStart(intent, startId);
		Log.d("Andex Log", "Service Quiz started");
		idUser = (Long) intent.getExtras().get("userId");
		idQuiz = (Long) intent.getExtras().get("idQuiz");
		
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				validate();
			}
		}, 0, 60000); 
		

		return START_NOT_STICKY;
	}

	private void validate(){
		
		QuizController quizController = new QuizController();

		//TODO ajouter le while infinit
		if (quizController.isShiftQuiz(idQuiz)) {

			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			String menssage = getResources().getString(R.string.notification);
			Notification notification = new Notification(
					R.drawable.ic_launcher, menssage,
					System.currentTimeMillis());

			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			Intent preIntent = new Intent(this, ListQuizActivity.class);
			preIntent.putExtra("userId", idUser);

			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					preIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

			notification.setLatestEventInfo(this, menssage, menssage,
					pendingIntent);
			notificationManager.notify(1, notification);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("Andex Log", "Service Quiz onDestroy");
	}

}
