package com.example.lockapp.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.lockapp.entity.AppInfo;

public class AppInfoProvider {
	private PackageManager pm;

	public AppInfoProvider(Context context) {
		pm = context.getPackageManager();
	}

	public List<AppInfo> getAllUserAppInfo() {
		List<PackageInfo> pakageInfos = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo pakageInfo : pakageInfos) {
			// pakageInfo.versionName != null
			if (filterApp(pakageInfo.applicationInfo)) {
				AppInfo appInfo = new AppInfo();
				appInfo.setPackageName(pakageInfo.packageName);
				appInfo.setAppName(pakageInfo.applicationInfo.loadLabel(pm)
						.toString());
				appInfo.setVersion(pakageInfo.versionName);
				appInfo.setAppIcon(pakageInfo.applicationInfo.loadIcon(pm));
				appInfo.setUserApp(true);
				appInfos.add(appInfo);
			}

		}
		return appInfos;
	}

	public boolean filterApp(ApplicationInfo info) {
		// 当前应用程序的标记&系统应用程序的标记
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}

}
