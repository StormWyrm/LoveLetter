package cn.qingfeng.loveletter.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import cn.qingfeng.loveletter.db.AccountOpenHelper;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/11/29 11:30
 * @DESC:         登录过的账号的缓存
 * @VERSION:      V1.0
 */
public class AccountProvider extends ContentProvider {
    public static final String AUTHORITIES = AccountProvider.class.getCanonicalName();
    public static final Uri URI_ACCOUNT = Uri.parse("content://" + AUTHORITIES + "/account");
    private static final int ACCOUNT = 1;//匹配后的返回指常量
    private static UriMatcher mUriMatcher;

    private AccountOpenHelper mOpenHelper;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITIES, "/account", ACCOUNT);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AccountOpenHelper(getContext());
        if (mOpenHelper != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
       int code = mUriMatcher.match(uri);
        switch (code){
            case ACCOUNT:
                SQLiteDatabase dataBase = mOpenHelper.getReadableDatabase();
                long changedNum = dataBase.insert(AccountOpenHelper.T_ACCOUNT, "", values);
                if(changedNum > 0){
                    System.out.println("------AccountProvider insert------");
                    uri = ContentUris.withAppendedId(uri,changedNum);
                    getContext().getContentResolver().notifyChange(URI_ACCOUNT,null);
                }
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = mUriMatcher.match(uri);
        int changedNum = 0;
        switch (code){
            case ACCOUNT:
                SQLiteDatabase dataBase = mOpenHelper.getReadableDatabase();
                changedNum = dataBase.delete(AccountOpenHelper.T_ACCOUNT,selection,selectionArgs);
                if(changedNum > 0){
                    System.out.println("------AccountProvider delete------");
                    getContext().getContentResolver().notifyChange(URI_ACCOUNT,null);
                }
                break;
        }
        return changedNum;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int code = mUriMatcher.match(uri);
        int changedNum = 0;
        switch (code){
            case ACCOUNT:
                SQLiteDatabase dataBase = mOpenHelper.getReadableDatabase();
                changedNum = dataBase.update(AccountOpenHelper.T_ACCOUNT,values,selection,selectionArgs);
                if(changedNum > 0){
                    System.out.println("------AccountProvider insert------");
                    getContext().getContentResolver().notifyChange(URI_ACCOUNT,null);
                }
                break;
        }
        return changedNum;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        int code = mUriMatcher.match(uri);
        Cursor cursor = null;
        switch (code){
            case ACCOUNT:
                SQLiteDatabase dataBase = mOpenHelper.getReadableDatabase();
                cursor = dataBase.query(AccountOpenHelper.T_ACCOUNT,projection,selection,selectionArgs,null,null,sortOrder);
                break;
        }
        return cursor;
    }


}
