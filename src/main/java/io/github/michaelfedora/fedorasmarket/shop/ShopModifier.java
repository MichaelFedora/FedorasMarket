package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.data.ShopType;
import io.github.michaelfedora.fedorasmarket.transaction.TransactionActiveParty;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 2/23/2016.
 */
public class ShopModifier {

    public final boolean negate;
    public final List<ShopType> shopTypes;

    protected ShopModifier(boolean negate, List<ShopType> shopTypes) {
        this.negate = negate;
        this.shopTypes = shopTypes;
    }

    public static final ShopModifier NONE = new ShopModifier(true, new ArrayList<>());

    public boolean isValidWith(ShopType type) {
        if(this.negate)
            return (!shopTypes.contains(type));
        else
            return shopTypes.contains(type);
    }

    public void execute(Shop shop, TransactionActiveParty owner, TransactionActiveParty customer) { }
}
