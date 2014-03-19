package com.example.lockapp.provider;

import com.example.lockapp.db.LockDBOpenHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class LockProvider extends ContentProvider {
	
	private LockDBOpenHelper helper;
	public static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		matcher.addURI("com.example.lock.mylockprovider", "lock", 1);
	}
	@Override
	public boolean onCreate() {
		helper = new LockDBOpenHelper(getContext());
		return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = helper.getWritableDatabase();  
        Uri insertUri = null;  
        switch (matcher.match(uri)) {  
        case 1:  
            long rowid = db.insert("lock", "packageName", values);  
            insertUri = ContentUris.withAppendedId(uri, rowid);  
            break;  
        default:  
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());  
        }  
        return insertUri;  
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		 SQLiteDatabase db = helper.getWritableDatabase();  
	        int count = 0;  
	        switch (matcher.match(uri)) {  
	        case 1:  
	            count = db.delete("lock", selection, selectionArgs);  
	            return count;  
	        default:  
	            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());  
	        }  
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}


}
