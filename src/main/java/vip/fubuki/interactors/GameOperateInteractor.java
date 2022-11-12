package vip.fubuki.interactors;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.minecraft.protocol.OnlinePlayerResponse;
import cn.chuanwise.xiaoming.minecraft.xiaoming.XMMCXiaoMingPlugin;
import cn.chuanwise.xiaoming.minecraft.xiaoming.configuration.PlayerInfo;
import cn.chuanwise.xiaoming.minecraft.xiaoming.net.OnlineClient;
import cn.chuanwise.xiaoming.minecraft.xiaoming.net.Server;
import cn.chuanwise.xiaoming.user.GroupXiaoMingUser;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import lombok.SneakyThrows;
import vip.fubuki.CheckInPlugin;
import vip.fubuki.GameShopPlugin;
import vip.fubuki.util.GameItem;
import vip.fubuki.util.LocalJsonUtil;
import vip.fubuki.util.Words;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class GameOperateInteractor extends SimpleInteractors<GameShopPlugin> {
      @Filter(Words.BUY+"(物品|道具|item) {id}")
      @Filter(Words.BUY+"(物品|道具|item) {id} {amount}")
    public void AutoDeliver(XiaoMingUser user, @FilterParameter("id") int id, @FilterParameter(value = "amount",defaultValue = "1") int amount, GroupXiaoMingUser groupXiaoMingUser) throws InterruptedException, TimeoutException {
          long groupCode = groupXiaoMingUser.getGroupCode();
          Boolean enabled = CheckInPlugin.getInstance().getConfiguration().CheckEnabled(groupCode);
          if (enabled == null) {
              enabled = false;
          }
          if (enabled) {
              if (CheckInPlugin.getInstance().getPointData().getPoints(user.getCode()) == null) {
                  user.sendMessage("你买个锤子,你有钱吗？");
              }
              else {
                  if (!CheckIllegal(id, amount,user)) {
                      String command = CommandGenerate(user, id, amount);
                      if (!command.equals("Failed")) {
                          Server server = XMMCXiaoMingPlugin.getInstance().getServer();
                          if (!server.isBound()) {
                              user.sendError("服务器尚未启动！");
                          } else {
                              List<OnlineClient> onlineClients = server.getOnlineClients();
                              if (onlineClients.isEmpty()) {
                                  user.sendError("目前没有任何服务器连接到商店,无法购买商品");
                              } else {
                                  String servername=GameShopPlugin.getInstance().getGameShop().getEnabledServer();
                                  if(!server.getOnlineClient(servername).isPresent()){
                                      user.sendMessage("游戏商店未连接配置的服务器。");
                                      return;
                                  }
                                  if (onlineClients.contains(server.getOnlineClient(servername).get())){
                                      OnlineClient onlineClient = server.getOnlineClient(servername).get();
                                      onlineClient.getRemoteContact().getConsole().execute(command);
                                      PointOperate(user, id, amount,onlineClient);

                                  }
                                  else{
                                      user.sendMessage("购买失败,请联系管理配置商店对应的服务器。");
                                  }
                              }
                          }
                      }
                  }
              }
          }
      }
    private String executeResponseDetail(OnlineClient onlineClient,XiaoMingUser user)throws InterruptedException, TimeoutException {
        Set<OnlinePlayerResponse.PlayerKey> onlinePlayerKeys = onlineClient.getServerClient().getOnlinePlayerKeys();
        if (!onlinePlayerKeys.isEmpty()) {
            List<String> playerNames = onlinePlayerKeys.stream().map(OnlinePlayerResponse.PlayerKey::getPlayerName).collect(Collectors.toList());
            Optional<PlayerInfo> optionalPlayerInfo = XMMCXiaoMingPlugin.getInstance().getPlayerConfiguration().getPlayerInfo(user.getCode());
            for (String playerName : playerNames) {
                if (optionalPlayerInfo.get().getPlayerNames().get(0).equals(playerName)) return "Success";
            }
        }
        return "Player Offline Failed";
    }
    private String CommandGenerate(XiaoMingUser user,Integer id,Integer amount){
        String command="minecraft:give ";
        Optional<PlayerInfo> optionalPlayerInfo = XMMCXiaoMingPlugin.getInstance().getPlayerConfiguration().getPlayerInfo(user.getCode());
        if (!optionalPlayerInfo.isPresent()) {
            user.sendError("购买失败,你还没有绑定玩家名。");
            return "Failed";
        } else {
            PlayerInfo playerInfo = optionalPlayerInfo.get();
            command = command+playerInfo.getPlayerNames().get(0);
        }
        GameItem goods =GameShopPlugin.getInstance().getGameShop().getGoods().get(id);
        command=command+" "+ LocalJsonUtil.regex(goods.getItem_name()) +" "+amount;
        return command;
    }
    private boolean CheckIllegal(int id,int amount,XiaoMingUser user){
          GameItem gameItem=GameShopPlugin.getInstance().getGameShop().getGood(id);
          int cost = gameItem.getPrice()*amount;
          int UserPoint=CheckInPlugin.getInstance().getPointData().getPoints(user.getCode());
          if(gameItem.getID()==null){
              user.sendMessage("购买请求非法,请检查商品ID和数量以及当前持有点数。");
              return true;
          }
          else if(UserPoint<cost){
              user.sendMessage("积分不足，购买失败,当前积分:"+ UserPoint + ",所需积分:\n" + gameItem.getPrice());
              return true;
          }
          else if(gameItem.isUnderCarriaged()){
              user.sendMessage("购买失败,商品已下架。");
              return true;
          }
          else {
              if (gameItem.getAmount() < amount){
                  user.sendMessage("购买请求非法,请检查商品ID和数量以及当前持有点数。");
                  return true;
              }
              else {
                  return false;
              }
          }
    }
    @SneakyThrows
    private void PointOperate(XiaoMingUser user, int id, int amount, OnlineClient onlineClient) {
        if (executeResponseDetail(onlineClient,user).equals("Success")) {
            GameItem gameItem = GameShopPlugin.getInstance().getGameShop().getGoods().get(id);
            long code = user.getCode();
            int cost= gameItem.getPrice() * amount;
            int left=CheckInPlugin.getInstance().getPointData().getPoints(code) -cost;
            CheckInPlugin.getInstance().getPointData().setPoints(code,left);
            int point=CheckInPlugin.getInstance().getPointData().getPoints(gameItem.getSeller());
            gameItem.setAmount(gameItem.getAmount()-amount);
            if(gameItem.getAmount()==0&&!gameItem.getSellerName().equals("admin")) {
                gameItem.setUnderCarriaged(true);
                        gameItem.setUnderCarriaged(true);
                        Map map=new HashMap<>();
                        map=GameShopPlugin.getInstance().getItemNBT().getItemNBT();
                        map.remove(id);
                        GameShopPlugin.getInstance().getItemNBT().setItemNBT(map);
            }
            GameShopPlugin.getInstance().getGameShop().setGoods(id,gameItem);
            CheckInPlugin.getInstance().getPointData().setPoints(gameItem.getSeller(),point+cost);
            user.sendMessage("成功购买"+gameItem.getItem_name()+"*"+amount+",花费"+cost+"积分,剩余:"+left);
            getXiaoMingBot().getContactManager().getPrivateContactPossibly(gameItem.getSeller()).get(0).sendMessage(
                    "你上架的"+gameItem.getItem_name()+"*"+amount+"已被人购买,获得:"+cost+"积分,当前积分:"+(point+cost));
        }else user.sendMessage("购买失败,错误信息如下:"+executeResponseDetail(onlineClient,user));
    }
}
