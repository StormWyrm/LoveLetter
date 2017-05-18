package cn.qingfeng.loveletter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:28
 * @DESC:         创建数据库
 * @VERSION:      V1.0
 */
public class ContactOpenHelper extends SQLiteOpenHelper {
    public static final String T_CONTACT = "t_contact";//数据库的表名

    //实现这个类 他会帮我们创建_id,_count这两列
    public class ContactTable implements BaseColumns {
        public static final String ACCOUNT = "account";//账号
        public static final String NICKNAME = "nickname"; //别名
        public static final String AVATAR = "avatar"; //头像
        public static final String PINYIN = "pinyin"; //拼音
        public static final String MY_ACCOUNT="my_account";//表示这是谁的联系人
    }

    public ContactOpenHelper(Context context) {
        super(context, "contact.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+T_CONTACT+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ContactTable.ACCOUNT+" TEXT, "
                + ContactTable.NICKNAME+" TEXT, "
                + ContactTable.AVATAR+" TEXT, "
                + ContactTable.PINYIN+" TEXT,"
                + ContactTable.MY_ACCOUNT+" TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
