package cn.qingfeng.loveletter.ui.fragment;

import java.util.HashMap;
import java.util.Map;

import cn.qingfeng.loveletter.ui.base.BaseFragment;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:37
 * @DESC:         用于缓存fragment
 * @VERSION:      V1.0
 */
public class FragmentFactory {
    private static Map<Integer,BaseFragment> map = new HashMap<>();
    public static BaseFragment getFragment(int position){
        BaseFragment fragment = map.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new MessageFragment();
                    map.put(0, fragment);
                    break;
                case 1:
                    fragment = new ContactFragment();
                    map.put(1, fragment);
                    break;
                case 2:
                    fragment = new MeFragment();
                    map.put(3, fragment);
                    break;
            }
        }
        return fragment;
    }
    public static void clearAll(){
        map.clear();
    }
}
