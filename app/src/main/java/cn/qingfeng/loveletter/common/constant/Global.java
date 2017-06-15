package cn.qingfeng.loveletter.common.constant;


import cn.qingfeng.loveletter.common.AppApplication;
import cn.qingfeng.loveletter.common.util.SPUtil;

/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/12/1 8:28
 * @DESC: 用于记录当前的ip 端口等信息
 * @VERSION: V1.0
 */
public class Global {
    public static final String HOST = (String) SPUtil.get(AppApplication.getInstance(), "ip", "192.168.0.1");
    public static final int PORT = 5222;
}
