package com.torcellite.imageComparator;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = "OCVSample::Activity";
	Bitmap bmp;
	ImageView iv;
	String text="";
	/*
	 * This is just a simple example program. No UI design. Just an OpenCV example.
	 * Compares two images and states if they're duplicate or not. Keypoints are
	 * detected and descriptors are extracted and compared. The algorithm - If
	 * the matches is 15% less than or equal to the duplicate descriptors or
	 * actual descriptors, the images are recognized as duplicates. People are
	 * welcome to change the algorithm.
	 */

	public MainActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
		
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iv = (ImageView) MainActivity.this.findViewById(R.id.imageView1);
		new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                new asyncTask().execute();
            }
        }, 2000);//Wait till OpenCV library is initialized
	}
	
	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, this,
				mLoaderCallback);
	}
	
	public class asyncTask extends AsyncTask<Void, Void, Void>
	{
		Mat img1, img2, descriptors, dupDescriptors;
		FeatureDetector detector;
		DescriptorExtractor SurfExtractor;
		DescriptorMatcher matcher;
		MatOfKeyPoint keypoints, dupKeypoints;
		MatOfDMatch matches, matches_final_mat;
		TextView tv;
		ProgressDialog pd;
		int m = 0, d = 0, dd = 0, pos = 0;
		String M, D, DD;
		@Override
		protected void onPreExecute()
		{
			tv = (TextView) MainActivity.this.findViewById(R.id.tv);
			pd = new ProgressDialog(MainActivity.this);
			pd.setIndeterminate(true);
			pd.setCancelable(true);
			pd.setCanceledOnTouchOutside(false);
			pd.setMessage("Processing");
			pd.show();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			compare();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
				Mat img3=new Mat();
				Features2d.drawMatches(img1, keypoints, img2, dupKeypoints, matches_final_mat, img3);
				bmp = Bitmap.createBitmap(img3.cols(), img3.rows(), Bitmap.Config.ARGB_8888);
				Imgproc.cvtColor(img3, img3, Imgproc.COLOR_BGR2RGB);
				Utils.matToBitmap(img3, bmp);
				List<DMatch> finalMatchesList = matches_final_mat.toList();
				if(finalMatchesList.size()>500)//dev discretion for number of matches to be found for an image to be judged as duplicate
					text=finalMatchesList.size()+" matches were found. Possible duplicate image.";
				else
					text=finalMatchesList.size()+" matches were found. Images aren't similar.";
				runOnUiThread(new Runnable() {
		            public void run() { 

		                    try {
		                        Thread.sleep(25);
		                    } catch (InterruptedException e) {
		                        e.printStackTrace();
		                    }
		    				tv.setText(text);
		    				iv.setImageBitmap(bmp);//set image in the UI thread
		    				iv.invalidate();
		            }
		        });
				pd.dismiss();
		}
		
		void compare() {
			img1 = Highgui.imread(Environment.getExternalStorageDirectory().getAbsolutePath()+"/WhatsApp/Media/WhatsApp Images/IMG-20130108-WA0002.jpg");//img1's path
			img2 = Highgui.imread(Environment.getExternalStorageDirectory().getAbsolutePath()+"/WhatsApp/Media/WhatsApp Images/IMG-20130103-WA0000.jpg");//img2's path
			detector = FeatureDetector.create(FeatureDetector.FAST);
			SurfExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
			matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

			keypoints = new MatOfKeyPoint();
			dupKeypoints = new MatOfKeyPoint();
			descriptors = new Mat();
			dupDescriptors = new Mat();
			matches = new MatOfDMatch();
			detector.detect(img1, keypoints);
			Log.d("LOG!", "number of query Keypoints= " + keypoints.size());
			detector.detect(img2, dupKeypoints);
			Log.d("LOG!", "number of dup Keypoints= " + dupKeypoints.size());
			// Descript keypoints
			SurfExtractor.compute(img1, keypoints, descriptors);
			SurfExtractor.compute(img2, dupKeypoints, dupDescriptors);
			Log.d("LOG!", "number of descriptors= " + descriptors.size());
			Log.d("LOG!", "number of dupDescriptors= " + dupDescriptors.size());
			// matching descriptors
			matcher.match(descriptors, dupDescriptors, matches);
			Log.d("LOG!", "Matches Size " + matches.size());
			//New method of finding best matches
			int DIST_LIMIT = 30;//minimum
			List<DMatch> matchesList = matches.toList();
			List<DMatch> matches_final= new ArrayList<DMatch>();
			for(int i=0; i<matchesList.size(); i++)
			{
			   if(matchesList .get(i).distance <= DIST_LIMIT){
			       matches_final.add(matches.toList().get(i));
			   }
			}

			matches_final_mat = new MatOfDMatch();
			matches_final_mat.fromList(matches_final);
			//Current method of finding if image is duplicate - very stupid. IMPROVEMENT NEEDED TO FIND BEST MATCHES!!!
			/*D = descriptors.size().toString();
			pos = D.indexOf('x');
			d = Integer.parseInt(D.substring(pos + 1));
			DD = dupDescriptors.size().toString();
			pos = DD.indexOf('x');
			dd = Integer.parseInt(DD.substring(pos + 1));
			M = matches.size().toString();
			pos = M.indexOf('x');
			m = Integer.parseInt(M.substring(pos + 1));
			System.out.println("dd=" + dd + "d=" + d + "m=" + m);*/

		}
	}
}
