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

public class GetBackground extends Activity
{
	SharedPreferences preferences;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		


		//	String uri=preferences.getString("uri","empty");

//		Toast message = Toast.makeText(MainActivity.this,uri, Toast.LENGTH_SHORT);
//		message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
//						   message.getYOffset() / 2);
//		message.show(); // display the Toast
//     
    }

	public void takePicture(View view){

		Log.d("a","clicked");
		Intent intent=new Intent(GetBackground.this,PictureTaker.class);
		Log.d("a","intent made");
		intent.putExtra("a",1);

		startActivity(intent); 
		Log.d("a","activity started");


	}
	public void getImage(View view){
		Intent intent = new Intent(getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, "/sdcard");

        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
		intent.putExtra("from",1);
		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

//alternatively you can set file filter //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

		//	startActivityForResult(intent, 0);
		startActivity(intent);
	}

}
