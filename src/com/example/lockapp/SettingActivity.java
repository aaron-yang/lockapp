package com.example.lockapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lockapp.service.LockService;
import com.example.lockapp.utils.ServiceStatusUtil;

public class SettingActivity extends Activity  implements OnClickListener{
	private TextView statusLabel;
	private CheckBox cb_status;
	private RelativeLayout status_setting_applock;
	private Intent serviceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		statusLabel = (TextView) findViewById(R.id.statusLabel);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		status_setting_applock = (RelativeLayout) findViewById(R.id.status_setting_applock);
		status_setting_applock.setOnClickListener(this);
		serviceIntent = new Intent(this,LockService.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.status_setting_applock:
			if (cb_status.isChecked()) {
				statusLabel.setText("程序锁服务没有开启");
				stopService(serviceIntent);
				cb_status.setChecked(false);
			} else {
				statusLabel.setText("程序锁服务已经开启");
				startService(serviceIntent);
				cb_status.setChecked(true);
			}
			break;
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (ServiceStatusUtil.isServiceRunning(this,
				"com.example.lockapp.service.LockService")) {
			cb_status.setChecked(true);
			statusLabel.setText("程序锁服务已经开启");
		} else {
			cb_status.setChecked(false);
			statusLabel.setText("程序锁服务没有开启");
		}
		super.onResume();
	}
	
	

}
