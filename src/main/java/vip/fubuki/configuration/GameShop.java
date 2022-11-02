package vip.fubuki.configuration;

import cn.chuanwise.xiaoming.preservable.SimplePreservable;
import lombok.Data;
import vip.fubuki.GameShopPlugin;
import vip.fubuki.util.GameItem;

import java.util.HashMap;
import java.util.Map;

@Data
public class GameShop extends SimplePreservable<GameShopPlugin> {
    Map<Integer, GameItem> ItemList = new HashMap<>();
    Map<Integer,String> EnabledServer=new HashMap<>();

    public String getEnabledServer() {
        return EnabledServer.get(0);
    }

    public void setEnabledServer(String enabledServer) {
        EnabledServer.put(0,enabledServer);
        readyToSave();
    }

    public Map<Integer, GameItem> getGoods() {
        return ItemList;
    }

    public void setItemList(Map<Integer, GameItem> itemList) {
        ItemList = itemList;
    }

    public GameItem getGood(Integer id){
        return ItemList.get(id);
    }

    public void setGoods(Integer id, GameItem gameitem) {
        ItemList.put(id,gameitem);
        readyToSave();
    }
}
