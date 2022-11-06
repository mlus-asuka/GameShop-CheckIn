package vip.fubuki.listeners;

import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.event.SimpleListeners;
import cn.chuanwise.xiaoming.minecraft.xiaoming.XMMCXiaoMingPlugin;
import cn.chuanwise.xiaoming.minecraft.xiaoming.event.ServerMessageEvent;
import cn.chuanwise.xiaoming.minecraft.xiaoming.net.OnlineClient;
import cn.chuanwise.xiaoming.minecraft.xiaoming.net.XMMCServerClient;
import lombok.SneakyThrows;
import vip.fubuki.GameShopPlugin;
import vip.fubuki.util.GameItem;
import vip.fubuki.util.LocalJsonUtil;
import vip.fubuki.util.ShopInGame;

import java.util.HashMap;
import java.util.Map;


public class ExtendProtocolListener
    extends SimpleListeners<GameShopPlugin> {

    private static final ExtendProtocolListener INSTANCE = new ExtendProtocolListener();

    public static ExtendProtocolListener getInstance() {
        return INSTANCE;
    }

    public static String SendMap;

    private ExtendProtocolListener() {
    }

    @SneakyThrows
    @EventListener
    public void onServerMessage(ServerMessageEvent event) {
        OnlineClient client=event.getOnlineClient();
        XMMCServerClient serverClient=client.getServerClient();
         String message= event.getMessage();
         boolean ACCESS=GameShopPlugin.getInstance().getGameShop().getEnabledServer().equals(client.getServerInfo().getName());
         Map map= LocalJsonUtil.StringToMap(message);
         if(!map.get("RequestType").toString().isEmpty()) {
             if (!ACCESS) {
                 map.put("Result", "DENIED");
                 serverClient.sendMessage(map.toString());
             } else {
                 map.put("Result", "ACCEPT");

                 serverClient.sendMessage(map.toString());
                 if (map.get("RequestType").toString().equals("buy")) {
                     int id=Integer.parseInt(map.get("ID").toString());
                     int amount=Integer.parseInt(map.get("amount").toString());
                     String player=(String) map.get("Player");
                     if(GameShopPlugin.getInstance().getGameShop().getGood(id)!=null) {
                         ShopInGame shop = new ShopInGame();
                         Map<String, String> responseMap = new HashMap<>();
                         String response = shop.AutoDeliver(player, id, amount);
                         serverClient.sendMessage(SendMap);
                         responseMap.put("Response", response);
                         responseMap.put("Player", player);
                         serverClient.sendMessage(responseMap.toString());
                     }
                     else{
                         Map<String, String> responseMap = new HashMap<>();
                         responseMap.put("Response", "请求的商品ID不存在。");
                         responseMap.put("Player", player);
                         serverClient.sendMessage(responseMap.toString());
                     }
                 } else if (map.get("RequestType").toString().equals("sell")) {
                     int Index = GameShopPlugin.getInstance().getGameShop().getGoods().size();
                     GameItem NewGood = new GameItem();
                     NewGood.setID(Index);
                     String name =LocalJsonUtil.regex2((String)map.get("id"));
                     NewGood.setItem_name(name);
                     Integer Price = Integer.parseInt(map.get("price").toString());
                     NewGood.setPrice(Price);
                     String Player = (String) map.get("Player");
                     NewGood.setSellerName(Player);
                     Integer Amount = Integer.parseInt(map.get("amount").toString());
                     NewGood.setAmount(Amount);
                     long userQQ =
                             (long) XMMCXiaoMingPlugin.getInstance().getPlayerConfiguration()
                                     .getPlayerInfo(Player).get().getAccountCodes().toArray()[0];
                     NewGood.setSeller(userQQ);
                     GameShopPlugin.getInstance().getGameShop().setGoods(Index, NewGood);
                     GameShopPlugin.getInstance().getItemNBT().setItemNBT(Index, map.get("NBT").toString());
                     getXiaoMingBot().getContactManager().getPrivateContactPossibly(userQQ).get(0).
                             sendMessage("成功上架一样名称为:" + name + ",价格为:" + Price + ",存量为:" + Amount + "的货品，ID:" + Index);
                 }
             }
         }
    }
}
