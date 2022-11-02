package vip.fubuki.interactors;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.GroupXiaoMingUser;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import vip.fubuki.CheckInPlugin;
import vip.fubuki.GameShopPlugin;
import vip.fubuki.util.GameItem;
import vip.fubuki.util.LocalJsonUtil;

import java.util.ArrayList;
import java.util.List;

public class GameShopInteractor extends SimpleInteractors<GameShopPlugin> {

    private int LastPage;
    @Filter("(游戏商店|游戏商城|game shop)")
    @Filter("(游戏商店|游戏商城|game shop) {page}")
    public void GameShop(XiaoMingUser user,@FilterParameter(value = "page",defaultValue = "1") int page,GroupXiaoMingUser groupXiaoMingUser){
        long groupCode = groupXiaoMingUser.getGroupCode();
        Boolean enabled = CheckInPlugin.getInstance().getConfiguration().CheckEnabled(groupCode);
        if (enabled == null) {
            enabled = false;
        }
        if (enabled) {
            LastPage=page;
            int Scanned=0;
            final List<String> ItemList=new ArrayList<>();
            int i=0;
            do{
                GameItem goods =GameShopPlugin.getInstance().getGameShop().getGoods().get(i);
                if(goods==null) break;
                String name = goods.getItem_name();
                Integer price = goods.getPrice();
                Integer amount = goods.getAmount();
                boolean UnderCarriaged = goods.isUnderCarriaged();
                String SellerName=goods.getSellerName();
                if (!UnderCarriaged) {
                    String ItemName= GameShopPlugin.getInstance().getItemList().getItemName(LocalJsonUtil.regex(name));
                    if (amount == -1) {
                        if(ItemName!=null){
                            ItemList.add(Scanned, "商品ID:" + i + ",商品名称:" + ItemName + ",单价:" + price + "积分,余量:无限,上架者:"+SellerName+"\n");
                        }
                        else ItemList.add(Scanned, "商品ID:" + i + ",商品名称:" + name + ",单价:" + price + "积分,余量:无限,上架者:"+SellerName+"\n");
                    } else {
                        if(ItemName!=null){
                            ItemList.add(Scanned, "商品ID:" + i + ",商品名称:" + ItemName + ",单价:" + price + "积分,余量:" + amount +",上架者:"+SellerName+ "\n");
                        }
                        else ItemList.add(Scanned, "商品ID:" + i + ",商品名称:" + name + ",单价:" + price + "积分,余量:" + amount +",上架者:"+SellerName+ "\n");
                    }
                    Scanned = Scanned + 1;
                }
                i++;
            }while (GameShopPlugin.getInstance().getGameShop().getGoods().get(i)!=null);

            int MaxPage = (int)(Math.floor(i / 8) + 1.0);
            String text = "商店页面 当前页:" + page + "/" + MaxPage + "\n";

            if ((page - 1) * 8 <= i && page != 0) {
                if(i!=0){
                    for (i = page * 8 - 7; i <= page * 8; i++) {
                        if(ItemList.get(i-1)==null) break;
                        text = text + ItemList.get(i - 1);
                        if (i == Scanned) {
                            break;
                        }
                    }
                }

                if (page == 1 && MaxPage == 1) {
                    text = text + "当前为唯一页 回复退出以结束查询";
                } else if (page == 1) {
                    text = text + "回复  下一页 切换页面 回复退出以结束查询";
                } else {
                    text = text + "回复 上一页 / 下一页 切换页面 回复退出以结束查询";
                }
                user.sendMessage(text);
            }
            else user.sendMessage("页码超出商品列表。");

            Quering(user);
        }
    }
    public void Quering(XiaoMingUser user){
        user.addTag("QueringGameShop");
        Runnable runnable = () -> user.removeTag("QueringGameShop");
        this.getXiaoMingBot().getScheduler().runLater(120000L, runnable);
    }

    @Filter("(上一页|previous)")
    public void PrePage(XiaoMingUser user, GroupXiaoMingUser groupXiaoMingUser) {
        if (user.hasTag("QueringGameShop")) {
            GameShop(user, LastPage - 1, groupXiaoMingUser);
        }

    }

    @Filter("(下一页|翻页|next)")
    public void NextPage(XiaoMingUser user, GroupXiaoMingUser groupXiaoMingUser) {
        if (user.hasTag("QueringGameShop")) {
            GameShop(user, LastPage + 1, groupXiaoMingUser);
        }

    }

    @Filter("(退出|结束|quit)")
    public void QuitQuery(XiaoMingUser user) {
        if (user.hasTag("QueringGameShop")) {
            user.removeTag("QueringGameShop");
        }

    }
}
