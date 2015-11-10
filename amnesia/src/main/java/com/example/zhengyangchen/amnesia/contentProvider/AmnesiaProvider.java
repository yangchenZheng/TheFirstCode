package com.example.zhengyangchen.amnesia.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.zhengyangchen.amnesia.bean.Alarms;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.dao.MemoDB;


/**
 * 内容提供器
 */
public class AmnesiaProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.zhengyangchen.amnesia.provider";
    public static final Uri URI_MEMO_ALL = Uri.parse("content://" + AUTHORITY + "/memo");
    public static final Uri URI_ALARMS_ALL = Uri.parse("content://" + AUTHORITY + "/alarms");
    private static UriMatcher uriMatcher;
    private SQLiteDatabase mDb;
    public static final int MEMO_ALL = 0;
    public static final int MEMO_ONE = 1;
    public static final int ALARMS_ALL = 2;
    public static final int ALARMS_ONE = 3;


    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "memo", MEMO_ALL);
        uriMatcher.addURI(AUTHORITY, "memo/#", MEMO_ONE);
        uriMatcher.addURI(AUTHORITY, "alarms", ALARMS_ALL);
        uriMatcher.addURI(AUTHORITY, "alarms/#", ALARMS_ONE);
    }

    @Override
    public boolean onCreate() {
        mDb = MemoDB.getInstance(getContext()).db;
        return true;//表示初始化成功
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        int matcher = uriMatcher.match(uri);
        switch (matcher) {
            case MEMO_ALL:
                cursor = mDb.query(Memo.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), URI_MEMO_ALL);
                break;
            case MEMO_ONE:
                long id = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(id)};
                cursor = mDb.query(Memo.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), URI_MEMO_ALL);
                break;
            case ALARMS_ALL:
                cursor = mDb.query(Alarms.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), URI_ALARMS_ALL);
                break;
            case ALARMS_ONE:
                long id1 = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(id1)};
                cursor = mDb.query(Alarms.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), URI_ALARMS_ALL);
                break;
            default:
                throw new IllegalArgumentException("wrong uri:" + uri);//抛出异常
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int matcher = uriMatcher.match(uri);
        Uri uriReturn = null;
        switch (matcher) {
            case MEMO_ALL:
                long rowId = mDb.insert(Memo.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    uriReturn = ContentUris.withAppendedId(uri, rowId);
                    notifyDataSetChanged(URI_MEMO_ALL);
                    Log.d("zyc", "not run");
                }
                break;
            case ALARMS_ALL:
                long rowId1 = mDb.insert(Alarms.TABLE_NAME, null, contentValues);
                if (rowId1 > 0) {
                    notifyDataSetChanged(URI_ALARMS_ALL);
                    uriReturn = ContentUris.withAppendedId(uri, rowId1);
                }
                break;
            default:
                throw new IllegalArgumentException("wrong uri:" + uri);
        }

        return uriReturn;
    }

    private void notifyDataSetChanged(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteRows = 0;
        int matcher = uriMatcher.match(uri);
        switch (matcher) {
            case MEMO_ONE:
                long id = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(id)};
                deleteRows = mDb.delete(Memo.TABLE_NAME, selection, selectionArgs);
                if (deleteRows > 0) {
                    notifyDataSetChanged(URI_MEMO_ALL);//通知数据发生改变
                }
                break;
            case ALARMS_ONE:
                long id1 = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(id1)};
                deleteRows = mDb.delete(Alarms.TABLE_NAME, selection, selectionArgs);
                if (deleteRows > 0) {
                    notifyDataSetChanged(URI_ALARMS_ALL);
                }
                break;
            default:
                throw new IllegalArgumentException("wrong uri:" + uri);
        }
        return deleteRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selections) {
        int updateRows = 0;
        switch (uriMatcher.match(uri)) {
            case ALARMS_ONE:
                String alarmId = uri.getPathSegments().get(1);
                selections = new String[]{String.valueOf(alarmId)};
                updateRows = mDb.update(Alarms.TABLE_NAME, contentValues, "_id = ?", selections);
                notifyDataSetChanged(URI_ALARMS_ALL);
                break;
            default:
                throw new IllegalArgumentException("Uri is wrong" + uri);
        }
        return updateRows;
    }
}
