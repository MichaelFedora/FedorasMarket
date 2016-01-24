package io.github.michaelfedora.fedoraschestshop.shop.data;

import io.github.michaelfedora.fedoraschestshop.shop.transaction.ShopTransaction;
import io.github.michaelfedora.fedoraschestshop.shop.Shop;

/**
 * Created by Michael on 1/24/2016.
 */
public class ShopData {
    public final String owner;
    public final Shop.Type type;

    public final ShopTransaction tData[];

    public ShopData(String owner, Shop.Type type, ShopTransaction td[]) {
        this.owner = owner;
        this.type = type;
        this.tData = td;
    }

    public static ShopData makeEcon(String owner, String itemName, ShopTransaction.Op op[], int amt[], int price[]) {

        ShopTransaction td[] = new ShopTransaction[2];
        td[0] = new ShopTransaction(op[0], new ShopTransaction.Party(itemName, amt[0], 0), new ShopTransaction.Party("", 0, price[0]));
        td[1] = new ShopTransaction(op[1], new ShopTransaction.Party(itemName, amt[1], 0), new ShopTransaction.Party("", 0, price[1]));

        return new ShopData(owner, Shop.Type.ECON, td);
    }

    public static ShopData makeTrade(String owner, String itemName[], int amt[]) {
        return new ShopData(owner,Shop.Type.TRADE, new ShopTransaction[] {
                new ShopTransaction(
                    ShopTransaction.Op.TRADE,
                    new ShopTransaction.Party(itemName[0], amt[0], 0),
                    new ShopTransaction.Party(itemName[1], amt[1], 0)
                )
            }
        );
    }
}
