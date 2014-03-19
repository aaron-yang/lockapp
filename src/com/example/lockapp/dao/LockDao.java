package com.example.lockapp.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lockapp.db.LockDBOpenHelper;


public class LockDao {
	private LockDBOpenHelper helper;
	
	public LockDao(Context context) {
		helper = new LockDBOpenHelper(context);
	}
	
	public boolean add(String packageName) {
		if (find(packageName))
			return false;
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			//执行添加的SQL语句
			db.execSQL("insert into lock (packageName) values (?)",
					new String[] { packageName });
			db.close();
		}
		return find(packageName);
	}
	
	public void delete(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			//执行删除的SQL语句
			db.execSQL("delete from lock where packageName=?",
					new String[] { packageName });
			db.close();
		}
	}
	
	
	public List<String> findAll() {
		List<String> packageNames = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select packageName from lock",
					null);
			while (cursor.moveToNext()) {
				packageNames.add(cursor.getString(0));
			}
			cursor.close();
			db.close();
		}
		return packageNames;
	}
	
	
	public boolean find(String packageName) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("select packageName from lock where package=?"+packageName,
					null);
			if(cursor.moveToNext()){
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}
	
}
