package andrew.green;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import tucker.shidel.greenscreenphotography.R;

public class done extends Activity {
	Uri uri;
	String s;
	SharedPreferences preferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.done);
		SharedPreferences prefs = this.getSharedPreferences("com.example.app",
				Context.MODE_PRIVATE);
		s = prefs.getString("file", "uri");
		uri = Uri.parse(s);
		ImageView i = (ImageView) findViewById(R.id.image);
		i.setImageURI(uri);

	}

	public void send(View view) {
		initShareIntent("message");

		/*
		 * Intent intent = new Intent(Intent.ACTION_VIEW);
		 * 
		 * intent.putExtra("sms_body", "Sent from Green Screen Photography.");
		 * intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(s)));
		 * intent.setType("vnd.android-dir/mms-sms"); startActivity(intent);
		 */

		/*
		 * Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		 * sendIntent.putExtra("sms_body", "Content of the SMS goes here...");
		 * sendIntent.setType("vnd.android-dir/mms-sms");
		 * sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(s)));
		 * startActivity(sendIntent);
		 */
	}

	public void email(View view) {
		Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mms://"));
		intent.putExtra("sms_body", "Sent from Green Screen Photography.");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(s)));
		intent.setType("vnd.android-dir/mms-sms");
		startActivity(intent);

	}

	public void addlayer(View view) {
		Intent intent = new Intent(getBaseContext(), Pic1View.class);
		Log.d("a", "intent made");
		intent.putExtra("uri", uri.toString());
		intent.putExtra("source", 1);

		Log.d("a", "saved successfully");

		startActivity(intent);
		finish();
	}

	public void set(View view) {
		try {
			getApplicationContext().setWallpaper(BitmapFactory.decodeFile(s));
			Toast.makeText(getBaseContext(), "Background Set",
					Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(getBaseContext(), "Background could not be set",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void fb(View view) {
		initShareIntent("face");

		/*
		 * Intent intent=new
		 * Intent(Intent.ACTION_SEND,Uri.parse("https://www.facebook.com"));
		 * intent.putExtra("sms_body", "Sent from Green Screen Photography.");
		 * intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(s)));
		 * intent.setType("image/jpeg");
		 * startActivity(Intent.createChooser(intent,"Send"));
		 */

		/*
		 * String fullUrl = "https://m.facebook.com/sharer.php?u=.."; try {
		 * Intent intent = new Intent(Intent.ACTION_SEND);
		 * intent.setClassName("com.facebook.katana"
		 * ,"com.facebook.katana.ShareLinkActivity");
		 * 
		 * intent.putExtra("sms_body", "Sent from Green Screen Photography.");
		 * intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(s)));
		 * //intent.setType("image/jpeg");
		 * 
		 * startActivity(intent);
		 * 
		 * } catch (Exception e) { Intent i = new Intent(Intent.ACTION_VIEW);
		 * i.setData(Uri.parse(fullUrl)); startActivity(i);
		 * 
		 * }
		 */
	}

	public void twitter(View view) {
		initShareIntent("twi");
	}

	public void instagram(View view) {
		initShareIntent("insta");
	}

	public void pintrest(View view) {
		initShareIntent("pint");
	}

	public void open(View view) {
		// initShareIntent("gal");
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setType("image/jpeg");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(s)));
		startActivity(Intent.createChooser(intent, "Select"));

	}

	public void retry(View view) {
		Intent intent = new Intent(getBaseContext(), GetBackground.class);
		startActivity(intent);
		finish();
	}

	public void rate(View view) {
		Uri uri = Uri.parse("market://details?id="
				+ getBaseContext().getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getBaseContext(), "Couldn't launch the market",
					Toast.LENGTH_LONG).show();
		}
	}

	public void exit(View view) {
		Toast.makeText(getBaseContext(), "Thank You!", Toast.LENGTH_SHORT)
				.show();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void initShareIntent(String type) {
		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND);

		share.setType("image/jpeg");

		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type)
						|| info.activityInfo.name.toLowerCase().contains(type)) {
					share.putExtra(Intent.EXTRA_SUBJECT,
							"Green Screen Photography");
					share.putExtra(Intent.EXTRA_TEXT,
							"This image was sent from the android app Green Screen Photography");
					share.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(s))); // Optional, just if you
														// wanna share an image.
					share.setPackage(info.activityInfo.packageName);
					found = true;
					break;
				}
			}
			if (!found) {
				if (type == "twi")
					Toast.makeText(getBaseContext(),
							"Could not find the twitter app",
							Toast.LENGTH_SHORT).show();
				if (type == "face")
					Toast.makeText(getBaseContext(),
							"Could not find the facebook app",
							Toast.LENGTH_SHORT).show();
				if (type == "insta")
					Toast.makeText(getBaseContext(),
							"Could not find the instagram app",
							Toast.LENGTH_SHORT).show();
				if (type == "pint")
					Toast.makeText(getBaseContext(),
							"Could not find the pintrest app",
							Toast.LENGTH_SHORT).show();
				return;
			}

			startActivity(Intent.createChooser(share, "Select"));
		}
	}
}
