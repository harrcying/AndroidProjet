package fr.umlv.andex.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import fr.umlv.andex.R;
import fr.umlv.andex.controller.LoginController;
import fr.umlv.andex.data.User;

public class LoginActivity extends Activity {
    /** Called when the activity is first created. */

	private LoginController loginController = new LoginController();
	private EditText nameUser;
	private EditText password;

	private boolean isWaiting;
	private User user = new User();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        nameUser = (EditText)findViewById(R.id.nameuser);
        password = (EditText)findViewById(R.id.password);
    }
    
    public void loginByUserAndPassword(View v){
    	
    	String passwordText = password.getText().toString();
    	String nameUserText = nameUser.getText().toString();
    	
    	
    	final String username = nameUserText;
    	final String password = passwordText;
    	
    	Runnable r = new Runnable() {
    		
    		@Override
			public void run() {
				try {
					synchronized (this) {	
						isWaiting = false;
						user = loginController.findUserByUserAndPassword(username, password);
						isWaiting=true;
						this.wait(); // On attends que le main ait bien vu que l'user a été crée.
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
		};
    	
    	Thread t = new Thread(r);
    	t.start();
    	
    	synchronized (r) {
			while(!isWaiting); // On attends que r ait fini de créer l'user
			r.notify(); // On dit a r que maintenant qu'on a vu qu'il avait fini de créer l'user il pouvait continuer
    	}

		try {
			t.join(); // On attends que la thread ait fini sont boulot et qu'elle meure.
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

    	if(user == null){
    		Toast.makeText(this, "Utilisateur ou Mot de pass incorrect.", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	try{
    		Intent preIntent = new Intent(v.getContext(),
    				ListQuizActivity.class);
    		preIntent.putExtra("userId", user.getUserId());
    		startActivity(preIntent);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
}