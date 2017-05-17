package cn.qingfeng.loveletter.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/11/29 10:03
 * @DESC: 记录本地登录过的账号
 * @VERSION: V1.0
 */
public class AccountOpenHelper extends SQLiteOpenHelper {
    public static final String T_ACCOUNT = "t_account";//数据库的名字

    public class AccountTable implements BaseColumns {
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }

    public AccountOpenHelper(Context context) {
        super(context, "account.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE t_account (_id INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT,password TEXT)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
