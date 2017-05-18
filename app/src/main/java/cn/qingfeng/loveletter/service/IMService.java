package cn.qingfeng.loveletter.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.util.PinyinUtil;
import cn.qingfeng.loveletter.common.util.ThreadUtil;
import cn.qingfeng.loveletter.db.ContactOpenHelper;
import cn.qingfeng.loveletter.db.SmsOpenHelper;
import cn.qingfeng.loveletter.provider.ContactProvider;
import cn.qingfeng.loveletter.provider.SmsProvider;
import cn.qingfeng.loveletter.chat.ChatActivity;
import cn.qingfeng.loveletter.common.util.SPUtil;
import cn.qingfeng.loveletter.common.util.ToastUtil;


/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/11/28 21:15
 * @DESC: 后台服务类
 * @VERSION: V1.0
 */


//在服务中更新数据的好处：
//        1.当应用程序退出的时候 服务依旧可以在后台更新数据
//        2.当后台服务关闭的时候 我们才会再一次向服务器请求数据
//          而不是每一次打开应用的时候都进行数据的更新
public class IMService extends Service {
    public static String ACCOUNT;//记录当前登录的账号
    public static XMPPConnection conn;//当前登录的连接
    private static Map<String, Chat> mChatMap = new HashMap<>();

    //获取联系人
    private Roster mRoster;//所有联系人的集合
    private MyRosterListener mRosterListener;//监听联系人变化的监听器

