package io.github.michaelfedora.fedorasmarket.shop.modifier;

import io.github.michaelfedora.fedorasmarket.data.ShopType;
import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.shop.ShopModifier;
import io.github.michaelfedora.fedorasmarket.transaction.TransactionActiveParty;

import java.util.ArrayList;
import java.util.List;

/**
 * For doing a transaction multiple times.
 */
public final class MultiAction extends ShopModifier {

    public final int number;

    public MultiAction(int number) {
        super(true, new ArrayList<>());
        this.number = number;
    }

    @Override
    public void execute(Shop shop, TransactionActiveParty owner, TransactionActiveParty customer) {
        for(int i = 0; i < number && shop.tradeTransaction.apply(owner, customer); i++);
    }
}
