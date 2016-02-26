package io.github.michaelfedora.fedorasmarket.data;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MichaelFedora on 1/25/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class FmDataKeys {
    public static final Key<Value<Shop>> SHOP_DATA;
    static {
        SHOP_DATA = KeyFactory.makeSingleKey(Shop.class, Value.class, DataQuery.of(PluginInfo.DATA_ROOT + ":shop_data"));
    }
}
