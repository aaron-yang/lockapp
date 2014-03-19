package com.example.lockapp;

import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lockapp.R;
import com.example.lockapp.dao.LockDao;
import com.example.lockapp.entity.AppInfo;
import com.example.lockapp.provider.AppInfoProvider;

public class MainActivity extends Activity {
	private ListView appList;
	private LinearLayout loading;
	private List<AppInfo> appInfoList;
	private AppInfoProvider appInfoProvider;
	private List<String> lockedPackageNames;
	private LockDao lockDao;
	private Handler handle = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loading.setVisibility(View.INVISIBLE);
			appList.setAdapter(new LockAdapter());
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appList = (ListView) findViewById(R.id.appList);
		loading = (LinearLayout) findViewById(R.id.loading);
		loading.setVisibility(View.VISIBLE);
		appInfoProvider = new AppInfoProvider(this);
		lockDao = new LockDao(this);
		new Thread(){

			@Override
			public void run() {
				lockedPackageNames = lockDao.findAll();
				appInfoList = appInfoProvider.getAllUserAppInfo();
				handle.sendEmptyMessage(0);
			}
			
		}.start();
		appList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				AppInfo appInfo = appInfoList.get(position);
				ImageView status = (ImageView) view.findViewById(R.id.status);
				TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.2f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				if(lockedPackageNames.contains(appInfo.getPackageName())){
					//采用内容提供者来观察数据库中的数据变�?
					Uri uri = Uri.parse("content://com.example.lock.mylockprovider/lock");
					getContentResolver().delete(uri, null, new String[]{appInfo.getPackageName()});
					status.setImageResource(R.drawable.unlock);
					lockedPackageNames.remove(appInfo.getPackageName());
				}else{
					Uri uri = Uri.parse("content://com.example.lock.mylockprovider/lock");
					ContentValues values = new ContentValues();
					values.put("packageName", appInfo.getPackageName());
					getContentResolver().insert(uri, values);
					status.setImageResource(R.drawable.lock);
					lockedPackageNames.add(appInfo.getPackageName());
				
				}
				view.startAnimation(ta);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;   
	}
	
	private class LockAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return appInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View view;
			if(convertView == null){
				view = View.inflate(getApplicationContext(), R.layout.lock_item, null);
				holder = new ViewHolder();
				holder.appIcon = (ImageView) view.findViewById(R.id.appIcon);
				holder.appName = (TextView) view.findViewById(R.id.appName);
				holder.status = (ImageView) view.findViewById(R.id.status);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			AppInfo appInfo = appInfoList.get(position);
			holder.appIcon.setImageDrawable(appInfo.getAppIcon());
			holder.appName.setText(appInfo.getAppName());
			if(lockedPackageNames.contains(appInfo.getPackageName())){
				holder.status.setImageResource(R.drawable.lock);
			}else{
				holder.status.setImageResource(R.drawable.unlock);
			}
			return view;
		}
		
	}
	
	public static class ViewHolder{
		ImageView appIcon;
		ImageView status;
		TextView  appName;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(getString(R.string.action_settings).equals(item.getTitle().toString())){
			Intent intent = new Intent(this,SettingActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
