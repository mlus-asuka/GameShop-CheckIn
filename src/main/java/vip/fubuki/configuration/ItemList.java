package vip.fubuki.configuration;

import cn.chuanwise.xiaoming.preservable.SimplePreservable;
import vip.fubuki.GameShopPlugin;

import java.util.HashMap;
import java.util.Map;

public class ItemList extends SimplePreservable<GameShopPlugin> {
    Map<String,String> ItemName=new HashMap<>();



    public String getItemName(String Item) {
        return ItemName.get(Item);
    }

    public void setItemName(Map<String, String> itemName) {
        ItemName = itemName;
        readyToSave();
    }
}
