package andrew.green;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import tucker.shidel.greenscreen.R;

public class MainActivity extends Activity
{
	SharedPreferences preferences;
	String s;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Button tb=(Button) findViewById(R.id.tb);
		TextView t1=(TextView) findViewById(R.id.t1);
		TextView t2=(TextView) findViewById(R.id.t2);
		t1.setText(getResources().getString(R.string.title2));
		t2.setText(getResources().getString(R.string.text2));
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			s = extras.getString("uri");
		}
	//	String uri=preferences.getString("uri","empty");

//		Toast message = Toast.makeText(MainActivity.this,uri, Toast.LENGTH_SHORT);
//		message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
//						   message.getYOffset() / 2);
//		message.show(); // display the Toast
//     
    }

	public void takePicture(View view){
	  
		Log.d("a","clicked");
		Intent intent=new Intent(MainActivity.this,PictureTaker.class);
		Log.d("a","intent made");
		intent.putExtra("a",2);
		intent.putExtra("uri",s);
		startActivity(intent); 
		Log.d("a","activity started");

		
	}
	public void getImage(View view){
		Intent intent = new Intent(getBaseContext(), FileDialog.class); intent.putExtra(FileDialog.START_PATH, "/sdcard");

        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
		intent.putExtra("from",2);
		intent.putExtra("uri",s);
		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

//alternatively you can set file filter //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

	//	startActivityForResult(intent, 0);
		startActivity(intent);
	}
	@Override
	protected final void onActivityResult(int requestCode, int resultCode, 
										  Intent data)
	{
	
		
		Log.d("a","onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { 
		      Log.d("a","ok");
		      Uri imageUri = data.getData(); 
			try
			{
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
				Log.d("a","bitmap created");
				
			}
			catch (IOException e)
			{}
			
		} 
			  }
}
