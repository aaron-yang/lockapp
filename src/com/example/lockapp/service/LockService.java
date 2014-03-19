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
		// �ڶ����������Ϊtrue��Uri�е�content://com.guoshisp.applock/ƥ����ȷ���ɸ�Ӧ��������ģ�ADD��DELETE�����ü�����ƥ����ȥ
		getContentResolver().registerContentObserver(uri, true, observer);

		// �Դ��붯̬ע��һ���㲥������
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		receiver = new LockScreenReceiver();
		// ���ô��붯̬��ע��㲥������.
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
							// ����ǳ��ĵ磬���������ø÷�����ͣ200����
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}

					if (lockPackageNames.contains(packageName)) {
						Intent intent = new Intent(LockService.this,
								KeyActivity.class);
						// ��Ϊ������û������ջ�����Ҫ����һ����Ҫ������ջ�����е�Activity�Ļ�����ҪΪ��Activity����һ������ջ��
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("packageName", packageName);
						startActivity(intent);
					}
					try {
						// ����ǳ��ĵ磬���������ø÷�����ͣ200����
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
		// ����Ҫ��ʱֹͣ�����ĳ���İ�����ӵ���Ӧ�ļ�����
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
			// ���´����ݿ��л�ȡ����
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