    //发送消息
    private Chat mChat;
    private ChatManager mChatManager;
    private MyMessageListener mMessageListener;
    private MyChatManagerListener mMyChatManagerListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public void sendMessage(Message message) {
            IMService.this.sendMessage(message);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("--------------同步花名册 begin--------------");
        ThreadUtil.runOnThread(new Runnable() {
            @Override
            public void run() {
                // 得到花名册对象
                if (conn != null) {
                    mRoster = conn.getRoster();
                    if (mRoster != null) {
                        // 得到所有的联系人
                        final Collection<RosterEntry> entries = mRoster.getEntries();

                        // 监听联系人的改变
                        mRosterListener = new MyRosterListener();
                        mRoster.addRosterListener(mRosterListener);

                        for (RosterEntry entry : entries) {
                            saveOrUpdateEntry(entry);
                        }
                    }
                }
            }
        });
        System.out.println("--------------同步花名册 end--------------");


        System.out.println("--------------接受聊天监听器 begin--------------");
        if (conn != null) {
            //1.获取消息的管理者 ChatManager
            if (mChatManager == null)
                mChatManager = conn.getChatManager();

            if (mMessageListener == null) {
                mMessageListener = new MyMessageListener();
            }

            if (mMyChatManagerListener == null) {
                mMyChatManagerListener = new MyChatManagerListener();
            }
            //注册聊天监听器
            mChatManager.addChatListener(mMyChatManagerListener);

        }


        System.out.println("--------------好友添加监听器 begin--------------");
        if (conn != null) {
            conn.addPacketListener(
                    new PacketListener() {
                        @Override
                        public void processPacket(Packet packet) {
                            //接收到添加好友申请
                            ToastUtil.showToastSafe(getApplication(), "好友添加申请");
                        }
                    },
                    new PacketFilter() {
                        @Override
                        public boolean accept(Packet packet) {
                            if (packet instanceof Presence) {
                                Presence presence = (Presence) packet;
                                if (presence.getType().equals(Presence.Type.subscribe)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("--------------service onStartCommand--------------");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        System.out.println("--------------service onDestroy--------------");
        // 移除rosterListener
        if (mRoster != null && mRosterListener != null) {
            mRoster.removeRosterListener(mRosterListener);
        }

        super.onDestroy();
    }

    /**
     * 更新或插入联系人数据库
     */
    private void saveOrUpdateEntry(RosterEntry entry) {
        //插入数据库
        String account = filterAccount(entry.getUser());
        String nickname = entry.getName();
        if (TextUtils.isEmpty(nickname)) {
            nickname = account.substring(0, account.indexOf("@"));
        }
        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, account);
        values.put(ContactOpenHelper.ContactTable.NICKNAME, nickname);
        values.put(ContactOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactOpenHelper.ContactTable.PINYIN, PinyinUtil.getPinyin(nickname));
        values.put(ContactOpenHelper.ContactTable.MY_ACCOUNT, IMService.ACCOUNT);
        // 先update,后插入-->重点
        int updateCount =
                getContentResolver().update(ContactProvider.URI_CONTACT, values,
                        ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
        if (updateCount <= 0) {// 没有更新到任何记录
            getContentResolver().insert(ContactProvider.URI_CONTACT, values);
        }
    }

    /**
     * 发送消息
     */
    public void sendMessage(Message message) {
        //2.创建聊天对象 Chat
        String accountTo = message.getTo();
        if (mChatMap.get(accountTo) == null) {
            mChat = mChatManager.createChat(accountTo, mMessageListener);
            mChatMap.put(accountTo, mChat);
        } else {
            mChat = mChatMap.get(accountTo);
        }

        try {
            mChat.sendMessage(message);
            saveMessage(getApplicationContext(), message, message.getTo(), message.getFrom());
        } catch (XMPPException e) {
            e.printStackTrace();
            ToastUtil.showToastSafe(getApplicationContext(), "发送失败,请检查网络");
        }
    }

    /**
     * 缓存消息到本地数据库中
     */
    private void saveMessage(Context context, Message msg, String sessionAccount, String myAccount) {
        ContentValues values = new ContentValues();

        //对账号进行处理
        String fromAccount = filterAccount(msg.getFrom());
        String toAccount = filterAccount(msg.getTo());
        sessionAccount = filterAccount(sessionAccount);
        myAccount = filterAccount(myAccount);

        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, fromAccount);
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, toAccount);
        values.put(SmsOpenHelper.SmsTable.BODY, msg.getBody());
        values.put(SmsOpenHelper.SmsTable.STATUS, "offline");
        values.put(SmsOpenHelper.SmsTable.TYPE, "chat");
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, sessionAccount);
        values.put(SmsOpenHelper.SmsTable.MY_ACCOUNT, myAccount);
        context.getContentResolver().insert(SmsProvider.URI_SMS, values);
    }

    /**
     * 显示通知
     */
    private void showNotification(String from, String message) {
        //初始化延迟意图
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ContactOpenHelper.ContactTable.ACCOUNT, from + "@" + conn.getServiceName());
        intent.putExtra(ContactOpenHelper.ContactTable.NICKNAME, from);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);

        //初始化通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));//设置下拉的图标
        builder.setSmallIcon(R.mipmap.ic_launcher);//小图标
        builder.setContentTitle(from);//设置标题
        builder.setContentText(message);//内容
        builder.setContentIntent(pendingIntent);//设置点击事件
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setOngoing(false);//是否为一个正在进行的通知
        builder.setPriority(Notification.PRIORITY_DEFAULT);//设置优先级
        builder.setAutoCancel(true);//设置点击后关闭
        builder.setDefaults(Notification.DEFAULT_LIGHTS);//设置默认灯光
        if ((Boolean) SPUtil.get(this, "isShowMessageDetail", true)) {
            builder.setTicker(message);//出现的时候显示在状态栏
        }
        if((Boolean) SPUtil.get(this,"isShowMessageVoice",true)){
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }
        if((Boolean) SPUtil.get(this,"isShowMessageShock",true)){
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        }




        //发出通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }


    /**
     * 获取账户的完整名称
     */
    private String filterAccount(String account) {
        return account.substring(0, account.indexOf("@")) + "@" + conn.getServiceName();
    }


    /**
     * 添加联系人数据改变的监听器
     */
    private class MyRosterListener implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> addresses) {// 联系人添加了
            System.out.println("--------------entriesAdded--------------");
            // 对应更新数据库
            for (String address : addresses) {
                RosterEntry entry = mRoster.getEntry(address);
                // 要么更新,要么插入
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {// 联系人修改了
            System.out.println("--------------entriesUpdated--------------");
            // 对应更新数据库
            for (String address : addresses) {
                RosterEntry entry = mRoster.getEntry(address);
                // 要么更新,要么插入
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {// 联系人删除了
            System.out.println("--------------entriesDeleted--------------");
            // 对应更新数据库
            for (String account : addresses) {
                // 执行删除操作
                getContentResolver().delete(ContactProvider.URI_CONTACT,
                        ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
            }

        }

        @Override
        public void presenceChanged(Presence presence) {// 联系人状态改变
            System.out.println("--------------presenceChanged--------------");
        }
    }

    /**
     * 接收到消息改变的监听器
     */
    private class MyMessageListener implements MessageListener {
        @Override
        public void processMessage(Chat chat, final Message message) {
            final String nickname = message.getFrom().substring(0, message.getFrom().indexOf("@"));

            //收到消息 将消息缓存到本地 并显示通知
            ThreadUtil.runOnThread(new Runnable() {
                @Override
                public void run() {
                    //缓存消息
                    saveMessage(getApplicationContext(), message, message.getFrom(), message.getTo());
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNotification(nickname, message.getBody());//显示通知
                        }
                    });
                }
            });
        }
    }

    //聊天监听器
    private class MyChatManagerListener implements ChatManagerListener {

        @Override
        public void chatCreated(Chat chat, boolean b) {
            System.out.println("--------------chatCreated--------------");

            // 判断chat是否存在map里面
            String participant = chat.getParticipant();// 和我聊天的那个人

            // 因为别人创建和我自己创建,参与者(和我聊天的人)对应的jid不同.所以需要统一处理
            participant = filterAccount(participant);

            if (!mChatMap.containsKey(participant)) {
                // 保存chat
                mChatMap.put(participant, chat);
                chat.addMessageListener(mMessageListener);
            }
            System.out.println("participant:" + participant);

            if (b) {// true
                System.out.println("-------------- 我创建了一个chat--------------");
                // participant:hm1@itheima.com admin@itheima.com hm1@itheima.com
            } else {// false
                System.out.println("-------------- 别人创建了一个chat--------------");
                // participant:hm1@itheima.com/Spark 2.6.3 admin@itheima.com hm1@itheima.com
            }
        }
    }
}


