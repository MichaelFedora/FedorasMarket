package io.github.michaelfedora.fedorasmarket.trade;

import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.FmSerializedData;
import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;

import java.util.Optional;

/**
 * Created by Michael on 3/3/2016.
 */
public class SerializedTradeForm implements FmSerializedData<TradeForm> {
    public TradeType tradeType;
    public SerializedTradeParty ownerPartyData;
    public SerializedTradeParty customerPartyData;

    public TradeForm deserialize() throws BadDataException {
        return TradeForm.fromSerializedData(this);
    }

    public Optional<TradeForm> safeDeserialize() {
        return Optional.of(TradeForm.fromSerializedData(this));
    }

    public String toString() {
        return "tradeType: " + this.tradeType + ", ownerPartyData: {" + this.ownerPartyData + "}, customerPartyData: {" + this.customerPartyData + "}";
    }
}
