package com.example.lockapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lockapp.service.LockService;
import com.example.lockapp.service.LockService.LocalBinder;

public class KeyActivity extends Activity implements OnClickListener {
	private ImageView icon;
	private TextView label;
	private Button cancel,submit;
	private MyConn conn;
	private LockService lockService;
	private EditText password;
	private String packageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key);
		Intent intent = getIntent();
		String packageName = intent.getStringExtra("packageName");
		icon = (ImageView) findViewById(R.id.icon);
		label = (TextView) findViewById(R.id.label);
		cancel = (Button) findViewById(R.id.cancel);
		submit = (Button) findViewById(R.id.submit);
		password = (EditText) findViewById(R.id.password);
		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo(packageName, 0);
			label.setText(info.applicationInfo.loadLabel(getPackageManager()));
			icon.setImageDrawable(info.applicationInfo.loadIcon(getPackageManager()));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent servieIntent = new Intent(this,LockService.class);
		conn = new MyConn();
		bindService(servieIntent, conn, BIND_AUTO_CREATE);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.key, menu);
		return true;
	}
	
	private class MyConn implements ServiceConnection{
		//在操作者在连接一个服务成功时被调用。IBinder对象就是onBind(Intent intent)返回的IBinder对象。
		public void onServiceConnected(ComponentName name, IBinder service) {
			//因为返回的IBinder实现了iService接口（向上转型）
			lockService = ((LocalBinder)service).getService();
		}
		//在服务崩溃或被杀死导致的连接中断时被调用，而如果我们自己解除绑定时则不会被调用
		public void onServiceDisconnected(ComponentName name) {
			
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(conn);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.cancel:
			finish();
			break;
		case R.id.submit:
			String pwd = password.getText().toString().trim();
			//判断输入的密码是否为空
			if(TextUtils.isEmpty(pwd)){
				Toast.makeText(this, "密码不能为空", 0).show();
				return ;
			}
			if("123".equals(pwd)){
				lockService.addToTempUnlockPagekageNames(packageName);
				finish();
				
			}else{
				Toast.makeText(this, "密码不正确", 0).show();
				return ;
			}
			break;
		}
	}
	

}
