package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.database.FmSerializable;
import io.github.michaelfedora.fedorasmarket.database.FmSerializedData;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;

/**
 * Created by Michael on 2/27/2016.
 */
public class ShopForm implements FmSerializable<ShopForm.Data> {

    public TradeForm tradeForm;
    public String modifier;

    public static class Data implements FmSerializedData<ShopForm> {
        public TradeForm.Data tradeFormData;
        public String modifier;

        public Data(TradeForm.Data tradeFormData, String modifier) {
            this.tradeFormData = tradeFormData;
            this.modifier = modifier;
        }

        @Override
        public ShopForm deserialize() {
            return ShopForm.fromData(this);
        }
    }

    @Override
    public Data toData() {
        return new Data(tradeForm.toData(), modifier);
    }

    public static ShopForm fromData(Data data) {
        return new ShopForm(data.tradeFormData.deserialize(), data.modifier);
    }

    public ShopForm(TradeForm tradeForm, String modifier) {
        this.tradeForm = tradeForm;
        this.modifier = modifier;
    }
}
