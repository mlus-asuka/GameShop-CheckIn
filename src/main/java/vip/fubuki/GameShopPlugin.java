package vip.fubuki;

import cn.chuanwise.xiaoming.plugin.JavaPlugin;
import lombok.SneakyThrows;
import vip.fubuki.configuration.GameItemNBT;
import vip.fubuki.configuration.GameShop;
import vip.fubuki.configuration.ItemList;
import vip.fubuki.interactors.*;
import vip.fubuki.listeners.ExtendProtocolListener;
import vip.fubuki.util.GameItemInitialization;

import java.io.File;


public class GameShopPlugin extends JavaPlugin {
    private static final GameShopPlugin INSTANCE=new GameShopPlugin();
    protected GameShop gameShop;
    protected ItemList itemList;
    protected GameItemNBT itemNBT;
    public GameShopPlugin() {
    }
    public static GameShopPlugin getInstance() {
        return INSTANCE;
    }

    public GameShop getGameShop() {return this.gameShop;}

    public GameItemNBT getItemNBT() {return this.itemNBT;}

    public ItemList getItemList(){return this.itemList;}

    public void onEnable() {
        getXiaoMingBot().getInteractorManager().registerInteractors(new GameShopInteractor(),this);
        getXiaoMingBot().getInteractorManager().registerInteractors(new GameOperateInteractor(),this);
        getXiaoMingBot().getInteractorManager().registerInteractors(new CarriageInteractors(),this);
        getXiaoMingBot().getInteractorManager().registerInteractors(new EnableGameShopInteractors(),this);
        getXiaoMingBot().getInteractorManager().registerInteractors(new ReloadInteractors(),this);
        getXiaoMingBot().getEventManager().registerListeners(ExtendProtocolListener.getInstance(), this);
    }

    public void onLoad(){
        reload();
    }
    public void reload(){
        File dataFolder = this.getDataFolder();
        dataFolder.mkdirs();
        gameShop=setupConfiguration(GameShop.class,"GameShop.json",GameShop::new);
        itemNBT=setupConfiguration(GameItemNBT.class,"ItemNBT.json",GameItemNBT::new);
        RegisterItem();
    }

    @SneakyThrows
    protected void RegisterItem() {
        File customizerDirectory = new File(this.getDataFolder(), "items");
        if (customizerDirectory.mkdirs()) {
            itemList = setupConfiguration(ItemList.class,new File(customizerDirectory,"ItemName.json"), ItemList::new);
            GameItemInitialization itemInitialization = new GameItemInitialization();
            itemInitialization.Initialization();
        } else {
            itemList = setupConfiguration(ItemList.class, new File(customizerDirectory, "ItemName.json"), ItemList::new);
        }
    }
}
