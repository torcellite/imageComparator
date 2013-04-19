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

	RadioGroup descTypes;
	RadioButton brief, brisk, freak, orb, sift, surf;
	Button apply;
	EditText num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		descTypes = (RadioGroup) findViewById(R.id.radioGroup1);
		brief = (RadioButton) findViewById(R.id.radio0);
		brisk = (RadioButton) findViewById(R.id.radio1);
		freak = (RadioButton) findViewById(R.id.radio2);
		orb = (RadioButton) findViewById(R.id.radio3);
		sift = (RadioButton) findViewById(R.id.radio4);
		surf = (RadioButton) findViewById(R.id.radio5);
		apply = (Button) findViewById(R.id.button1);
		num = (EditText) findViewById(R.id.editText1);
		num.setText("500");
		apply.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int descriptor, min_dist = 500;
				if (brief.isChecked())
					descriptor = DescriptorExtractor.BRIEF;
				else if (brisk.isChecked())
					descriptor = DescriptorExtractor.BRISK;
				if (freak.isChecked())
					descriptor = DescriptorExtractor.FREAK;
				if (orb.isChecked())
					descriptor = DescriptorExtractor.ORB;
				if (sift.isChecked())
					descriptor = DescriptorExtractor.SIFT;
				else
					descriptor = DescriptorExtractor.SURF;
				try {
					min_dist = Integer.parseInt(num.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
					min_dist = 500;
				}
				Intent call = new Intent(Settings.this, MainActivity.class);
				call.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				call.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				call.putExtra("descriptor", descriptor);
				call.putExtra("min_dist", min_dist);
				startActivity(call);
			}
		});
	}

	@Override
	protected void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
		int descriptor = newIntent.getExtras().getInt("descriptor");
		int min_dist = newIntent.getExtras().getInt("min_dist");
		num.setText(min_dist + "");
		switch (descriptor) {
		case 1:
			sift.setChecked(true);
			break;
		case 2:
			surf.setChecked(true);
			break;
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
				int descriptor, min_dist = 500;
				if (brief.isChecked())
					descriptor = DescriptorExtractor.BRIEF;
				else if (brisk.isChecked())
					descriptor = DescriptorExtractor.BRISK;
				if (freak.isChecked())
					descriptor = DescriptorExtractor.FREAK;
				if (orb.isChecked())
					descriptor = DescriptorExtractor.ORB;
				if (sift.isChecked())
					descriptor = DescriptorExtractor.SIFT;
				else
					descriptor = DescriptorExtractor.SURF;
				try {
					min_dist = Integer.parseInt(num.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
					min_dist = 500;
				}
				Intent call = new Intent(Settings.this, MainActivity.class);
				call.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				call.putExtra("descriptor", descriptor);
				call.putExtra("min_dist", min_dist);
				startActivity(call);
			}
		});
	}

}
