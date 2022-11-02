package vip.fubuki.util;

public class GameItem {
    private Integer ID;
    private String item_name;
    private Integer price;
    private Integer amount;
    private long Seller;

    private String SellerName;
    private boolean UnderCarriaged=false;

    public String getSellerName() {
        return SellerName;
    }

    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public long getSeller() {
        return Seller;
    }

    public void setSeller(long seller) {
        Seller = seller;
    }

    public boolean isUnderCarriaged() {
        return UnderCarriaged;
    }

    public void setUnderCarriaged(boolean whetherUnderCarriaged) {
        UnderCarriaged = whetherUnderCarriaged;
    }
}
