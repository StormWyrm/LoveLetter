package cn.qingfeng.loveletter.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import cn.qingfeng.loveletter.db.ContactOpenHelper;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/11/29 11:30
 * @DESC:         缓存联系人
 * @VERSION:      V1.0
 */
public class ContactProvider extends ContentProvider {
    private static final String AUTHORITIES = ContactProvider.class.getCanonicalName();
    private static final int CONTACT = 1;//返回值常量
    public static Uri URI_CONTACT = Uri.parse("content://" + AUTHORITIES + "/contact"); // 对应联系人表的一个uri常量

    private ContactOpenHelper mHelper;
    static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //添加一个匹配规则
        mUriMatcher.addURI(AUTHORITIES, "/contact", CONTACT);
    }

    @Override
    public boolean onCreate() {
        mHelper = new ContactOpenHelper(getContext());
        if (mHelper != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int code = mUriMatcher.match(uri);
        switch (code) {
            case CONTACT:
                SQLiteDatabase database = mHelper.getReadableDatabase();
                // 新插入的id
                long id = database.insert(ContactOpenHelper.T_CONTACT, "", values);
                if (id != -1) {
                    System.out.println("---------ContactsProvider-----insertSuccess--------------");
                    // 拼接最新的uri
                    // content://com.itheima.xmpp_20150807.provider.ContactsProvider/contact/id
                    uri = ContentUris.withAppendedId(uri, id);

                    // 通知ContentObserver数据改变了
                    // 参数1:通知哪个数据库上的监听器
                    // 参数2:当为null的时候通知所有的监听器 不为null的时候只通知指定的观察者
                    getContext().getContentResolver().notifyChange(URI_CONTACT,null);

                }
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int changeNum = 0;
        int code = mUriMatcher.match(uri);
        switch (code) {
            case CONTACT:
                SQLiteDatabase database = mHelper.getReadableDatabase();
                changeNum = database.delete(ContactOpenHelper.T_CONTACT, selection, selectionArgs);
                if (changeNum > 0) {
                    System.out.println("---------ContactsProvider-----deleteSuccess--------------");
                    // 通知ContentObserver数据改变了
                    // getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,"指定只有某一个observer可以收到");//
                    getContext().getContentResolver().notifyChange(ContactProvider.URI_CONTACT, null);// 为null就是所有都可以收到

                }
                break;
        }

        return changeNum;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int changeNum = 0;
        int code = mUriMatcher.match(uri);
        switch (code) {
            case CONTACT:
                SQLiteDatabase database = mHelper.getReadableDatabase();
                changeNum = database.update(ContactOpenHelper.T_CONTACT, values, selection, selectionArgs);
                if (changeNum > 0) {
                    System.out.println("---------ContactsProvider-----updateSuccess--------------");
                    // 通知ContentObserver数据改变了
                    // getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,"指定只有某一个observer可以收到");//
                    getContext().getContentResolver().notifyChange(ContactProvider.URI_CONTACT, null);// 为null就是所有都可以收到


                }
                break;
        }

        return changeNum;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        int code = mUriMatcher.match(uri);
        switch (code) {
            case CONTACT:
                SQLiteDatabase database = mHelper.getReadableDatabase();
                cursor = database.query(ContactOpenHelper.T_CONTACT, projection, selection, selectionArgs
                        , null, null, sortOrder);
                System.out.println("---------ContactsProvider-----querySuccess--------------");
                break;
        }

        return cursor;
    }


}
