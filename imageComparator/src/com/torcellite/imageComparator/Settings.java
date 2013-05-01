package com.torcellite.imageComparator;

import org.opencv.features2d.DescriptorExtractor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Settings extends Activity {

	@SuppressWarnings("unused")
	private static RadioGroup descTypes;
	private static RadioButton brief, brisk, freak, orb;
	private static Button apply;
	private static EditText DIST_LIMIT, MIN_MATCHES;
	private static int descriptor, min_dist, min_matches;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		descTypes = (RadioGroup) findViewById(R.id.radioGroup1);
		brief = (RadioButton) findViewById(R.id.radio0);
		brisk = (RadioButton) findViewById(R.id.radio1);
		freak = (RadioButton) findViewById(R.id.radio2);
		orb = (RadioButton) findViewById(R.id.radio3);
		apply = (Button) findViewById(R.id.button1);
		DIST_LIMIT = (EditText) findViewById(R.id.editText1);
		MIN_MATCHES = (EditText) findViewById(R.id.editText2);
		MIN_MATCHES.setText("100");
		DIST_LIMIT.setText("80");
		apply.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int descriptor = 0, min_dist = 80, min_matches=100;
				if (brief.isChecked())
					descriptor = DescriptorExtractor.BRIEF;
				else if (brisk.isChecked())
					descriptor = DescriptorExtractor.BRISK;
				else if (freak.isChecked())
					descriptor = DescriptorExtractor.FREAK;
				else if (orb.isChecked())
					descriptor = DescriptorExtractor.ORB;
				try {
					min_dist = Integer.parseInt(DIST_LIMIT.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
					min_dist = 500;
				}
				Intent call = new Intent(Settings.this, MainActivity.class);
				call.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				call.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				call.putExtra("descriptor", descriptor);
				call.putExtra("min_dist", min_dist);
				call.putExtra("min_matches", min_matches);
				startActivity(call);
			}
		});
	}

	@Override
	protected void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
		descriptor = newIntent.getExtras().getInt("descriptor");
		min_dist = newIntent.getExtras().getInt("min_dist");
		min_matches = newIntent.getExtras().getInt("min_matches");
		DIST_LIMIT.setText(min_dist + "");
		switch (descriptor) {
		case 3:
			orb.setChecked(true);
			break;
		case 4:
			brief.setChecked(true);
			break;
		case 5:
			brisk.setChecked(true);
			break;
		case 6:
			freak.setChecked(true);
			break;
		}
		apply.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (brief.isChecked())
					descriptor = DescriptorExtractor.BRIEF;
				else if (brisk.isChecked())
					descriptor = DescriptorExtractor.BRISK;
				else if (freak.isChecked())
					descriptor = DescriptorExtractor.FREAK;
				else if (orb.isChecked())
					descriptor = DescriptorExtractor.ORB;
				try {
					min_dist = Integer.parseInt(DIST_LIMIT.getText().toString());
					min_matches = Integer.parseInt(MIN_MATCHES.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
					min_dist = 80;
					min_matches=100;
				}
				Intent call = new Intent(Settings.this, MainActivity.class);
				call.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				call.putExtra("descriptor", descriptor);
				call.putExtra("min_dist", min_dist);
				call.putExtra("min_matches", min_matches);
				startActivity(call);
			}
		});
	}

}
