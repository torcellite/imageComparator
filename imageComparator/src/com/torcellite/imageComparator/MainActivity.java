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
	Bitmap bmp, yourSelectedImage, bmpimg1, bmpimg2;
	ImageView iv1, iv2;
	TextView tv;
	String path1, path2;
	String text, selectedPath;
	Button start;
	int imgNo = 0;
	Uri selectedImage;
	InputStream imageStream;
	long startTime, endTime;
	private static final int SELECT_PHOTO = 100;

	private int descriptor = DescriptorExtractor.ORB;
	String descriptorType;
	private int min_dist = 500;

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
		tv.setText("Select the two images to be compared.\n"+"DescriptorExtractor:"+descriptorType+" Minimum distance between keypoints:"+min_dist);
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
					new asyncTask().execute();
					startTime = System.currentTimeMillis();
					System.out.println(descriptor + " " + min_dist);
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

	public class asyncTask extends AsyncTask<Void, Void, Void> {
		Mat img1, img2, descriptors, dupDescriptors;
		FeatureDetector detector;
		DescriptorExtractor DescExtractor;
		DescriptorMatcher matcher;
		MatOfKeyPoint keypoints, dupKeypoints;
		MatOfDMatch matches, matches_final_mat;
		TextView tv;
		ProgressDialog pd;
		boolean isDuplicate = false;

		// int m = 0, d = 0, dd = 0, pos = 0;
		// String M, D, DD;
		@Override
		protected void onPreExecute() {
			tv = (TextView) MainActivity.this.findViewById(R.id.tv);
			pd = new ProgressDialog(MainActivity.this);
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
				Features2d.drawMatches(img1, keypoints, img2, dupKeypoints,
						matches_final_mat, img3);
				bmp = Bitmap.createBitmap(img3.cols(), img3.rows(),
						Bitmap.Config.ARGB_8888);
				Imgproc.cvtColor(img3, img3, Imgproc.COLOR_BGR2RGB);
				Utils.matToBitmap(img3, bmp);
				List<DMatch> finalMatchesList = matches_final_mat.toList();
				endTime = System.currentTimeMillis();
				if (finalMatchesList.size() > min_dist)// dev discretion for
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
						MainActivity.this);
				alertDialog.setTitle("Result");
				alertDialog.setCancelable(false);
				LayoutInflater factory = LayoutInflater.from(MainActivity.this);
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
											+ "\nMinimum distance between keypoints: "
											+ min_dist + "\n" + path1
											+ " was compared to " + path2
											+ "\n" + "Is actual duplicate: "
											+ shouldBeDuplicate.isChecked()
											+ "\nRecognized as duplicate: "
											+ isDuplicate + "\n");
									bw.close();
									Toast.makeText(
											MainActivity.this,
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
												MainActivity.this,
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
				Toast.makeText(MainActivity.this, e.toString(),
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
				detector = FeatureDetector.create(FeatureDetector.DYNAMIC_FAST);
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
				int DIST_LIMIT = 30;// minimum
				List<DMatch> matchesList = matches.toList();
				List<DMatch> matches_final = new ArrayList<DMatch>();
				for (int i = 0; i < matchesList.size(); i++) {
					if (matchesList.get(i).distance <= DIST_LIMIT) {
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
