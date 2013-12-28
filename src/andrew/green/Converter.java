package andrew.green;

import android.graphics.*;
import android.graphics.Bitmap.*;
import android.net.*;
import android.os.*;
import android.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.FileChannel.*;



public class Converter {
	public static Bitmap convertToMutable(Bitmap imgIn) {
	    try {
	        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

	        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

	        int width = imgIn.getWidth();
	        int height = imgIn.getHeight();
	        Config type = imgIn.getConfig();

	        FileChannel channel = randomAccessFile.getChannel();
	        MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
	        imgIn.copyPixelsToBuffer(map);
	        imgIn.recycle();
	        System.gc();
	        
	        imgIn = Bitmap.createBitmap(width, height, type);
	        map.position(0);
	        
	        imgIn.copyPixelsFromBuffer(map);
	        
	        channel.close();
	        randomAccessFile.close();

	        
	        file.delete();

	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } 

	    return imgIn;
	}
	
	
	public static Bitmap makeItFit(String uriString){
		int samplesize=1;
		BitmapFactory.Options options = new BitmapFactory.Options(); 
		options.inJustDecodeBounds = true;
		
		boolean tryagain=true;
		BitmapFactory.decodeFile(uriString,options); 
		int width=options.outWidth;
		int height=options.outHeight;
		int bytes=width*height;
		int ratio2;
		Bitmap bitmap;
		bitmap=null;
		ratio2=Math.round(bytes/2000000);
		BitmapFactory.Options o = new BitmapFactory.Options(); 
		o.inSampleSize=ratio2;
		samplesize=o.inSampleSize;
		Log.d("a","ratio: "+String.valueOf(ratio2));
		while (tryagain){

			try{
				bitmap = BitmapFactory.decodeFile(uriString,o); 
				Log.d("a","made bitmap without crash: "+String.valueOf(bitmap.getWidth()*bitmap.getHeight()));
				Log.d("a","created!"+String.valueOf(o.inSampleSize));
				tryagain=false;

			}catch(OutOfMemoryError e){

				
				bitmap=null;
				Log.d("a","failed at: "+String.valueOf(o.inSampleSize));

				tryagain=true;
				samplesize++;
				o.inSampleSize=samplesize;
			}
		}
		return bitmap;
	}
	
		
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{ // Raw height and width of image 
	final int height = options.outHeight; 
	final int width = options.outWidth;
	int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) { 
		
		    if (width > height) { 
			
			    inSampleSize = Math.round((float)height / (float)reqHeight); 
				
				} else { 
				
			    inSampleSize = Math.round((float)width / (float)reqWidth);
				}
		} return inSampleSize; 
		}
		
		
	
	
}
