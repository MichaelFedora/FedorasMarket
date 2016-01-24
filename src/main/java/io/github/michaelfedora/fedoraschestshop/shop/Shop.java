package io.github.michaelfedora.fedoraschestshop.shop;

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

    public enum Type {
        ECON,
        TRADE
    };

    public enum Op {
        BUY,
        SELL
    }

    public class EconTransactionData {
        public Op op;
        public int amt;
        public int price;

        public EconTransactionData(Op op, int amt, int price) {
            this.op = op;
            this.amt = amt;
            this.price = price;
        }
    }

    public class EconData {
        public String itemName;
        public EconTransactionData tData[] = new EconTransactionData[2];

        public EconData(String itemName, EconTransactionData td[]) {
            this.itemName = itemName;
            this.tData[0] = td[0];
            this.tData[1] = td[1];
        }

        public EconData(String itemName, Op op[], int amt[], int price[]) {
            this.itemName = itemName;
            this.tData[0] = new EconTransactionData(op[0], amt[0], price[0]);
            this.tData[1] = new EconTransactionData(op[1], amt[1], price[1]);
        }
    }

    public class TradeData {
        public String itemName[] = new String[2];
        public int amt[] = new int[2];

        public TradeData(String itemName[], int amt[]) {
            this.itemName[0] = itemName[0];
            this.itemName[1] = itemName[1];
            this.amt[0] = amt[0];
            this.amt[1] = amt[1];
        }
    }


    Type type; // econ vs trade?
    Op op[] = new Op[2]; // used for shop
    int amt[] = new int[2]; // used for both ratio [trade] & amount [econ].
    int price[] = new int[2]; // used for shop
    String itemName[] = new String[2]; // used for trade (both) and shop (one)

    //TODO: Implement
    String owner; // uuid for economy
    Location<World> loc; // location for reference


    public Optional<EconData> getShopData() {
        if(type == Type.ECON)
            return Optional.of(new EconData(itemName[0], op, amt, price));

        return Optional.empty();
    }

    public Optional<TradeData> getTradeData() {
        if(type == Type.TRADE)
            return Optional.of(new TradeData(itemName, amt));

        return Optional.empty();
    }

    public Type getType() {
        return type;
    }

    private static void tryMakeShop(Optional<Shop> opt_shop, Sign sign) {
        Shop shop = new Shop();

        List<String> lines = new ArrayList<>();

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
                            shop.op[ia] = Op.BUY;
                            break;
                        case 'S':
                            shop.op[ia] = Op.SELL;
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
