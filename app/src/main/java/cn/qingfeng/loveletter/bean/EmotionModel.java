package cn.qingfeng.loveletter.bean;

/**
 * @AUTHER:       李青峰 
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:26
 * @DESC:         聊天表情的model
 * @VERSION:      V1.0
 */  
public class EmotionModel {
    public String name;
    public int iconRes;

    public EmotionModel(String name, int iconRes) {
        this.name = name;
        this.iconRes = iconRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}