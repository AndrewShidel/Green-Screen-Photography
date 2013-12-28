// PictureTaker.java
// Activity for taking a picture with the device's camera
package andrew.green;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.hardware.*;
import android.hardware.Camera.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import android.hardware.Camera;

public class PictureTaker extends Activity
{
   private static final String TAG = "PICTURE_TAKER"; // for logging errors
   private int a;
   private int h,w;
   boolean portrait;

   private SurfaceView surfaceView; // used to display camera preview     
   private SurfaceHolder surfaceHolder; // manages the SurfaceView changes
   private boolean isPreviewing; // is the preview running?

   private Camera camera; // used to capture image data
   private List<String> effects; // supported color effects for camera
   private List<Camera.Size> sizes; // supported preview sizes for camera
   private String effect = Camera.Parameters.EFFECT_NONE; // default effect
   
   // called when the activity is first created
   @Override
   public void onCreate(Bundle bundle) 
   {
      super.onCreate(bundle); 
      setContentView(R.layout.camera_preview); // set the layout
      
      // initialize the surfaceView and set its touch listener
      surfaceView = (SurfaceView) findViewById(R.id.cameraSurfaceView);
      surfaceView.setOnTouchListener(touchListener);                   
      
      // initialize surfaceHolder and set object to handles its callbacks
      surfaceHolder = surfaceView.getHolder();   
      surfaceHolder.addCallback(surfaceCallback);
	  
	   Bundle extras = getIntent().getExtras();
	   a=extras.getInt("a");

      // required before Android 3.0 for camera preview
      surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
	  
   } // end method onCreate
    @Override
	public void onConfigurationChanged(Configuration con){
		super.onConfigurationChanged(con);
		if (con.orientation==Configuration.ORIENTATION_LANDSCAPE){
			Log.d("a",con.toString());
			camera.setDisplayOrientation(0);
			
		}
	    
	}
   

   // create the Activity's menu from list of supported color effects
   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {
      super.onCreateOptionsMenu(menu);

      // create menu items for each supported effect
      for (String effect : effects){
         menu.add(effect); 
		 
      }
      return true;
   } // end method onCreateOptionsMenu
   
   	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
			Intent intent = new Intent(getBaseContext(), MainActivity.class);

