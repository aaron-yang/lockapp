package com.example.lockapp.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.example.lockapp.KeyActivity;
import com.example.lockapp.dao.LockDao;

public class LockService extends Service {
	private List<String> tempUnlockPackageNames;
	private List<String> lockPackageNames;
	private LockDao dao;
	boolean flag;
	private MyObserver observer;
	private LockScreenReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new LocalBinder();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse("com.example.lock.mylockprovider/lock");
		observer = new MyObserver(new Handler());
		// 第二个参数如果为true，Uri中的content://com.guoshisp.applock/匹配正确即可感应到，后面的（ADD或DELETE）不用继续在匹配下去
		getContentResolver().registerContentObserver(uri, true, observer);

		// 以代码动态注册一个广播接收者
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		receiver = new LockScreenReceiver();
		// 采用代码动态的注册广播接受者.
		registerReceiver(receiver, filter);

		super.onCreate();
		tempUnlockPackageNames = new ArrayList<String>();
		dao = new LockDao(this);
		lockPackageNames = dao.findAll();
		flag = true;
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				while (flag) {
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					RunningTaskInfo taskinfo = am.getRunningTasks(1).get(0);
					String packageName = taskinfo.topActivity.getPackageName();
					if (tempUnlockPackageNames.contains(packageName)) {
						try {
							// 服务非常耗电，这里用于让该服务暂停200毫秒
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}

					if (lockPackageNames.contains(packageName)) {
						Intent intent = new Intent(LockService.this,
								KeyActivity.class);
						// 因为服务本身没有任务栈，如果要开启一个需要在任务栈中运行的Activity的话，需要为该Activity创建一个任务栈。
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("packageName", packageName);
						startActivity(intent);
					}
					try {
						// 服务非常耗电，这里用于让该服务暂停200毫秒
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		}.start();
	}

	public class LocalBinder extends Binder {
		public LockService getService() {
			return LockService.this;
		}
	}

	public void addToTempUnlockPagekageNames(String packageName) {
		// 将需要临时停止保护的程序的包名添加到对应的集合中
		tempUnlockPackageNames.add(packageName);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		flag = false;
		getContentResolver().unregisterContentObserver(observer);
		observer = null;
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private class MyObserver extends ContentObserver {
		public MyObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			// 重新从数据库中获取数据
			lockPackageNames = dao.findAll();
			super.onChange(selfChange);
		}
	}

	private class LockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			tempUnlockPackageNames.clear();
		}

	}

}
