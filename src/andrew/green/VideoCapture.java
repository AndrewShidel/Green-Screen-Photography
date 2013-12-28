package andrew.green;

import android.app.*;
import android.content.pm.*;
import android.media.*;
import android.os.*;
import android.view.*;
import java.io.*;

public class VideoCapture extends Activity implements SurfaceHolder.Callback {
    MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        recorder = new MediaRecorder();
        initRecorder();
        setContentView(R.layout.camera_view);

        SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        recorder.setPreviewDisplay(holder.getSurface());
        //cameraView.setClickable(true);
        //cameraView.setOnClickListener(this);
    }
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_DOWN==event.getAction()){
			if (recording) {
				recorder.stop();
				
				recording = false;

				// Let's initRecorder so we can record again
				//initRecorder();
				//prepareRecorder();
				finish();
			} else {
				recording = true;
				recorder.start();
			}
		}
		return true;
		}

    private void initRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        
        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(cpHigh);
        recorder.setOutputFile("/sdcard/green_screen"+System.currentTimeMillis()+".mp4");
        recorder.setMaxDuration(50000); // 50 seconds
        recorder.setMaxFileSize(50000000); // Approximately 5 megabytes
    }

    private void prepareRecorder() {
        

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

//    public void onClick(View v) {
//        if (recording) {
//            recorder.stop();
//            recording = false;
//
//            // Let's initRecorder so we can record again
//            initRecorder();
//            prepareRecorder();
//        } else {
//            recording = true;
//            recorder.start();
//        }
//    }

    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        finish();
    }
}