			startActivity(intent);
			finish();
		}
		return true;
	}
   // handle choice from options menu
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      Camera.Parameters p = camera.getParameters(); // get parameters  
      p.setColorEffect(item.getTitle().toString()); // set color effect
	  
      camera.setParameters(p); // apply the new parameters             
      return true;
   } // end method onOptionsItemSelected

   // handles SurfaceHolder.Callback events
   private SurfaceHolder.Callback surfaceCallback =
      new SurfaceHolder.Callback()                 
      {
         // release resources after the SurfaceView is destroyed
         @Override
         public void surfaceDestroyed(SurfaceHolder arg0)
         {
            camera.stopPreview(); // stop the Camera preview
            isPreviewing = false;
            camera.release(); // release the Camera's Object resources
         } // end method surfaceDestroyed
         
         // initialize the camera when the SurfaceView is created
         @Override
         public void surfaceCreated(SurfaceHolder arg0)
         {
            // get camera and its supported color effects/preview sizes
            camera = Camera.open(); // defaults to back facing camera   
            effects = camera.getParameters().getSupportedColorEffects();
			 
            sizes = camera.getParameters().getSupportedPreviewSizes();  
			
         } // end method surfaceCreated
         
         @Override
         public void surfaceChanged(SurfaceHolder holder, int format,
            int width, int height)                                   
         {
			 
			w=width;
			h=height;
			
			if (height>=width){
				portrait=true;
			}else{
				portrait=false;
			}
            if (isPreviewing) // if there's already a preview running
               camera.stopPreview(); // stop the preview
      
            // configure and set the camera parameters
            setPrefs(false);
      
            try 
            {
               camera.setPreviewDisplay(holder); // display using holder
			  
            } // end try
            catch (IOException e) 
            {
               Log.v(TAG, e.toString());
            } // end catch
      
            camera.startPreview(); // begin the preview
            isPreviewing = true;
			
         } // end method surfaceChanged
		 
		 public void setPrefs(boolean flash){
			 Camera.Parameters p = camera.getParameters();               
			 p.setPreviewSize(sizes.get(0).width, sizes.get(0).height);  
			 
			 // if(getRequestedOrientation()==1) p.set("rotation", 450); 
			 if (getResources().getConfiguration().orientation==getResources().getConfiguration().ORIENTATION_PORTRAIT){
				 camera.setDisplayOrientation(90);
				 Log.d("a","rotated");
				 }
				 
		     if (getResources().getConfiguration().orientation==getResources().getConfiguration().ORIENTATION_LANDSCAPE)
				 
			 if (flash){
				 p.setFlashMode( Camera.Parameters.FLASH_MODE_TORCH);
				 }else{
					 p.setFlashMode( Camera.Parameters.FLASH_MODE_OFF);
				 }
			 p.setColorEffect(effect); // use the current selected effect
			 camera.setParameters(p); // apply the new parameters        
		 }
		 
	
		 
		 
		 
		 
      }; // end SurfaceHolder.Callback
	ShutterCallback shutterCallback = new ShutterCallback() { // <6>
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() { // <7>
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() { // <8>
		public void onPictureTaken(byte[] data, Camera camera) {
			byte[] bitmapdata=new byte[data.length];
			String fileName;
			int orientation=getRequestedOrientation();
			Log.d("a","orientation"+String.valueOf(orientation));
			if (a==1) {
				fileName = "temp1" + System.currentTimeMillis();
				}else{
					fileName= "temp2" + System.currentTimeMillis();
				}
			
		if (portrait){
			final BitmapFactory.Options options = new BitmapFactory.Options(); 
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data,0,data.length);

           options.inSampleSize = Converter.calculateInSampleSize(options, 1000, 1000);

           options.inJustDecodeBounds = false;
			
			Bitmap b=BitmapFactory.decodeByteArray(data,0,data.length,options);
			int bw=b.getWidth();
			int bh=b.getHeight();
			
			Matrix m=new Matrix();
			m.postRotate(90);
			b=Bitmap.createBitmap(b,0,0,bw,bh,m,true);
			
		
           //bitmap.getPixels(i,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
		  
		   data=null;
		   System.gc();
		   if (h>=w){
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			bitmapdata = stream.toByteArray();
			
			}
			}
//			int count=0;
//			while (count<i.length){
//				data[count]=(byte) i[count];
//				count++;
//			}
			
			
			
			// create a ContentValues and configure new image's data
			ContentValues values = new ContentValues();
			values.put(Images.Media.TITLE, fileName);
			values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
			values.put(Images.Media.MIME_TYPE, "image/jpg");
			

			// get a Uri for the location to save the file
			Uri uri = getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
            
			Uri uri2=Images.Media.EXTERNAL_CONTENT_URI;
			SharedPreferences pref = getBaseContext().getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
			pref.edit().putString("uri2", uri2.toString()).commit();

			try 
			{
				// get an OutputStream to uri
				OutputStream outStream = 
					getContentResolver().openOutputStream(uri);
				
				if (portrait)outStream.write(bitmapdata); // output the image
				if (!portrait)outStream.write(data);
				outStream.flush(); // empty the buffer
				outStream.close(); // close the stream
				
                if (a==1){
					SharedPreferences prefs = getBaseContext().getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
					prefs.edit().putString("pic1", uri.toString()).commit();
					Intent intent=new Intent(PictureTaker.this,MainActivity.class);
					Log.d("a","intent made");
					message.show();
					intent.putExtra("uri",uri.toString());
				
					//intent.putExtra("rawB",data);
					Log.d("a","saved successfully "+String.valueOf(a));
					startActivity(intent); 
					finish();
				}
				else{
					
					
					Intent intent=new Intent(PictureTaker.this,Pic1View.class);
					Log.d("a","intent made");
					intent.putExtra("uri",uri.toString());
					intent.putExtra("source",2);
				//	intent.putExtra("raw",data);
					Log.d("a","saved successfully "+String.valueOf(a));
					message.show();
					startActivity(intent); 
					finish();
				}
				
			} // end try
			catch (IOException ex) 
			{
				Log.d("a","error saving the picture.  returning to original activity");
				setResult(RESULT_CANCELED); // error taking picture
               
				// display a message indicating that the image was saved
				Toast message = Toast.makeText(PictureTaker.this,"Sorry, could not save image", Toast.LENGTH_SHORT);
				message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
				message.show(); // display the Toast
				finish();
			} // end catch
			
		} // end method onPictureTaken
	};
	
	
	private String mScreenshotPath = Environment.getExternalStorageDirectory() + "/greenscreen";

	public void saveScreenshot(Bitmap bitmap, Uri uri) {

		if (ensureSDCardAccess()) {


			File file = new File(uri.toString());
			

			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.close();
				Log.d("a", "File saved");
			} catch (FileNotFoundException e) {
				Log.d("a", "FileNotFoundException");
			} catch (IOException e) {
				Log.d("a", "IOEception");
			}
		}

		

	}

	private boolean ensureSDCardAccess() {
		File file = new File(mScreenshotPath);
		if (file.exists()) {
			return true;
		} else if (file.mkdirs()) {
			return true;
		}
		Log.d("a","no sd card access");
		return false;
	}
	
	
   // handles Camera callbacks
   Camera.PictureCallback pictureCallback = new Camera.PictureCallback()
   {
      // called when the user takes a picture
      public void onPictureTaken(byte[] imageData, Camera c)
      {
         // use "Slideshow_" + current time in ms as new image file name
         String fileName = "Slideshow_" + System.currentTimeMillis();
   
         // create a ContentValues and configure new image's data
         ContentValues values = new ContentValues();
         values.put(Images.Media.TITLE, fileName);
         values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
         values.put(Images.Media.MIME_TYPE, "image/jpg");
   
         // get a Uri for the location to save the file
         Uri uri = getContentResolver().insert(
            Images.Media.EXTERNAL_CONTENT_URI, values);
	
	
		  
         try 
         {
            // get an OutputStream to uri
            OutputStream outStream = 
               getContentResolver().openOutputStream(uri);
            outStream.write(imageData); // output the image
            outStream.flush(); // empty the buffer
            outStream.close(); // close the stream
            
            // Intent for returning data to SlideshowEditor
            Intent returnIntent = new Intent();
            returnIntent.setData(uri); // return Uri to SlideshowEditor 
            setResult(RESULT_OK, returnIntent); // took pic successfully
   
            // display a message indicating that the image was saved
            Toast message = Toast.makeText(PictureTaker.this, 
               "Picture Saved!", Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
               message.getYOffset() / 2);
            message.show(); // display the Toast
			
			 if (a==1){
			 Intent intent=new Intent(PictureTaker.this,Pic1View.class);
			 Log.d("a","intent made");
             intent.putExtra("uri",uri.toString());
			 intent.putExtra("source",5);
			 intent.putExtra("raw",imageData);
			 startActivity(intent); 
			 }
          finish(); // finish and return to SlideshowEditor
         } // end try
         catch (IOException ex) 
         {
            setResult(RESULT_CANCELED); // error taking picture

            // display a message indicating that the image was saved
            Toast message = Toast.makeText(getBaseContext(), "Sorry, could not save image", Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
         } // end catch
      } // end method onPictureTaken
   }; // end pictureCallback

   // takes picture when user touches the screen
   private OnTouchListener touchListener = new OnTouchListener()
   {
      @Override
      public boolean onTouch(View v, MotionEvent event) 
      {
         // take a picture
       // camera.takePicture(shutterCallback, rawCallback, jpegCallback);
	      
		  camera.takePicture(null, null, jpegCallback);
         return false;
      } // end method onTouch
   }; // end touchListener
} // end class PictureTaker





