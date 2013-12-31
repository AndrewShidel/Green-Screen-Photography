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
import android.widget.*;
import java.io.*;
import tucker.shidel.greenscreen.R;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class Pic1View extends Activity {
	float x = 0;
	float y = 0;
	float dx = 0;
	float dy = 0;
	int h, w;
	float predx = 0;
	float predy = 0;
	int px, py, pr;
	boolean returntrue = false;
	int v1, v2, v3, v4, b3x, b3y;
	int bax, bay, bar, bacx, bacy;
	int color;
	int[] colorA, pixels, backup;
	int index = 0;
	ContentResolver r;
	double t = 20;
	double ratio, bx;
	int count = 0;
	boolean pick = false;
	boolean shouldmodify = false;
	boolean cancelmessage = false;
	boolean touched = false;
	float tx1, ty1, tx2, ty2;
	float transX1 = 0;
	float transY1 = 0;
	float transX2 = 0;
	float transY2 = 0;
	float transX = 0;
	float transY = 0;
	boolean two = false;
	Bitmap prebitmap, bitmap, button1, button2, prebutton2, button3, back;
	String uriString, s;
	int source;
	Toast message2;
	Paint ballpaint;
	Paint textPaint;
	Paint linepaint, bpaint;
	BitmapFactory.Options o;
	File file;

	private static float MIN_ZOOM = .025f;
	private static float MAX_ZOOM = 500f;

	private float scaleFactor = 1.f;
	public ScaleGestureDetector detector;
	public GestureDetector listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(new Panel(this));
		// button1=BitmapFactory.decodeResource(getResources(),
		// R.drawable.button1);
		Bundle extras = getIntent().getExtras();
		s = extras.getString("buri");
		uriString = extras.getString("uri");
		Toast message = Toast.makeText(Pic1View.this, uriString,
				Toast.LENGTH_SHORT);
		message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
				message.getYOffset() / 2);
		message.show(); // display the Toast
		byte[] b = extras.getByteArray("raw");
		source = extras.getInt("source", 2);
		r = this.getContentResolver();
		System.gc();
		o = new BitmapFactory.Options();
		tx1 = 1;
		ty1 = 1;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		if (source == 2) {

			Uri uri = Uri.parse(uriString);

			try {

				prebitmap = MediaStore.Images.Media.getBitmap(r, uri);
				Log.d("a", "bitmap created");
				bitmap = Converter.convertToMutable(prebitmap);

			} catch (IOException e) {
			}
		} else if (source == 5) {

			prebitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			bitmap = Converter.convertToMutable(prebitmap);
		} else {
			int samplesize = 1;
			boolean tryagain = true;
			BitmapFactory.decodeFile(uriString, options);
			int width = options.outWidth;
			int height = options.outHeight;
			int bytes = width * height;
			int ratio2;
			ratio2 = Math.round(bytes / 1800000);
			o.inSampleSize = ratio2;
			Log.d("a", "ratio: " + String.valueOf(ratio2));
			while (tryagain) {

				try {
					bitmap = BitmapFactory.decodeFile(uriString, o);
					Log.d("a",
							"made bitmap without crash: "
									+ String.valueOf(bitmap.getWidth()
											* bitmap.getHeight()));
					try {
						bitmap = Converter.convertToMutable(prebitmap);
					} catch (NullPointerException n) {
						bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
					}
					Log.d("a",
							"made mutable without crash: "
									+ String.valueOf(bitmap.getWidth()
											* bitmap.getHeight()));
					Log.d("a", "created!" + String.valueOf(o.inSampleSize));
					tryagain = false;

				} catch (OutOfMemoryError e) {

					prebitmap = null;
					bitmap = null;
					Log.d("a", "failed at: " + String.valueOf(o.inSampleSize));

					tryagain = true;
					samplesize++;
					samplesize++;
					o.inSampleSize = 4;
				} catch (NullPointerException e) {
				}

				predx = dx;
				predy = dy;

			}
		}

		// bitmap=Converter.convertToMutable(prebitmap);
		// bitmap=prebitmap.copy(prebitmap.getConfig(),true);
		/*
		 * try{ bitmap=Converter.convertToMutable(prebitmap);
		 * }catch(OutOfMemoryError e){ message.cancel(); message =
		 * Toast.makeText(Pic1View.this,"Image is to large. Try again",
		 * Toast.LENGTH_SHORT); message.setGravity(Gravity.CENTER,
		 * message.getXOffset() / 2, message.getYOffset() / 2); message.show();
		 * Intent intent = new Intent(getBaseContext(), GetBackground.class);
		 * 
		 * startActivity(intent); }
		 */

		// bitmap.setHasAlpha(true);

		colorA = new int[20];
		pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		backup = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(backup, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		System.gc();
		showDialog(1);

	}

	SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		// called when the user double taps the screen
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.d("a", "double tap");
			scaleFactor = 1;
			return true; // the event was handled
		} // end method onDoubleTap
	}; // end gestureListener

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case 1:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.dt1));

			builder.setMessage(getResources().getString(R.string.dialog1))
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
			// do the work to define the game over Dialog
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	class Panel extends SurfaceView implements SurfaceHolder.Callback {
		private TutorialThread _thread;

		public Panel(Context context) {
			super(context);
			detector = new ScaleGestureDetector(getContext(),
					new ScaleListener());
			listener = new GestureDetector(getContext(), gestureListener);
			getHolder().addCallback(this);
			_thread = new TutorialThread(getHolder(), this);

		}

		@Override
		public void onDraw(Canvas canvas) {
			// if (dx>=w || dx<=0-bitmap.getWidth()*scaleFactor || dy>=h ||
			// dy<=0-bitmap.getHeight()*scaleFactor){
			// dx=0;
			// dy=0;
			// }

			if (dst(dx, predx, dy, predy) > 20) {
				dx = predx;
				dy = predy;
			}

			if (dx > w)
				dx = -1 * (bitmap.getWidth() * scaleFactor);
			if (dx <= 0 - bitmap.getWidth() * scaleFactor)
				dx = w;
			if (dy > h)
				dy = -1 * (bitmap.getHeight() * scaleFactor);
			if (dy <= 0 - bitmap.getHeight() * scaleFactor)
				dy = h;

			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(back, (w / 2) - (back.getWidth() / 2), (h / 2)
					- (back.getHeight() / 2), null);

			if (cancelmessage == true) {
				cancelmessage = false;
				message2.cancel();

			}
			canvas.save();
			// dx-=detector.getFocusX()*scaleFactor;
			// dy-=detector.getFocusY()*scaleFactor;
			canvas.translate(dx, dy);
			canvas.scale(scaleFactor, scaleFactor);

			// canvas.drawBitmap(bitmap,x+(w/2-bitmap.getWidth()/2),y+(h/2-bitmap.getHeight()/2),null);

			// canvas.drawBitmap(bitmap,(w/2)-(bitmap.getWidth()/2),(h/2)-(bitmap.getHeight()/2),null);
			canvas.drawBitmap(bitmap, 0, 0, null);

			/*
			 * Toast message =
			 * Toast.makeText(Pic1View.this,String.valueOf(color),
			 * Toast.LENGTH_SHORT); message.setGravity(Gravity.CENTER,
			 * message.getXOffset() / 2, message.getYOffset() / 2);
			 * message.show(); // display the Toast
			 */

			canvas.restore();

			canvas.drawText(
					String.valueOf((int) (tx1 - dx - ((w / 2) - (bitmap
							.getWidth() / 2))))
							+ " "
							+ String.valueOf((int) (ty1 - dy - ((h / 2) - (bitmap
									.getHeight() / 2)))), 0, 150, textPaint);

			canvas.drawText("Threshold: " + String.valueOf(t), (w / 2), h
					- textPaint.getTextSize(), textPaint);

			canvas.drawCircle(px, py, pr, ballpaint);
			canvas.drawLine(v1, v2, v3, v4, linepaint);
			canvas.drawCircle((int) bx, v2, w / 32, ballpaint);
			canvas.drawBitmap(button2, bax, bay, bpaint);
			canvas.drawBitmap(button3, b3x, b3y, bpaint);

			predx = dx;
			predy = dy;

			// canvas.drawBitmap(button1,(w/2)-(button1.getWidth()/2),h-button1.getHeight(),null);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			detector.onTouchEvent(event);
			listener.onTouchEvent(event);

			if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
				Log.d("a", "pointer 2 down");
				two = true;
				transX1 = detector.getFocusX();
				transY1 = detector.getFocusY();
			}

			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				tx1 = (int) event.getX();
				ty1 = (int) event.getY();

				if (Math.abs(tx1 - bacx) <= bar && Math.abs(ty1 - bacy) <= bar) {
					// if (ty1>=h-(h/8)){
					// bitmap=prebitmap.copy(Bitmap.Config.ARGB_8888,true);
					bitmap.setPixels(backup, 0, bitmap.getWidth(), 0, 0,
							bitmap.getWidth(), bitmap.getHeight());
					index = 0;
					colorA = new int[20];
					bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0,
							bitmap.getWidth(), bitmap.getHeight());

				}

				if (Math.abs(tx1 - bx) <= w / 25
						&& Math.abs(ty1 - v2) <= w / 25
						&& MotionEvent.ACTION_DOWN == event.getAction()) {
					touched = true;

				}
				if (tx1 >= b3x && tx1 <= b3x + button3.getWidth() && ty1 >= b3y
						&& ty1 <= b3y + button3.getWidth()) {
					if (source != 1) {
						Log.d("a", "touched");
						// bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
						Log.d("a", "got pixels");
						// saveScreenshot(bitmap);
						saveScreenshot(bitmap);

						SharedPreferences prefs = getBaseContext()
								.getSharedPreferences("com.example.app",
										Context.MODE_PRIVATE);
						prefs.edit().putString("file", file.toString())
								.commit();

						Intent intent = new Intent(Pic1View.this, Resize.class);
						// intent.putExtra("pic2",bitmap);
						// intent.putExtra("pic1",s);
						startActivity(intent);
						finish();
					} else {
						saveScreenshot(bitmap);

						SharedPreferences prefs = getBaseContext()
								.getSharedPreferences("com.example.app",
										Context.MODE_PRIVATE);
						prefs.edit().putString("file", file.toString())
								.commit();

						Intent intent2 = new Intent(getBaseContext(),
								done.class);
						startActivity(intent2);
						finish();

					}

				}

			}
			if (MotionEvent.ACTION_MOVE == event.getAction() && touched == true) {
				if (event.getX() >= w / 8 && event.getX() <= w - (w / 8)) {
					bx = (int) event.getX();
					t = (int) ((bx - (w / 8)) / ratio);

					if (message2 != null)
						message2.cancel();
					message2 = Toast.makeText(Pic1View.this,
							"Proccessing... Please wait", Toast.LENGTH_LONG);
					message2.setGravity(Gravity.CENTER,
							message2.getXOffset() / 2,
							message2.getYOffset() / 2);
					message2.show();
				}
			}
			if (MotionEvent.ACTION_UP == event.getAction() && touched) {
				touched = false;
				if (index > 0) {
					shouldmodify = true;

				}
			}
			if (event.getPointerCount() == 1 && !touched) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {

					tx2 = (int) event.getX();
					ty2 = (int) event.getY();
					// x=x-(tx1-tx2)/scaleFactor;
					// y=(int) (y-(ty1-ty2)/scaleFactor);
					dx -= (tx1 - tx2);
					dy -= (ty1 - ty2);
					tx1 = (int) event.getX();
					ty1 = (int) event.getY();

				}
			}

			if (event.getPointerCount() > 1 && two) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {

					transX2 = (int) detector.getFocusX();
					transY2 = (int) detector.getFocusY();
					// x=x-(tx1-tx2)/scaleFactor;
					// y=(int) (y-(ty1-ty2)/scaleFactor);
					dx -= (transX1 - transX2);
					dy -= (transY1 - transY2);
					transX1 = (int) detector.getFocusX();
					transY1 = (int) detector.getFocusY();

				}
			}

			if (event.getAction() == MotionEvent.ACTION_UP
					&& Math.abs(event.getX() - px) <= pr
					&& Math.abs(event.getY() - py) <= pr) {
				if (pick == false) {
					pick = true;
					ballpaint.setColor(Color.argb(225, 0, 225, 0));
				} else {
					pick = false;
					ballpaint.setColor(Color.argb(100, 225, 0, 0));
				}
				return true;
			}
			if (MotionEvent.ACTION_UP == event.getAction() && pick) {
				try {
					// color=bitmap.getPixel((int)(tx1+dx+((w/2)-(bitmap.getWidth()/2))),(int)(ty1+dy+((h/2)-(bitmap.getHeight()/2))));
					color = bitmap.getPixel(
							(int) ((tx1 / scaleFactor) - (dx / scaleFactor)),
							(int) ((ty1 / scaleFactor) - (dy / scaleFactor)));
					colorA[index] = color;
					index++;
				} catch (IllegalArgumentException e) {
					Log.d("a", "out of bounds.  index=" + index);
				}
				shouldmodify = true;
				if (message2 != null)
					message2.cancel();
				message2 = Toast.makeText(Pic1View.this,
						"Proccessing... Please wait", Toast.LENGTH_LONG);
				message2.setGravity(Gravity.CENTER, message2.getXOffset() / 2,
						message2.getYOffset() / 2);
				message2.show(); // display the Toast*/
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

		private class ScaleListener extends
				ScaleGestureDetector.SimpleOnScaleGestureListener {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				scaleFactor *= detector.getScaleFactor();
				scaleFactor = Math.max(MIN_ZOOM,
						Math.min(scaleFactor, MAX_ZOOM));
				// dx-=detector.getScaleFactor()/detector.getFocusX();
				// dy-=detector.getScaleFactor()/detector.getFocusY();
				Log.d("a", String.valueOf(detector.getScaleFactor()));
				// dx=1/((detector.getScaleFactor())/(detector.getFocusX()-dx));
				// dy=1/((detector.getScaleFactor())/(detector.getFocusY()-dy));
				dx = (dx - detector.getFocusX()) * detector.getScaleFactor()
						+ detector.getFocusX();
				dy = (dy - h + detector.getFocusY())
						* detector.getScaleFactor() + h - detector.getFocusY();
				invalidate();
				return true;
			}
		}

		public float dst(float x1, float x2, float y1, float y2) {
			float distance;
			distance = (float) Math
					.sqrt((Math.abs(Math.pow((x1 - x2), 2)) + Math.abs(Math
							.pow((y1 - y2), 2))));

			return distance;
		}

		private String mScreenshotPath = Environment
				.getExternalStorageDirectory() + "/greenscreen";

		public void saveScreenshot(Bitmap bitmap2) {

			if (ensureSDCardAccess()) {

				file = new File(mScreenshotPath + "/temp3" + ".png");
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(file);
					// bitmap2.setHasAlpha(true);
					bitmap2.compress(Bitmap.CompressFormat.PNG, 100, fos);

					fos.close();
				} catch (FileNotFoundException e) {
					Log.e("Panel", "FileNotFoundException", e);
				} catch (IOException e) {
					Log.e("Panel", "IOEception", e);
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
			return false;
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

				_thread.setRunning(false);

				Intent intent = new Intent(getBaseContext(), MainActivity.class);

				startActivity(intent);

			}

			return true;
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub

			h = height;
			w = width;
			bpaint = new Paint();

			textPaint = new Paint();
			textPaint.setColor(Color.GREEN);
			textPaint.setTextSize(w / 40); // text size 1/20 of screen width
			textPaint.setAntiAlias(true); // smoothes the text
			ballpaint = new Paint();
			ballpaint.setColor(Color.argb(225, 225, 0, 0));
			linepaint = new Paint();
			linepaint.setColor(Color.argb(225, 0, 225, 0));
			linepaint.setAntiAlias(true);
			linepaint.setStrokeWidth(w / 140);

			v1 = w / 8;
			v2 = h - (h / 16);
			v3 = w - (w / 8);
			v4 = h - (h / 16);

			ratio = (v3 - v1) / 225;

			bx = (t * ratio) + (w / 8);

			pr = w / 16;
			px = w - (pr);
			py = pr;

			bar = (w / 16);
			bax = 0;
			bay = 0;

			int amount = 61;
			int span = w / amount;
			float hRatio = ((float) h / (float) w);
			int[] backA;
			int bheight = (int) (amount * hRatio);
			int byteSize = (amount * bheight);
			backA = new int[byteSize];
			int loop = 0;
			boolean black = true;
			while (loop < byteSize) {
				if (black) {
					backA[loop] = Color.BLACK;
					black = false;
				} else {
					backA[loop] = Color.WHITE;
					black = true;
				}
				loop++;
			}

			back = Bitmap.createBitmap(backA, amount, bheight,
					Bitmap.Config.ARGB_8888);
			Matrix bm = new Matrix();
			bm.postScale(span, span);

			back = Bitmap.createBitmap(back, 0, 0, back.getWidth(),
					back.getHeight(), bm, false);

			button2 = BitmapFactory.decodeResource(getResources(),
					R.drawable.back);

			float bh = button2.getHeight();
			float bw = button2.getWidth();
			float r = (w / 8) / bw;
			Matrix m = new Matrix();
			m.postScale(r, r);
			button2 = Bitmap.createBitmap(button2, 0, 0, (int) bw, (int) bh, m,
					true);
			bacx = bax + (button2.getWidth() / 2);
			bacy = bay + (button2.getHeight() / 2);

			button3 = BitmapFactory.decodeResource(getResources(),
					R.drawable.next);
			float bh2 = button3.getHeight();
			float bw2 = button3.getWidth();
			Matrix m2 = new Matrix();
			r = (w / 4) / bw2;
			m2.postScale(r, r);

			button3 = Bitmap.createBitmap(button3, 0, 0, (int) bw2, (int) bh2,
					m2, false);

			b3x = (w / 2) - (button3.getWidth() / 2);
			b3y = v2 - (w / 15) - button3.getHeight();

			dx = (w / 2) - (bitmap.getWidth() / 2);
			dy = (h / 2) - (bitmap.getHeight() / 2);

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {

			_thread.setRunning(true);
			_thread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

			// boolean retry = true;
			// _thread.setRunning(false);
			// while (retry) {
			// try {
			// _thread.join();
			// retry = false;
			// } catch (InterruptedException e) {
			// // we will try it again and again...
			// }
			// }
		}
	}

	public class TutorialThread extends Thread {
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

		public void modify(int color1) {

			try {

				int width = bitmap.getWidth();

				int height = bitmap.getHeight();
				int cx = 0;
				int cy = 0;
				int c = 0;
				int bp;
				int a, r, g, b;
				int a2, r2, g2, b2;

				a = Color.alpha(color1);
				r = Color.red(color1);
				g = Color.green(color1);
				b = Color.blue(color1);

				while (cx < width && cy < height) {
					bp = bitmap.getPixel(cx, cy);
					a2 = Color.alpha(bp);
					r2 = Color.red(bp);
					g2 = Color.green(bp);
					b2 = Color.blue(bp);
					if (Math.abs(a - a2) <= t  Math.sqrt(Math.pow((r-r2),2) + Math.pow((g-g2),2) + Math.pow((b-b2),2)) <= t){ //&& Math.abs(r - r2) <= t
							//&& Math.abs(g - g2) <= t * 2
							//&& Math.abs(b - b2) <= t) {

						bitmap.setPixel(cx, cy, Color.TRANSPARENT);
						count++;
					}
					cx++;
					if (cx >= width) {
						cy++;
						cx = 0;

					}

				}
				cancelmessage = true;
				// bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
			} catch (IllegalArgumentException e) {
				cancelmessage = true;
			}
		}

		@SuppressLint("WrongCall")
		@Override
		public void run() {
			Canvas c;
			while (_run) {
				if (shouldmodify) {
					bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0,
							bitmap.getWidth(), bitmap.getHeight());
					shouldmodify = false;
					int counter = 1;
					Toast message = Toast.makeText(Pic1View.this, "Saving image...",
						Toast.LENGTH_SHORT);
					message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
					message.getYOffset() / 2);
					message.show(); // display the Toast
					
					while (counter <= index && index > 0) {
						if (index > 0)
							modify(colorA[counter - 1]);
						counter++;
					}
					cancelmessage = true;
				}
				c = null;
				try {
					c = _surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {
						try {
							_panel.onDraw(c);
						} catch (NullPointerException e) {
						}
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
