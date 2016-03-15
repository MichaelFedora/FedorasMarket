package io.github.michaelfedora.fedorasmarket.trade;

/**
 * Created by Michael on 2/23/2016.
 */
public enum GoodType {
    ALL,
    ITEM,
    CURRENCY;

    public boolean valid() {
        return this != ALL;
    }

    public boolean equalsWeak(GoodType other) {
        return this == ALL || this == other;
    }
}
