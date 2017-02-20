package com.tchip.autoprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AutoStateDbHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "AutoState.db";

	public static final String TABLE_STATE = "state";
	private static final String COL_NAME = "name"; // 字段名称
	private static final String COL_VALUE = "value"; // 内容

	private static final String[] STATE_COL_PROJECTION = new String[] {
			COL_NAME, COL_VALUE, };

	public AutoStateDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Create table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlLine = "CREATE TABLE " + TABLE_STATE + " (" + COL_NAME
				+ " TEXT PRIMARY KEY," + COL_VALUE + " INTEGER" + ");";

		db.execSQL(sqlLine);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);
		onCreate(db); // Create tables again
	}

	public int addAutoState(AutoState autoSate) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		String name = autoSate.getName();
		String value = autoSate.getValue();
		if (hasValue(name)) {
			values.put(COL_VALUE, value);

			return db.update(TABLE_STATE, values, COL_NAME + "=?",
					new String[] { name });
		} else {
			values.put(COL_NAME, name);
			values.put(COL_VALUE, value);
			long rowId = db.insert(TABLE_STATE, null, values); // Insert to
			db.close();
			return (int) rowId;
		}
	}

	public boolean hasValue(String name) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, STATE_COL_PROJECTION, COL_NAME
				+ "=?", new String[] { name }, null, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			// Step step = new Step(cursor.getInt(0), cursor.getString(1));
			// int stepCount = cursor.getInt(1);
			cursor.close();
			return true; // step.count;
		} else {
			cursor.close();
			return false;
		}
	}

	public String getValue(String name) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, STATE_COL_PROJECTION, COL_NAME
				+ "=?", new String[] { name }, null, null, null, null);
		String value = "";
		if (cursor != null && cursor.moveToFirst()) {
			// Step step = new Step(cursor.getInt(0), cursor.getString(1));
			// int stepCount = cursor.getInt(1);
			value = cursor.getString(1);
		}
		cursor.close();
		return value;
	}

}
