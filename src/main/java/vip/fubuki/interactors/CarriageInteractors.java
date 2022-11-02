package vip.fubuki.interactors;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import vip.fubuki.CheckInPlugin;
import vip.fubuki.GameShopPlugin;
import vip.fubuki.util.GameItem;

@SuppressWarnings("ALL")
public class CarriageInteractors extends SimpleInteractors {
    @Required("checkin.admin.carriage")
    @Filter("上架(物品|道具|item) {Item_Name} {Price} {Amount}")
    public void AdminCarriage(XiaoMingUser user, @FilterParameter("Item_Name") String name, @FilterParameter("Price") int Price, @FilterParameter(value = "Amount",defaultValue = "-1") int Amount){
        int Index=GameShopPlugin.getInstance().getGameShop().getGoods().size();
        GameItem NewGood = new GameItem();
        NewGood.setID(Index);
        NewGood.setItem_name(name);
        NewGood.setPrice(Price);
        NewGood.setSellerName("admin");
        NewGood.setSeller(CheckInPlugin.getInstance().getConfiguration().getShopOwner());
        if (Amount < 0) {
            Amount = -1;
        }

        NewGood.setAmount(Amount);
        GameShopPlugin.getInstance().getGameShop().setGoods(Index,NewGood);
        user.sendMessage("成功上架一样名称为:" + name + ",价格为:" + Price + ",存量为:" + Amount + "的货品，ID:" + Index);
    }

    @Required("checkin.admin.outcarriage")
    @Filter("下架(物品|道具|item) {ID}")
    public void UnderCarriaged(XiaoMingUser user, @FilterParameter("ID") int id) {
        if (id > 0 && id <= GameShopPlugin.getInstance().getGameShop().getGoods().size()) {
            if (!GameShopPlugin.getInstance().getGameShop().getGoods().get(id).isUnderCarriaged()) {
                user.sendMessage("这件商品原本就是下架的。");
            } else {
                GameItem goods= GameShopPlugin.getInstance().getGameShop().getGoods().get(id);
                goods.setUnderCarriaged(true);
                GameShopPlugin.getInstance().getGameShop().setGoods(id,goods);
                user.sendMessage("成功下架ID" + id + "的商品。");
            }
        } else {
            user.sendMessage("操作失败,没有此ID的商品。");
        }

    }
    @Required("checkin.admin.replenish")
    @Filter("(补货|补充|replenish)(物品|道具|item) {ID} {Amount}")
    public void Replenishment(XiaoMingUser user, @FilterParameter("ID") int id, @FilterParameter("Amount") int Amount) {
        if (id > 0 && id <= GameShopPlugin.getInstance().getGameShop().getGoods().size()) {
            GameItem goods = GameShopPlugin.getInstance().getGameShop().getGoods().get(id);
            int PreAmount = goods.getAmount();
            PreAmount += Amount;
            if (PreAmount < 0) {
                PreAmount = -1;
            }

            goods.setAmount(PreAmount);
            GameShopPlugin.getInstance().getGameShop().setGoods(id, goods);
            user.sendMessage("补货成功,当前余量:" + PreAmount + "\n提示:当余量为-1时将被认为是不限量的。");
        } else {
            user.sendMessage("操作失败,没有此ID的商品。");
        }
    }
    @Required("checkin.admin.change")
    @Filter("改(物品|道具|item)价格 {ID} {Price}")
    public void ChangePrice(XiaoMingUser user, @FilterParameter("ID") int ID, @FilterParameter("Price") int Price) {
        GameItem good = GameShopPlugin.getInstance().getGameShop().getGoods().get(ID);
        good.setPrice(Price);
        GameShopPlugin.getInstance().getGameShop().setGoods(ID, good);
        user.sendMessage("成功将商品ID:" + ID + "的物品价格更改为:" + Price);
    }
}
