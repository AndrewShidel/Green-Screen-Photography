package andrew.green;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import java.io.*;
import tucker.shidel.greenscreen.R;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class Resize extends Activity {

	float dx = 0;
	float dy = 0;
	float x = 0;
	float y = 0;
	float predx = 0;
	float predy = 0;
	float w, h;
	float transX1 = 0;
	float transY1 = 0;
	float transX2 = 0;
	float transY2 = 0;
	float transX = 0;
	float transY = 0;
	boolean two = false;
	float th = 0;
	float th1 = 0;
	float th2 = 0;
	float focX, focY;
	float rI, rN;
	float tx1, ty1, tx2, ty2;
	int b3x, b3y;
	int cx, cy;
	boolean zero = false;

	Bitmap pic1, pic2, nbitmap, button3;
	String s, s2;
	boolean temp = true;
	ContentResolver r;
	BitmapFactory.Options o, o2;

	private static float MIN_ZOOM = .025f;
	private static float MAX_ZOOM = 31f;

	private float scaleFactor = 1.f;
	private ScaleGestureDetector detector;
	public GestureDetector listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("a", "activity resize started");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(new Panel(this));

		showDialog(1);

		r = this.getContentResolver();

		SharedPreferences prefs = this.getSharedPreferences("com.example.app",
				Context.MODE_PRIVATE);
		s = prefs.getString("pic1", "uri");
		o2 = new BitmapFactory.Options();
		o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
		s2 = prefs.getString("file", "uri");
		pic2 = BitmapFactory.decodeFile(s2, o2);
		Log.d("a", s);
		Uri uri = Uri.parse(s);
		o = new BitmapFactory.Options();

		try {
			// pic1 =BitmapFactory.decodeFile(s);
			pic1 = Converter.makeItFit(s);
			Log.d("a", "bitmap created");

		} catch (OutOfMemoryError e) {
			Log.d("a", "out of memory");
		} catch (NullPointerException ex) {
		}

		if (pic1 == null) {
			try {
				pic1 = MediaStore.Images.Media.getBitmap(r, uri);
				Log.d("a", "bitmap created 2");

			} catch (IOException e) {
				Log.d("a", "IOException");
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case 1:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.dt2));

			builder.setMessage(getResources().getString(R.string.dialog2))
					.setCancelable(false)

					.setNegativeButton(
							getResources().getString(R.string.click),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			break;
		case 2:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("Set Image Path");

			builder2.setMessage(
					"Set the image path by going to the desired folder, tapping new, and typing file name")
					.setCancelable(false)

					.setNegativeButton("Continue",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									saveScreenshot();
								}
							});

			AlertDialog alert2 = builder2.create();
			alert2.show();
			break;

		default:
			dialog = null;
		}
		return dialog;
	}

	private String mScreenshotPath = Environment.getExternalStorageDirectory()
			+ "/greenscreen";

	public void saveScreenshot() {

		if (ensureSDCardAccess()) {

			File file = new File(mScreenshotPath + "/"
					+ System.currentTimeMillis() + ".jpg");
			SharedPreferences prefs = getBaseContext().getSharedPreferences(
					"com.example.app", Context.MODE_PRIVATE);
			prefs.edit().putString("file", file.toString()).commit();

			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
				nbitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.close();
				Log.d("a", "File saved");
				
				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			    
				Uri contentUri = Uri.fromFile(file);
				mediaScanIntent.setData(contentUri);
				this.sendBroadcast(mediaScanIntent);
				
			} catch (FileNotFoundException e) {
				Log.d("a", "FileNotFoundException");
			} catch (IOException e) {
				Log.d("a", "IOEception");
			}
		}
		
		
		

		Intent intent = new Intent(getBaseContext(), done.class);
		startActivity(intent);
		finish();

	}

	private boolean ensureSDCardAccess() {
		File file = new File(mScreenshotPath);
		if (file.exists()) {
			return true;
		} else if (file.mkdirs()) {
			return true;
		}
		Log.d("a", "no sd card access");
		return false;
	}

	@Override
	protected final void onActivityResult(int requestCode, int resultCode,
			Intent data) {

		Log.d("a", "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Log.d("a", "ok");

		}
	}

	class Panel extends SurfaceView implements SurfaceHolder.Callback {
		private TutorialThread _thread;

		public Panel(Context context) {
			super(context);
			getHolder().addCallback(this);
			_thread = new TutorialThread(getHolder(), this);
			detector = new ScaleGestureDetector(getContext(),
					new ScaleListener());
			listener = new GestureDetector(getContext(), gestureListener);
		}

		@Override
		public void onDraw(Canvas canvas) {

			if (dst(dx, predx, dy, predy) > 20) {
				dx = predx;
				dy = predy;
			}

			if (dx - (pic1.getWidth() / 2) > w)
				dx = -1 * (pic1.getWidth() * scaleFactor);
			if (dx - (pic1.getWidth() / 2) <= 0 - pic1.getWidth() * scaleFactor)
				dx = w;
			if (dy - (pic1.getHeight() / 2) > h)
				dy = -1 * (pic1.getHeight() * scaleFactor);
			if (dy - (pic1.getHeight() / 2) <= 0 - pic1.getHeight()
					* scaleFactor)
				dy = h;

			canvas.drawColor(Color.BLACK);
			if (pic1 != null && temp)
				canvas.drawBitmap(pic1, cx, cy, null);

			if (pic1 != null && temp && zero)
				canvas.drawBitmap(pic1, 0, 0, null);

			canvas.save();
			if (zero) {
				canvas.translate(dx - cx, dy - cy);
			} else {
				canvas.translate(dx, dy);
			}
			canvas.scale(scaleFactor, scaleFactor);

			canvas.rotate(th);

			if (pic2 != null && temp)
				canvas.drawBitmap(pic2, 0 - pic2.getWidth() / 2,
						0 - pic2.getHeight() / 2, null);

			if (!temp && nbitmap != null)
				canvas.drawBitmap(nbitmap, cx, cy, null);
			canvas.restore();

			try {
				if (!zero)
					canvas.drawBitmap(button3, b3x, b3y, null);
			} catch (NullPointerException e) {
			}

			predx = dx;
			predy = dy;

		}

		@SuppressLint("WrongCall")
		public Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
			zero = true;
			Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
					bmp1.getHeight(), bmp1.getConfig());
			Canvas canvas = new Canvas(bmOverlay);
			onDraw(canvas);
			// canvas.drawBitmap(bmp1, new Matrix(), null);
			// //bmp2=Bitmap.createScaledBitmap(bmp2,(int)(scaleFactor*bmp2.getWidth()),(int)(scaleFactor*bmp2.getHeight()),true);
			// Matrix m=new Matrix();
			// m.postRotate(th,bmp2.getWidth()/2,bmp2.getHeight()/2);
			// m.postScale(scaleFactor,scaleFactor);
			// bmp2=Bitmap.createBitmap(bmp2,0,0,bmp2.getWidth(),bmp2.getHeight(),m,true);
			// canvas.drawBitmap(bmp2, dx-cx-((pic2.getWidth()*scaleFactor)/2),
			// dy-cy-((pic2.getHeight()*scaleFactor)/2), null);

			dx = 0;
			dy = 0;
			scaleFactor = 1;
			th = 0;
			return bmOverlay;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			detector.onTouchEvent(event);
			listener.onTouchEvent(event);

			if (event.getAction() == MotionEvent.ACTION_UP) {
				tx1 = event.getX();
				ty1 = event.getY();
			}

			if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
				// Log.d("a","pointer 2 down");
				two = true;
				transX1 = detector.getFocusX();
				transY1 = detector.getFocusY();
			}

			if (event.getPointerCount() > 1) {
				// Log.d("a","multi touch");
				if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
					// Log.d("a","down");
					focX = detector.getFocusX();
					focY = detector.getFocusY();
					x = event.getX();
					y = event.getY();
					dst(focX, x, focY, y);
					th1 = (float) Math.atan(y / x);
					rI = rotation(event);

				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {

					rN = rotation(event);
					th += rN - rI;
					rI = rotation(event);
				}
			}

			if (event.getPointerCount() > 1 && two) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {

					transX2 = (int) detector.getFocusX();
					transY2 = (int) detector.getFocusY();
					dx -= (transX1 - transX2);
					dy -= (transY1 - transY2);
					transX1 = (int) detector.getFocusX();
					transY1 = (int) detector.getFocusY();

				}
			}

			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				tx1 = (int) event.getX();
				ty1 = (int) event.getY();

				if (tx1 <= b3x + button3.getWidth() && tx1 >= b3x && ty1 >= b3y
						&& temp) {

					nbitmap = overlay(pic1, pic2);
					temp = false;
					saveScreenshot();
					_thread.setRunning(false);

				}
			}

			if (event.getPointerCount() == 1) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {

					tx2 = (int) event.getX();
					ty2 = (int) event.getY();

					dx -= (tx1 - tx2);
					dy -= (ty1 - ty2);
					tx1 = (int) event.getX();
					ty1 = (int) event.getY();

				}
			}

			return true;
		}

		SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
			// called when the user double taps the screen
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				Log.d("a", "double tap");
				float change = 1f / scaleFactor;
				scaleFactor = 1;

				dx = (dx - (w / 2)) * change + (w / 2);
				dy = (dy - h + (h / 2)) * change + h - (h / 2);

				return true; // the event was handled
			} // end method onDoubleTap
		}; // end gestureListener

		private float rotation(MotionEvent event) {
			double delta_x = (event.getX(0) - event.getX(1));
			double delta_y = (event.getY(0) - event.getY(1));
			double radians = Math.atan2(delta_y, delta_x);
			return (float) Math.toDegrees(radians);
		}

		public float dst(float x1, float x2, float y1, float y2) {
			float distance;
			distance = (float) Math
					.sqrt((Math.abs(Math.pow((x1 - x2), 2)) + Math.abs(Math
							.pow((y1 - y2), 2))));

			return distance;
		}

		private class ScaleListener extends
				ScaleGestureDetector.SimpleOnScaleGestureListener {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				scaleFactor *= detector.getScaleFactor();
				scaleFactor = Math.max(MIN_ZOOM,
						Math.min(scaleFactor, MAX_ZOOM));
				dx = (dx - detector.getFocusX()) * detector.getScaleFactor()
						+ detector.getFocusX();
				dy = (dy - h + detector.getFocusY())
						* detector.getScaleFactor() + h - detector.getFocusY();
				invalidate();
				return true;
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			cx = (getWidth() / 2) - (pic1.getWidth() / 2);
			cy = (getHeight() / 2) - (pic1.getHeight() / 2);
			float bx, by;
			w = width;
			h = height;
			bx = pic1.getWidth();
			by = pic1.getHeight();
			if (width / pic1.getWidth() <= height / pic1.getHeight()) {
				float ratio = w / pic1.getWidth();
				Log.d("a", String.valueOf(ratio));
				pic1 = Bitmap.createScaledBitmap(pic1, width,
						(int) (by * ratio), true);
			} else {
				float ratio = h / pic1.getHeight();
				Log.d("a", String.valueOf(ratio));
				pic1 = Bitmap.createScaledBitmap(pic1, (int) (bx * ratio),
						height, true);
			}
			dx = (w / 2) - (pic2.getWidth() / 2);
			dy = (h / 2) - (pic2.getHeight() / 2);

			button3 = BitmapFactory.decodeResource(getResources(),
					R.drawable.save);
			float bh2 = button3.getHeight();
			float bw2 = button3.getWidth();
			Matrix m2 = new Matrix();
			float r = (w / 4) / bw2;
			m2.postScale(r, r);

			button3 = Bitmap.createBitmap(button3, 0, 0, (int) bw2, (int) bh2,
					m2, false);

			b3x = (int) (w / 2) - (button3.getWidth() / 2);
			b3y = (int) (h - button3.getHeight());
			predx = dx;
			predy = dy;
			_thread.setRunning(true);
			invalidate();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			_thread.setRunning(true);
			try {
				_thread.start();
			} catch (IllegalThreadStateException e) {
				Log.d("a", "restarting");
				invalidate();

			}

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// simply copied from sample application LunarLander:
			// we have to tell thread to shut down & wait for it to finish, or
			// else
			// it might touch the Surface after we return and explode
			Log.d("a", "destroyed");
			boolean retry = true;
			_thread.setRunning(false);
			while (retry) {
				try {
					_thread.join();
					retry = false;
				} catch (InterruptedException e) {
					// we will try it again and again...
				}
			}
		}
	}

	class TutorialThread extends Thread {
		private SurfaceHolder _surfaceHolder;
		private Panel _panel;
		private boolean _run = false;

		public TutorialThread(SurfaceHolder surfaceHolder, Panel panel) {
			_surfaceHolder = surfaceHolder;
			_panel = panel;
		}

		public void setRunning(boolean run) {
			_run = run;
		}

		@SuppressLint("WrongCall")
		@Override
		public void run() {
			Canvas c;
			while (_run) {
				c = null;
				try {
					c = _surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {
						_panel.onDraw(c);
					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						_surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}
}
