package io.github.michaelfedora.fedorasmarket.trade;

import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.FmSerializedData;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Michael on 3/3/2016.
 */
public class SerializedTradeParty implements FmSerializedData<TradeParty> {
    public final Map<String,Integer> items = new HashMap<>();
    public final Map<String,BigDecimal> currencies = new HashMap<>();

    @Override
    public Optional<TradeParty> safeDeserialize() {
        return Optional.of(TradeParty.fromSerializedData(this));
    }

    @Override
    public TradeParty deserialize() throws BadDataException {
        return TradeParty.fromSerializedData(this);
    }

    public String toString() {
        return "items: " + this.items + ", currencies: " + this.currencies;
    }
}
