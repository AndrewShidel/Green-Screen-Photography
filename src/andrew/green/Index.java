package andrew.green;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import tucker.shidel.greenscreenphotography.R;

public class Index extends Activity {
	SharedPreferences preferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index);

	}

	public void getVideo(View view) {

		Intent intent = new Intent(getBaseContext(), VideoCapture.class);

		startActivity(intent);

	}

	public void getImage(View view) {
		Intent intent = new Intent(getBaseContext(), MainActivity.class);

		startActivity(intent);
	}

}
