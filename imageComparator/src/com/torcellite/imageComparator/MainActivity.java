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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "OCVSample::Activity";
	private static Bitmap bmp, yourSelectedImage, bmpimg1, bmpimg2;
	private static ImageView iv1, iv2;
	private static TextView tv;
	private static String path1, path2;
	private static String text;
	private static Button start;
	private static int imgNo = 0;
	private static Uri selectedImage;
	private static InputStream imageStream;
	private static long startTime, endTime;
	private static final int SELECT_PHOTO = 100;

	private static int descriptor = DescriptorExtractor.ORB;
	private static String descriptorType;
	private static int min_dist = 80;
	private static int min_matches = 100;

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
		tv = (TextView) MainActivity.this.findViewById(R.id.tv);
		run();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent call = new Intent(MainActivity.this, Settings.class);
			call.putExtra("descriptor", descriptor);
			call.putExtra("min_dist", min_dist);
			call.putExtra("min_matches", min_matches);
			call.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			call.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(call);
			break;
		}
		return true;
	}

	public void run() {
		if (descriptor == DescriptorExtractor.BRIEF)
			descriptorType = "BRIEF";
		else if (descriptor == DescriptorExtractor.BRISK)
			descriptorType = "BRISK";
		else if (descriptor == DescriptorExtractor.FREAK)
			descriptorType = "FREAK";
		else if (descriptor == DescriptorExtractor.ORB)
			descriptorType = "ORB";
		else if (descriptor == DescriptorExtractor.SIFT)
			descriptorType = "SIFT";
		else if(descriptor == DescriptorExtractor.SURF)
			descriptorType = "SURF";
		System.out.println(descriptorType);
		tv.setText("Select the two images to be compared.\n"+"DescriptorExtractor:"+descriptorType+"\nHamming distance between descriptors:"+min_dist+"\nMinimum number of good matches:"+min_matches);
		iv1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
				imgNo = 1;

			}
		});
		iv2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
				imgNo = 2;
			}
		});
		start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (bmpimg1 != null && bmpimg2 != null) {
					/*if(bmpimg1.getWidth()!=bmpimg2.getWidth()){
						bmpimg2 = Bitmap.createScaledBitmap(bmpimg2, bmpimg1.getWidth(), bmpimg1.getHeight(), true);
					}*/
					bmpimg1 = Bitmap.createScaledBitmap(bmpimg1, 100, 100, true);
					bmpimg2 = Bitmap.createScaledBitmap(bmpimg2, 100, 100, true);
					Mat img1 = new Mat();
					Utils.bitmapToMat(bmpimg1, img1);
			        Mat img2 = new Mat();
			        Utils.bitmapToMat(bmpimg2, img2);
			        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGBA2GRAY); 
			        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_RGBA2GRAY); 
			        img1.convertTo(img1, CvType.CV_32F);
			        img2.convertTo(img2, CvType.CV_32F);
			        Log.d("ImageComparator", "img1:"+img1.rows()+"x"+img1.cols()+" img2:"+img2.rows()+"x"+img2.cols());
			        Mat hist1 = new Mat();
			        Mat hist2 = new Mat();
			        MatOfInt histSize = new MatOfInt(180);
			        MatOfInt channels = new MatOfInt(0);
			        ArrayList<Mat> bgr_planes1= new ArrayList<Mat>();
			        ArrayList<Mat> bgr_planes2= new ArrayList<Mat>();
			        Core.split(img1, bgr_planes1);
			        Core.split(img2, bgr_planes2);
			        MatOfFloat histRanges = new MatOfFloat (0f, 180f);		        
			        boolean accumulate = false;
			        Imgproc.calcHist(bgr_planes1, channels, new Mat(), hist1, histSize, histRanges, accumulate);
			        Imgproc.calcHist(bgr_planes2, channels, new Mat(), hist1, histSize, histRanges, accumulate);
			        try{
			        	Mat dst=new Mat();
			        	Core.compare(img1, img2, dst, Core.CMP_EQ);
			        	Log.d("ImageComparator", Core.countNonZero(dst)+"/"+dst.size());
			        	img1.convertTo(img1, CvType.CV_8U);
				        img2.convertTo(img2, CvType.CV_8U);
			        	double l2_norm = Core.norm(img1, img2, Core.NORM_INF);
				        Log.d("ImageComparator", "l2_norm="+l2_norm);
				        img1.convertTo(img1, CvType.CV_32F);
				        img2.convertTo(img2, CvType.CV_32F);
				        hist1.convertTo(hist1, CvType.CV_32F);
				        hist2.convertTo(hist2, CvType.CV_32F);
			        	double compare= Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_BHATTACHARYYA);
			        	Log.d("ImageComparator", "compare="+compare);
			        	/*int noOfSimilarPixels=Core.countNonZero(dst);*/
			        }
			        catch(Exception e) {
			        	Log.e("ImageComparator", "Exception outer");
			        	
			        }
			        	new asyncTask(MainActivity.this).execute();	
					startTime = System.currentTimeMillis();
				} else
					Toast.makeText(MainActivity.this,
							"You haven't selected images.", Toast.LENGTH_LONG)
							.show();
			}
		});
	}

	@Override
	protected void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
		min_dist = newIntent.getExtras().getInt("min_dist");
		descriptor = newIntent.getExtras().getInt("descriptor");
		min_matches = newIntent.getExtras().getInt("min_matches");
		run();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				selectedImage = imageReturnedIntent.getData();
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				yourSelectedImage = BitmapFactory.decodeStream(imageStream);
				if (imgNo == 1) {
					iv1.setImageBitmap(yourSelectedImage);
					path1 = selectedImage.getPath();
					bmpimg1 = yourSelectedImage;
					iv1.invalidate();
				} else if (imgNo == 2) {
					iv2.setImageBitmap(yourSelectedImage);
					path2 = selectedImage.getPath();
					bmpimg2 = yourSelectedImage;
					iv2.invalidate();
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, this,
				mLoaderCallback);
	}
	
	public static class asyncTask extends AsyncTask<Void, Void, Void> {
		private static Mat img1, img2, descriptors, dupDescriptors;
		private static FeatureDetector detector;
		private static DescriptorExtractor DescExtractor;
		private static DescriptorMatcher matcher;
		private static MatOfKeyPoint keypoints, dupKeypoints;
		private static MatOfDMatch matches, matches_final_mat;
		private static ProgressDialog pd;
		private static boolean isDuplicate = false;
		private MainActivity asyncTaskContext=null;
		private static Scalar RED = new Scalar(255,0,0);
		private static Scalar GREEN = new Scalar(0,255,0);
		public asyncTask(MainActivity context)
		{
			asyncTaskContext=context;
		}
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(asyncTaskContext);
			pd.setIndeterminate(true);
			pd.setCancelable(true);
			pd.setCanceledOnTouchOutside(false);
			pd.setMessage("Processing...");
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			compare();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				Mat img3 = new Mat();
				MatOfByte drawnMatches = new MatOfByte();
				Features2d.drawMatches(img1, keypoints, img2, dupKeypoints,
						matches_final_mat, img3, GREEN, RED,  drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
				bmp = Bitmap.createBitmap(img3.cols(), img3.rows(),
						Bitmap.Config.ARGB_8888);
				Imgproc.cvtColor(img3, img3, Imgproc.COLOR_BGR2RGB);
				Utils.matToBitmap(img3, bmp);
				List<DMatch> finalMatchesList = matches_final_mat.toList();
				final int matchesFound=finalMatchesList.size();
				endTime = System.currentTimeMillis();
				if (finalMatchesList.size() > min_matches)// dev discretion for
														// number of matches to
														// be found for an image
														// to be judged as
														// duplicate
				{
					text = finalMatchesList.size()
							+ " matches were found. Possible duplicate image.\nTime taken="
							+ (endTime - startTime) + "ms";
					isDuplicate = true;
				} else {
					text = finalMatchesList.size()
							+ " matches were found. Images aren't similar.\nTime taken="
							+ (endTime - startTime) + "ms";
					isDuplicate = false;
				}
				pd.dismiss();
				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						asyncTaskContext);
				alertDialog.setTitle("Result");
				alertDialog.setCancelable(false);
				LayoutInflater factory = LayoutInflater.from(asyncTaskContext);
				final View view = factory.inflate(R.layout.image_view, null);
				ImageView matchedImages = (ImageView) view
						.findViewById(R.id.finalImage);
				matchedImages.setImageBitmap(bmp);
				matchedImages.invalidate();
				final CheckBox shouldBeDuplicate = (CheckBox) view
						.findViewById(R.id.checkBox);
				TextView message = (TextView) view.findViewById(R.id.message);
				message.setText(text);
				alertDialog.setView(view);
				shouldBeDuplicate
						.setText("These images are actually duplicates.");
				alertDialog.setPositiveButton("Add to logs",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								File logs = new File(Environment
										.getExternalStorageDirectory()
										.getAbsolutePath()
										+ "/imageComparator/Data Logs.txt");
								FileWriter fw;
								BufferedWriter bw;
								try {
									fw = new FileWriter(logs, true);
									bw = new BufferedWriter(fw);
									bw.write("Algorithm used: "
											+ descriptorType
											+ "\nHamming distance: "
											+ min_dist + "\nMinimum good matches: "+min_matches
											+"\nMatches found: "+matchesFound+"\nTime elapsed: "+(endTime-startTime)+"seconds\n"+ path1
											+ " was compared to " + path2
											+ "\n" + "Is actual duplicate: "
											+ shouldBeDuplicate.isChecked()
											+ "\nRecognized as duplicate: "
											+ isDuplicate + "\n");
									bw.close();
									Toast.makeText(
											asyncTaskContext,
											"Logs updated.\nLog location: "
													+ Environment
															.getExternalStorageDirectory()
															.getAbsolutePath()
													+ "/imageComparator/Data Logs.txt",
											Toast.LENGTH_LONG).show();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									// e.printStackTrace();
									try {
										File dir = new File(Environment
												.getExternalStorageDirectory()
												.getAbsolutePath()
												+ "/imageComparator/");
										dir.mkdirs();
										logs.createNewFile();
										logs = new File(
												Environment
														.getExternalStorageDirectory()
														.getAbsolutePath()
														+ "/imageComparator/Data Logs.txt");
										fw = new FileWriter(logs, true);
										bw = new BufferedWriter(fw);
										bw.write("Algorithm used: "
												+ descriptorType
												+ "\nMinimum distance between keypoints: "
												+ min_dist + "\n" + path1
												+ " was compared to " + path2
												+ "\n"
												+ "Is actual duplicate: "
												+ shouldBeDuplicate.isChecked()
												+ "\nRecognized as duplicate: "
												+ isDuplicate + "\n");
										bw.close();
										Toast.makeText(
												asyncTaskContext,
												"Logs updated.\nLog location: "
														+ Environment
																.getExternalStorageDirectory()
																.getAbsolutePath()
														+ "/imageComparator/Data Logs.txt",
												Toast.LENGTH_LONG).show();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

								}
							}
						});
				alertDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(asyncTaskContext, e.toString(),
						Toast.LENGTH_LONG).show();
			}
		}

		void compare() {
			try {
				bmpimg1 = bmpimg1.copy(Bitmap.Config.ARGB_8888, true);
				bmpimg2 = bmpimg2.copy(Bitmap.Config.ARGB_8888, true);
				img1 = new Mat();
				img2 = new Mat();
				Utils.bitmapToMat(bmpimg1, img1);
				Utils.bitmapToMat(bmpimg2, img2);
				Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2RGB);
				Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2RGB);
				detector = FeatureDetector.create(FeatureDetector.PYRAMID_FAST);
				DescExtractor = DescriptorExtractor.create(descriptor);
				matcher = DescriptorMatcher
						.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

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
				DescExtractor.compute(img1, keypoints, descriptors);
				DescExtractor.compute(img2, dupKeypoints, dupDescriptors);
				Log.d("LOG!", "number of descriptors= " + descriptors.size());
				Log.d("LOG!",
						"number of dupDescriptors= " + dupDescriptors.size());
				// matching descriptors
				matcher.match(descriptors, dupDescriptors, matches);
				Log.d("LOG!", "Matches Size " + matches.size());
				// New method of finding best matches
				List<DMatch> matchesList = matches.toList();
				List<DMatch> matches_final = new ArrayList<DMatch>();
				for (int i = 0; i < matchesList.size(); i++) {
					if (matchesList.get(i).distance <= min_dist) {
						matches_final.add(matches.toList().get(i));
					}
				}

				matches_final_mat = new MatOfDMatch();
				matches_final_mat.fromList(matches_final);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
