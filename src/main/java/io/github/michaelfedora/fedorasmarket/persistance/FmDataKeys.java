package io.github.michaelfedora.fedorasmarket.persistance;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

/**
 * Created by MichaelFedora on 1/25/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class FmDataKeys {
    public static final Key<Value<ShopReference>> SHOP_REFERENCE;
    static {
        SHOP_REFERENCE = KeyFactory.makeSingleKey(ShopReference.class, Value.class, DataQuery.of(PluginInfo.DATA_ROOT + ".shopref"));
    }
}
