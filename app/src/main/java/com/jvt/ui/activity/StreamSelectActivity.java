package com.jvt.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.jvt.R;

public class StreamSelectActivity extends Activity {
	CheckBox main, sub;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_stream_select);
		main = (CheckBox) findViewById(R.id.ck_main);
		sub = (CheckBox) findViewById(R.id.ck_sub);
		type = getIntent().getIntExtra("stream", 1);
		if (type == 1) {
			sub.setChecked(true);
			main.setChecked(false);
		} else {
			sub.setChecked(false);
			main.setChecked(true);
		}
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		sub.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					main.setChecked(false);
					back(1);
				
				}

			}
		});
		main.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					sub.setChecked(false);
					back(0);
				}

			}
		});

	}

	void back(int type) {
		Intent it = new Intent();
		it.putExtra("stream", type);
		setResult(RESULT_OK, it);
		finish();
	}
}
