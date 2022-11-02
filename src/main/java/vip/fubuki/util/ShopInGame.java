package vip.fubuki.util;

import cn.chuanwise.xiaoming.minecraft.xiaoming.XMMCXiaoMingPlugin;
import cn.chuanwise.xiaoming.minecraft.xiaoming.net.OnlineClient;
import cn.chuanwise.xiaoming.minecraft.xiaoming.net.Server;
import cn.chuanwise.xiaoming.minecraft.xiaoming.net.XMMCServerClient;
import lombok.SneakyThrows;
import vip.fubuki.CheckInPlugin;
import vip.fubuki.GameShopPlugin;
import vip.fubuki.listeners.ExtendProtocolListener;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class ShopInGame {
    public String AutoDeliver(String player, int id, int amount) throws InterruptedException, TimeoutException {
        long userQQ =
                (long) XMMCXiaoMingPlugin.getInstance().getPlayerConfiguration()
                        .getPlayerInfo(player).get().getAccountCodes().toArray()[0];
            if (CheckInPlugin.getInstance().getPointData().getPoints(userQQ) == null) {
               return  "你买个锤子，你有钱吗？";
            }
            else {
                if (!CheckIllegal(id, amount,userQQ)) {
                    if(GameShopPlugin.getInstance().getGameShop().getGood(id).getSellerName().equals("admin")) {
                        String command = CommandGenerate(player, id, amount);
                        Server server = XMMCXiaoMingPlugin.getInstance().getServer();
                        String servername = GameShopPlugin.getInstance().getGameShop().getEnabledServer();
                        OnlineClient onlineClient = server.getOnlineClient(servername).get();
                        onlineClient.getRemoteContact().getConsole().execute(command);
                    }else {
                        HashMap<Object, Object> map=new HashMap<>();
                        map.put("RequestType","buy");map.put("Player",player);
                        map.put("amount",amount);map.put("ItemNBT", GameShopPlugin.getInstance().getItemNBT().getItemNBT().get(id));
                        ExtendProtocolListener.SendMap=map.toString();
                    }
                    return PointOperate(userQQ, id, amount);
                }else return "购买失败，请检查请求的数量是否合法，积分是否足够";
            }
        }
    private String CommandGenerate(String player,Integer id,Integer amount){
            String command="give ";
            command = command+player;
        GameItem goods =GameShopPlugin.getInstance().getGameShop().getGoods().get(id);
        command=command+" "+ LocalJsonUtil.regex(goods.getItem_name()) +" "+amount;
        return command;
    }
    private boolean CheckIllegal(int id,int amount,long UserQQ){
        GameItem gameItem=GameShopPlugin.getInstance().getGameShop().getGood(id);
        int cost = gameItem.getPrice()*amount;
        int UserPoint=CheckInPlugin.getInstance().getPointData().getPoints(UserQQ);
        if(gameItem.getID()==null){
            return true;
        }
        else if(UserPoint<cost){
            return true;
        }
        else if(gameItem.isUnderCarriaged()){
            return true;
        }
        else {
            return gameItem.getAmount() < amount;
        }
    }
    @SneakyThrows
    private String PointOperate(long code, int id, int amount) {
            GameItem gameItem = GameShopPlugin.getInstance().getGameShop().getGoods().get(id);
            int cost= gameItem.getPrice() * amount;
            int left=CheckInPlugin.getInstance().getPointData().getPoints(code) -cost;
            CheckInPlugin.getInstance().getPointData().setPoints(code,left);
            int point=CheckInPlugin.getInstance().getPointData().getPoints(gameItem.getSeller());
            gameItem.setAmount(gameItem.getAmount()-amount);
            if(gameItem.getAmount()==0){
                if(!gameItem.getSellerName().equals("admin")){
                    gameItem.setUnderCarriaged(true);
                }
            }
            GameShopPlugin.getInstance().getGameShop().setGoods(id,gameItem);
            CheckInPlugin.getInstance().getPointData().setPoints(gameItem.getSeller(),point+cost);
            GameShopPlugin.getInstance().getXiaoMingBot().getContactManager().getPrivateContactPossibly(gameItem.getSeller()).get(0).sendMessage(
                "你上架的"+gameItem.getItem_name()+"*"+amount+"已被人购买，获得:"+cost+"积分，当前积分:"+(point+cost));
            return  ("成功购买"+gameItem.getItem_name()+"*"+amount+"，花费"+cost+"积分，剩余"+CheckInPlugin.getInstance().getPointData().getPoints(code));

    }
}


