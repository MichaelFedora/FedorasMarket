package io.github.michaelfedora.fedorasmarket.trade;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Michael on 2/24/2016.
 */
public enum PartyType {
    OWNER,
    CUSTOMER;

    public static final Map<String,PartyType> choices;
    static {
        Map<String,PartyType> c = new TreeMap<>();
        c.put("owner", OWNER);
        c.put("customer", CUSTOMER);
        choices = c;
    }
}
