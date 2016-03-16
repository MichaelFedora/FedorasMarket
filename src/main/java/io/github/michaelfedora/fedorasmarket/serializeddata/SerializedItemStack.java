package io.github.michaelfedora.fedorasmarket.serializeddata;

import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.FmSerializedData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Michael on 3/16/2016.
 */
public class SerializedItemStack implements FmSerializedData<ItemStack> {

    public String itemName;
    public int itemAmount;

    public SerializedItemStack(String itemName, int itemAmount) {
        this.itemName = itemName;
        this.itemAmount = itemAmount;
    }

    @Override
    public ItemStack deserialize() throws BadDataException {

        ItemType type = Sponge.getRegistry().getType(ItemType.class, itemName).orElseThrow(() -> new BadDataException("Bad item type :c"));

        return ItemStack.of(type, itemAmount);
    }

    @Override
    public Optional<ItemStack> safeDeserialize() {
        try {

            return Optional.of(deserialize());

        } catch(BadDataException e) {

            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return itemAmount + " " + itemName + "(s)";
    }
}
