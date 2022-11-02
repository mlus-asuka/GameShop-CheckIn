package vip.fubuki.interactors;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import vip.fubuki.GameShopPlugin;

public class EnableGameShopInteractors extends SimpleInteractors {
    @Required("checkin.admin.enable")
    @Filter("(设置|set)游戏商店 {服务器}")
    public void enableGroup(XiaoMingUser user, @FilterParameter("服务器") String onlineClient) {
        GameShopPlugin.getInstance().getGameShop().setEnabledServer(onlineClient);
        user.sendMessage("成功设置商店对应服务器为:"+onlineClient);
    }
    }
