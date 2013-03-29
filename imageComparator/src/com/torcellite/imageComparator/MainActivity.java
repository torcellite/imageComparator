package com.torcellite.imageComparator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = "OCVSample::Activity";
	Mat img1, img2, descriptors, dupDescriptors;
	FeatureDetector detector;
	DescriptorExtractor SurfExtractor;
	DescriptorMatcher matcher;
	MatOfKeyPoint keypoints, dupKeypoints;
	MatOfDMatch matches;
	TextView tv;
	ImageView iv;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				compare();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
	/*
	 * Compares two images and states if they're duplicate or not. Keypoints are
	 * detected and descriptors are extracted and compared. The algorithm - If
	 * the matches is 15% less than or equal to the duplicate descriptors or
	 * actual descriptors, the images are recognized as duplicates. People are
	 * welcome to change the algorithm.
	 */
	void compare() {
		try{
		OpenCVLoader.initDebug();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, this,
				mLoaderCallback);	
		img1 = Highgui.imread(Environment.getExternalStorageDirectory().getAbsolutePath()+"/mnt/sdcard");//img1's path
		img2 = Highgui.imread(Environment.getExternalStorageDirectory().getAbsolutePath()+"/mnt/sdcard");//img2's path
		detector = FeatureDetector.create(FeatureDetector.FAST);
		SurfExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);

		keypoints = new MatOfKeyPoint();
		dupKeypoints = new MatOfKeyPoint();
		descriptors = new Mat();
		dupDescriptors = new Mat();
		matches = new MatOfDMatch();
		detector.detect(img1, keypoints);
		Mat img3=new Mat();
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
		tv.setText("Number of matches:" + matches.size());
		int m = 0, d = 0, dd = 0, pos = 0;
		String M, D, DD;
		D = descriptors.size().toString();
		pos = D.indexOf('x');
		d = Integer.parseInt(D.substring(pos + 1));
		DD = dupDescriptors.size().toString();
		pos = DD.indexOf('x');
		dd = Integer.parseInt(DD.substring(pos + 1));
		M = matches.size().toString();
		pos = M.indexOf('x');
		m = Integer.parseInt(M.substring(pos + 1));
		System.out.println("dd=" + dd + "d=" + d + "m=" + m);
		if (d > dd) {
			if (((m - dd) / dd <= .15 && (m - d) / d <= .15)
					&& (d - dd) / d <= 50) {
				tv.setText("Duplicate image.");
				Features2d.drawMatches(img1, keypoints, img2, dupKeypoints, matches, img3);
				Bitmap bmp = Bitmap.createBitmap(img3.cols(), img3.rows(), Bitmap.Config.ARGB_8888);
				Utils.matToBitmap(img3, bmp);
				iv.setImageBitmap(bmp);
			} else
				tv.setText("Not duplicate images.");
		} else {
			if (((m - dd) / dd <= .15 && (m - d) / d <= .15)
					&& (d - dd) / dd <= 50) {
				tv.setText("Duplicate image.");
				Features2d.drawMatches(img1, keypoints, img2, dupKeypoints, matches, img3);
				Bitmap bmp = Bitmap.createBitmap(img3.cols(), img3.rows(), Bitmap.Config.ARGB_8888);
				Utils.matToBitmap(img3, bmp);
				iv.setImageBitmap(bmp);
			} else
				tv.setText("Not duplicate images.");
		}
		}
		catch(Exception e)
		{
			System.out.println(e);
			tv.setText(e.toString());
		}
	}

	public MainActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
		
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.tv);
		iv = (ImageView) findViewById(R.id.imageView1);
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, this,
				mLoaderCallback);
	}
}
