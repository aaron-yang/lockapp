package com.example.lockapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LockDBOpenHelper extends SQLiteOpenHelper {

	
	public LockDBOpenHelper(Context context) {
		//参数一：应用上下文，参数二：数据库名称
		//参数三：游标工厂对象，null表示使用系统默认的游标工厂对象，参数四：版本号
		super(context, "lock.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		arg0.execSQL("create table lock (_id integer primary key autoincrement, packageName varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
