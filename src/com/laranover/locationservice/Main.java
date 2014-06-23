package com.laranover.locationservice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.laranover.locationservice.R;

public class Main extends Activity implements OnClickListener{

	private Button btnStart, btnStop;
		
//	private static final String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initialize();
		addListener();
	}
	
	private void initialize(){
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
	}
	
	private void addListener(){
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btnStart:
				Utils.startService(getBaseContext());
				break;
			case R.id.btnStop:
				Utils.stopService(getBaseContext());
				break;
		}	
	}
		
}
