package andrew.green;

import android.app.*;
import android.content.*;
import android.os.*;
import tucker.shidel.greenscreen.R;

public class Settings extends Activity {
	SharedPreferences preferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
	}
}
