package vip.fubuki.configuration;

import cn.chuanwise.xiaoming.preservable.SimplePreservable;

import java.util.HashMap;
import java.util.Map;

public class GameItemNBT extends SimplePreservable {
    Map<Integer,String> ItemNBT=new HashMap<>();


    public Map<Integer, String> getItemNBT() {
        return ItemNBT;
    }

    public void setItemNBT(Map<Integer, String> itemNBT) {
        ItemNBT = itemNBT;
        readyToSave();
    }

    public void setItemNBT(Integer id, String itemNBT) {
        ItemNBT.put(id,itemNBT) ;
        readyToSave();
    }
}
