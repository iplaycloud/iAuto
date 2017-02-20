package com.tchip.autoprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AutoProvider extends ContentProvider {

	private AutoStateDbHelper autoStateDbHelper;

	private static final UriMatcher VIDEO_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	private static final int STATE = 0;
	private static final int STATE_NAME = 1;

	private static final String URI_PREFIX = "com.tchip.provider.AutoProvider";

	static {
		VIDEO_MATCHER.addURI(URI_PREFIX, "state", STATE);
		VIDEO_MATCHER.addURI(URI_PREFIX, "state/name/*", STATE_NAME);
	}

	@Override
	public boolean onCreate() {
		this.autoStateDbHelper = new AutoStateDbHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = autoStateDbHelper.getReadableDatabase();
		switch (VIDEO_MATCHER.match(uri)) {
		case STATE_NAME:
			String name = uri.getPathSegments().get(2);
			String nameWhere = "name='" + name + "'";
			return db.query(AutoStateDbHelper.TABLE_STATE, projection,
					nameWhere, selectionArgs, null, null, sortOrder);

		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri.toString());
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = autoStateDbHelper.getReadableDatabase();
		switch (VIDEO_MATCHER.match(uri)) {
		case STATE_NAME:
			return uri;

		case STATE:
			String name = values.getAsString("name");
			String value = values.getAsString("value");
			if (name != null && name.trim().length() > 0 && value != null
					&& value.trim().length() > 0) {
				long id = db
						.insert(AutoStateDbHelper.TABLE_STATE, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return ContentUris.withAppendedId(uri, id);
			} else {
				return uri;
			}

		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri.toString());
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = autoStateDbHelper.getReadableDatabase();
		switch (VIDEO_MATCHER.match(uri)) {
		case STATE_NAME:
			try {
				int count = db.update(AutoStateDbHelper.TABLE_STATE, values,
						selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			} catch (Exception e) {
				return 0;
			}

		case STATE:
			// int count = db.update(AutoStateDbHelper.TABLE_STATE, values,
			// selection, selectionArgs);
			// getContext().getContentResolver().notifyChange(uri, null);
			return 0;

		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri.toString());
		}
	}

	/**
	 * 该方法用于返回当前Url所代表数据的MIME类型。
	 * 如果操作的数据属于集合类型，那么MIME类型字符串应该以vnd.android.cursor.dir/开头
	 * 如果要操作的数据属于非集合类型数据，那么MIME类型字符串应该以vnd.android.cursor.item/开头
	 */
	@Override
	public String getType(Uri uri) {
		switch (VIDEO_MATCHER.match(uri)) {
		case STATE:
			return "vnd.android.cursor.item/state";

		case STATE_NAME:
			return "vnd.android.cursor.item/state/name";

		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
	}

}
