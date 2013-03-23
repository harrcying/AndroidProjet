package fr.umlv.andex.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import fr.umlv.andex.R;

public class PhotoActivity extends Activity implements SurfaceHolder.Callback {
	/** Called when the activity is first created. */
	private Camera camera;
	private SurfaceView surfaceCamera;
	private Boolean isPreview;
	private FileOutputStream stream;
	private Uri uri;
	private int indice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//supression bare de titre:
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// On met l'application en plein écran
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

		isPreview = false;
		setContentView(R.layout.photo);

		// Si vous voulez gérer plusieurs caméra sur votre mobile / tablette
		for (int nbCamera = 0; nbCamera < Camera.getNumberOfCameras();  nbCamera++) {
			Camera.CameraInfo informationsCamera = new Camera.CameraInfo();
			Camera.getCameraInfo(nbCamera, informationsCamera);

			if (informationsCamera != null) {
				switch (informationsCamera.facing) {
				case Camera.CameraInfo.CAMERA_FACING_FRONT:
					indice = nbCamera;
					break;
				case Camera.CameraInfo.CAMERA_FACING_BACK:
					indice = nbCamera;
					break;
				}
			}
		}
		
		// On recupere notre surface pour le preview
		surfaceCamera = (SurfaceView) findViewById(R.id.surfaceViewCamera);

		// Quand on clique sur notre surface
		surfaceCamera.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// On prend une photo
				if (camera != null) {
					SavePicture();
				}

			}
		});

		// Méthode d'initialisation de la caméra
		InitializeCamera();
	}

	@SuppressWarnings("deprecation")
	public void InitializeCamera() {
		// On attache nos retour du holder à notre activite
		surfaceCamera.getHolder().addCallback(this);

		// On spécifie le type du holder en mode SURFACE_TYPE_PUSH_BUFFERS
		surfaceCamera.getHolder().setType(
				SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// Retour sur l'application
	@Override
	public void onResume() {
		super.onResume();
		camera = Camera.open(indice);
	}

	// Mise en pause de l'application
	@Override
	public void onPause() {
		super.onPause();

		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	// Implémentation du surface holder

	// Quand la surface change
	public void surfaceChanged(SurfaceHolder holder, int format, int  width,
			int height) {

		// Si le mode preview est lance alors on le stop
		if (isPreview) {
			camera.stopPreview();
		}
		// On recupere les parametres de la camera
		Camera.Parameters parameters = camera.getParameters();

		// On change la taille
		//parameters.setPreviewSize(width, height);
		/**suprimer pour faire fonctionner l'appli*/

		// On applique nos nouveaux parametres
		camera.setParameters(parameters);

		try {
			// On attache notre previsualisation de la camera au holder de la  surface
			camera.setPreviewDisplay(surfaceCamera.getHolder());
		} catch (IOException e) {
		}

		// On lance la previeuw
		camera.startPreview();
		isPreview = true;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// On prend le controle de la camera
		if (camera == null)
			camera = Camera.open();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// On arrete la camera et on rend la main
		if (camera != null) {
			camera.stopPreview();
			isPreview = false;
			camera.release();
		}
	}

	// Callback pour la prise de photo
	Camera.PictureCallback pictureCallback = new  Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			if (data != null) {
				// Enregistrement de votre image
				try {
					if (stream != null) {
						stream.write(data);
						stream.flush();
						stream.close();}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// On redémarre la prévisualisation
				camera.startPreview();
				
				Intent intent = new Intent();
				intent.putExtra("PATH_PHOTO", getRealPathFromURI(uri));
				setResult(RESULT_OK, intent);
				finish();
				
			}
		}
	};

	@SuppressLint("SimpleDateFormat")
	private void SavePicture() {
		try {
			SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM- dd-HH.mm.ss");
			String fileName = "photo_" + timeStampFormat.format(new Date()) +  ".jpg";

			// Metadata pour la photo
			ContentValues values = new ContentValues();
			values.put(Media.TITLE, fileName);
			values.put(Media.DISPLAY_NAME, fileName);
			values.put(Media.DESCRIPTION, "Image prise par FormationCamera");
			values.put(Media.DATE_TAKEN, new Date().getTime());
			values.put(Media.MIME_TYPE, "image/jpeg");

			// Support de stockage
			uri =  getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);

			// Ouverture du flux pour la sauvegarde
			stream = (FileOutputStream)getContentResolver().openOutputStream(uri);
			camera.takePicture(null, pictureCallback, pictureCallback);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("deprecation")
	public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
