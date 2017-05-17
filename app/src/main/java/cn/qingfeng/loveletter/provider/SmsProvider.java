package cn.qingfeng.loveletter.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import cn.qingfeng.loveletter.dbhelper.SmsOpenHelper;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/11/29 11:31
 * @DESC:         缓存消息
 * @VERSION:      V1.0
 */
public class SmsProvider extends ContentProvider {
    private static final String AUTHORITY = SmsProvider.class.getCanonicalName();
    public static final Uri URI_SMS = Uri.parse("content://" + AUTHORITY + "/sms") ;//对应表的uri常量
    public static final Uri URI_SESSION = Uri.parse("content://" + AUTHORITY + "/session") ;

    private static final int SMS = 1;
    private static final int SESSION = 2;


    private SmsOpenHelper smsOpenHelper;
    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "/sms", SMS);
        uriMatcher.addURI(AUTHORITY, "/session", SESSION);
    }

    @Override
    public boolean onCreate() {
        smsOpenHelper = new SmsOpenHelper(getContext());
        if (smsOpenHelper != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = uriMatcher.match(uri);
        switch (code) {
            case SMS:
                SQLiteDatabase database = smsOpenHelper.getReadableDatabase();
                long id = database.insert(SmsOpenHelper.T_SMS, "", values);
                if(id > 0){
                    System.out.println("-------SmsProvider insert------");
                    uri = ContentUris.withAppendedId(URI_SMS,id);
                    getContext().getContentResolver().notifyChange(URI_SMS,null);
                }
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int changeNum = 0;
        int code = uriMatcher.match(uri);
        switch (code) {
            case SMS:
                SQLiteDatabase database = smsOpenHelper.getReadableDatabase();
                changeNum = database.delete(SmsOpenHelper.T_SMS,selection,selectionArgs);
                if(changeNum > 0){
                    System.out.println("-------SmsProvider delete------");
                    getContext().getContentResolver().notifyChange(URI_SMS,null);
                }
                break;
        }
        return changeNum;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int changeNum = 0;
        int code = uriMatcher.match(uri);
        switch (code) {
            case SMS:
                SQLiteDatabase database = smsOpenHelper.getReadableDatabase();
                changeNum = database.update(SmsOpenHelper.T_SMS,values,selection,selectionArgs);
                if(changeNum > 0){
                    System.out.println("-------SmsProvider update------");
                    getContext().getContentResolver().notifyChange(URI_SMS,null);
                }
                break;
        }
        return changeNum;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        int code = uriMatcher.match(uri);
        switch (code){
            case SMS:
                SQLiteDatabase database = smsOpenHelper.getReadableDatabase();
                cursor = database.query(SmsOpenHelper.T_SMS, projection, selection, selectionArgs, null, null, sortOrder);
                System.out.println("-------SmsProvider query------");
                break;
            case SESSION:
                SQLiteDatabase db = smsOpenHelper.getReadableDatabase();
                cursor = db.rawQuery("SELECT * FROM "
                        + "(SELECT * FROM t_sms WHERE my_account = ?)"
                        + " GROUP BY session_account ORDER BY time desc", selectionArgs);
        }
        return cursor;
    }

}
