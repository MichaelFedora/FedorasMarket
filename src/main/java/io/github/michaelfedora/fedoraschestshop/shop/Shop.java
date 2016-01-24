package io.github.michaelfedora.fedoraschestshop.shop;

import io.github.michaelfedora.fedoraschestshop.data.shop.EconShopData;
import io.github.michaelfedora.fedoraschestshop.data.shop.TradeShopData;
import io.github.michaelfedora.fedoraschestshop.shop.data.ShopData;
import io.github.michaelfedora.fedoraschestshop.shop.transaction.ShopTransaction;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by MichaelFedora on 1/23/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class Shop {

    //TODO: LATER: Add Admin Shop
    /*public enum Stance {
        CHEST,
        ADMIN
    }*/

    public enum Type {
        ECON,
        TRADE
    }


    Type type; // econ vs trade?
    ShopTransaction.Op op[] = new ShopTransaction.Op[2]; // used for shop
    int amt[] = new int[2]; // used for both ratio [trade] & amount [econ].
    int price[] = new int[2]; // used for shop
    String itemName[] = new String[2]; // used for trade (both) and shop (one)

    //TODO: Implement
    String owner; // uuid for economy
    Location<World> loc; // location for reference


    public Optional<ShopData> getShopData() {

        switch(type) {

            case ECON:
                return Optional.of(ShopData.makeEcon("", itemName[0], op, amt, price));

            case TRADE:
                return Optional.of(ShopData.makeTrade("", itemName, amt));

            default:
                return Optional.of(new ShopData("", type, ));
        }

        return Optional.empty();
    }

    public Type getType() {
        return type;
    }

    private static void tryMakeShop(Optional<Shop> opt_shop, Sign sign) {
        Shop shop = new Shop();

        List<String> lines = new ArrayList<String>();

        for(Text t : sign.getSignData().lines())
            lines.add(t.toPlain());

        {
            String s = lines.get(0);

            if(s.equals("[FCP][Shop]"))
                shop.type = Type.ECON;
            else if(s.equals("[FCS][Trade]"))
                shop.type = Type.TRADE;
            else
                return;
        }

        shop.itemName[0] = lines.get(1);

        switch(shop.type) {
            case ECON:
                //il: iterator line ; ia: iterator array
                for(int il = 2, ia = 0; ia < 2; il++, ia++) {
                    String s[] = lines.get(il).split(":");

                    if(s.length < 3)
                        return;

                    switch(s[ia].charAt(0)) {
                        case 'B':
                            shop.op[ia] = ShopTransaction.Op.BUY;
                            break;
                        case 'S':
                            shop.op[ia] = ShopTransaction.Op.SELL;
                    }

                    shop.amt[ia] = Integer.getInteger(s[1]);
                    shop.price[ia] = Integer.getInteger(s[2]);
                }
                break;

            case TRADE:

                String s[] = lines.get(2).split(":");
                if(s.length < 2)
                    return;

                shop.amt[0] = Integer.getInteger(s[0]);
                shop.amt[1] = Integer.getInteger(s[1]);

                shop.itemName[1] = lines.get(3);

                break;

            default:
                return;
        }

        opt_shop.of(shop);
    }

    public static Optional<Shop> make(Sign sign) {
        Optional<Shop> ret = Optional.of(new Shop());

        tryMakeShop(ret, sign);

        return ret;
    }

}
