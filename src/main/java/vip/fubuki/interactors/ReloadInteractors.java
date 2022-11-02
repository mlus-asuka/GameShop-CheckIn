package vip.fubuki.interactors;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import vip.fubuki.GameShopPlugin;

public class ReloadInteractors extends SimpleInteractors {
    @Required("checkin.admin.reload")
    @Filter("(重载|reload)游戏商店")
    public void Reload(XiaoMingUser user){
        GameShopPlugin.getInstance().reload();
        user.sendMessage("GameShop Reload Successful");
    }
}
