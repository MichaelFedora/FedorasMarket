package io.github.michaelfedora.fedoraschestshop.shop;

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
public class ShopKeys {
    public static final Key<Value<Shop>> DATA;
    static {
        DATA = KeyFactory.makeSingleKey(Shop.class, Value.class, DataQuery.of("fedoraschestshop:data"));
    }
}
