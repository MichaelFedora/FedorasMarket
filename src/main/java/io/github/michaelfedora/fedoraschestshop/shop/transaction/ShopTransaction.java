package io.github.michaelfedora.fedoraschestshop.shop.transaction;

/**
 * Created by Michael on 1/24/2016.
 */
public class ShopTransaction {

    public enum Op {
        BUY, // Limited Trade (Owner has item and amount, Customer has price)
        SELL, // Switched-Buy (Customer becomes Owner, has item and amount, and Owner becomes Customer, has price)
        TRADE, // Full Trade (Owner and Customer do full trade (money and items)
    }

    public static class Party {

        public final String itemName;
        public final int amtItem;
        public final int amtMoney;

        public Party(String itemName, int amtItem, int amtMoney) {
            this.itemName = itemName;
            this.amtItem = amtItem;
            this.amtMoney = amtMoney;
        }
    }

    public final Op op;
    public final Party owner;
    public final Party customer;

    public ShopTransaction(Op op, Party owner, Party customer) {
        this.op = op;
        this.owner = owner;
        this.customer = customer;
    }

}
