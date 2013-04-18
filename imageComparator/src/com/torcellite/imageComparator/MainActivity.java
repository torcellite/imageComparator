package com.torcellite.imageComparator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "OCVSample::Activity";
	Bitmap bmp, yourSelectedImage, bmpimg1, bmpimg2;
	ImageView iv1, iv2;
	String path1, path2;
	String text, selectedPath;
	Button start;
	int imgNo=0;
	Uri selectedImage;
    InputStream imageStream;
    long startTime, endTime;
	private static final int SELECT_PHOTO = 100;
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
		iv1 = (ImageView) MainActivity.this.findViewById(R.id.img1);
		iv2 = (ImageView) MainActivity.this.findViewById(R.id.img2);
		start = (Button) MainActivity.this.findViewById(R.id.button1);
		iv1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
				imgNo=1;
				
			}
		});
		iv2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
				imgNo=2;
			}
		});
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(bmpimg1!=null&&bmpimg2!=null)
				{
					System.out.println(path1);
					System.out.println(path2);
					new asyncTask().execute();
					startTime=System.currentTimeMillis();
				}
				else
					Toast.makeText(MainActivity.this, "You haven't selected images.", Toast.LENGTH_LONG).show();
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case SELECT_PHOTO:
	        if(resultCode == RESULT_OK){  
	            selectedImage = imageReturnedIntent.getData();
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				yourSelectedImage = BitmapFactory.decodeStream(imageStream);
				if(imgNo==1)
				{
					iv1.setImageBitmap(yourSelectedImage);
					path1=selectedImage.getPath();
					bmpimg1=yourSelectedImage;
					iv1.invalidate();
				}
				else if(imgNo==2)
				{
					iv2.setImageBitmap(yourSelectedImage);
					path2=selectedImage.getPath();
					bmpimg2=yourSelectedImage;
					iv2.invalidate();
				}
	        }
	    }
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, this,
				mLoaderCallback);
		iv1.refreshDrawableState();
		iv2.refreshDrawableState();
	}
	
	public class asyncTask extends AsyncTask<Void, Void, Void>
	{
		Mat img1, img2, descriptors, dupDescriptors;
		FeatureDetector detector;
		DescriptorExtractor ORBExtractor;
		DescriptorMatcher matcher;
		MatOfKeyPoint keypoints, dupKeypoints;
		MatOfDMatch matches, matches_final_mat;
		TextView tv;
		ProgressDialog pd;
		boolean isDuplicate=false;
		//int m = 0, d = 0, dd = 0, pos = 0;
		//String M, D, DD;
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
				endTime=System.currentTimeMillis();
				if(finalMatchesList.size()>500)//dev discretion for number of matches to be found for an image to be judged as duplicate
				{
					text=finalMatchesList.size()+" matches were found. Possible duplicate image.\nTime taken="+(endTime-startTime)+"ms";
					isDuplicate=true;
				}
				else
				{
					text=finalMatchesList.size()+" matches were found. Images aren't similar.\nTime taken="+(endTime-startTime)+"ms";
					isDuplicate=false;
				}
				pd.dismiss();
				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
				alertDialog.setTitle("Result");
				LayoutInflater factory = LayoutInflater.from(MainActivity.this);
				final View view = factory.inflate(R.layout.image_view, null);
				ImageView matchedImages=(ImageView) view.findViewById(R.id.finalImage);
				matchedImages.setImageBitmap(bmp);
				matchedImages.invalidate();
				final CheckBox shouldBeDuplicate=(CheckBox) view.findViewById(R.id.checkBox);
				TextView message=(TextView) view.findViewById(R.id.message);
				message.setText(text);
				alertDialog.setView(view);
				shouldBeDuplicate.setText("These images are actually duplicates.");
				alertDialog.setPositiveButton("Add to logs", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
					   File logs=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/imageComparator/Data Logs.txt");
					   FileWriter fw;
					   BufferedWriter bw;
					try {
						fw = new FileWriter(logs, true);
					    bw=new BufferedWriter(fw);
					    bw.write(path1+" was compared to "+path2+"\n"+"Is actual duplicate: "+shouldBeDuplicate.isChecked()+"\nRecognized as duplicate: "+isDuplicate+"\n");
						bw.close();
						Toast.makeText(MainActivity.this, "Logs updated.\nLog location: "+Environment.getExternalStorageDirectory().getAbsolutePath()+"/imageComparator/Data Logs.txt", Toast.LENGTH_LONG).show();
					}
					   catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							try {
								File dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/imageComparator/");
								dir.mkdirs();
								logs.createNewFile();
								logs= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/imageComparator/Data Logs.txt");
								fw = new FileWriter(logs, true);
							    bw=new BufferedWriter(fw);
							    bw.write(path1+" was compared to "+path2+"\n"+"Is actual duplicate: "+shouldBeDuplicate.isChecked()+"\nRecognized as duplciate: "+isDuplicate+"\n");
								bw.close();
								Toast.makeText(MainActivity.this, "Logs updated.\nLog location: "+Environment.getExternalStorageDirectory().getAbsolutePath()+"/imageComparator/Data Logs.txt", Toast.LENGTH_LONG).show();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						
						}
				   }
				});
				alertDialog.show();
		}
		
		void compare() {
			try{
			bmpimg1=bmpimg1.copy(Bitmap.Config.ARGB_8888, true);
			bmpimg2=bmpimg2.copy(Bitmap.Config.ARGB_8888, true);
			img1=new Mat();
			img2=new Mat();
			Utils.bitmapToMat(bmpimg1, img1);
			Utils.bitmapToMat(bmpimg2, img2);
			Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2RGB);
			Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2RGB);
			System.out.println(img1+" "+img2);
			detector = FeatureDetector.create(FeatureDetector.FAST);
			ORBExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
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
			ORBExtractor.compute(img1, keypoints, descriptors);
			ORBExtractor.compute(img2, dupKeypoints, dupDescriptors);
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
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			//-------old stuff--------
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
