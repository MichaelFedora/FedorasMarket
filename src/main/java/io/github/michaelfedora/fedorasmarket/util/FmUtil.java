package io.github.michaelfedora.fedorasmarket.util;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmUtil {

    public static Text makeErrorPrefix(String cause) {
        return Text.of(TextStyles.BOLD, TextColors.DARK_RED, "[" + PluginInfo.NAME + "]", TextStyles.RESET,
                TextColors.DARK_GRAY, "[" + cause + "]", TextColors.GRAY, ": ");
    }

    public static Text makePrefix() {
        return Text.of(TextStyles.BOLD, TextColors.GREEN, "[" + PluginInfo.NAME + "]: ");
    }

    public static Text makeCausePrefix(String cause) {
        return Text.of(TextStyles.BOLD, TextColors.GREEN, "[" + PluginInfo.NAME + "]", TextStyles.RESET,
                TextColors.BLUE, "[" + cause + "]", TextColors.GRAY, ": ");
    }

    public static Text makeInfoPrefix(String cause) {
        return Text.of(TextStyles.BOLD, TextColors.GRAY, "[" + PluginInfo.NAME + "]", TextStyles.RESET, TextColors.DARK_GRAY, "[" + cause + "]", TextColors.GRAY, ": ");
    }

    public static Text makeWarnPrefix(String cause) {
        return Text.of(TextStyles.BOLD, TextColors.YELLOW, "[" + PluginInfo.NAME + "]", TextStyles.RESET, TextColors.GOLD, "[" + cause + "]", TextColors.GRAY, ": ");
    }

    public static Text makeMessageError(String cause, String message) {
        return Text.of(makeErrorPrefix(cause), TextColors.RED, message);
    }

    public static Text makeMessage(String s) {
        return Text.of(makePrefix(), TextColors.WHITE, TextStyles.RESET, s);
    }

    public static Text makeMessage(String cause, String message) {
        return Text.of(makeCausePrefix(cause), TextColors.WHITE, message);
    }

    public static Text makeMessageInfo(String cause, String message) {
        return Text.of(makeInfoPrefix(cause), TextColors.GRAY, message);
    }

    public static Text makeMessageWarn(String cause, String s) {
        return Text.of(makeWarnPrefix(cause), TextColors.YELLOW, TextStyles.RESET, s);
    }

    public static Optional<Sign> getShopSignFromLocation(Location<World> loc) {
        if(loc.getBlock().getType() != BlockTypes.WALL_SIGN)
            return Optional.empty();

        return loc.getTileEntity().map((te) -> (Sign) te);
    }

    public static Optional<Sign> getShopSignFromBlockSnapshot(BlockSnapshot bsnap) {

        if(bsnap.getState().getType() != BlockTypes.WALL_SIGN)
            return Optional.empty();

        Optional<Location<World>> opt_loc = bsnap.getLocation();
        if (opt_loc.isPresent())
            return opt_loc.get().getTileEntity().map((te) -> (Sign) te);

        return Optional.empty();
    }

    public static Map<String, Currency> getCurrenciesByName() {

        Map<String, Currency> currencies = new TreeMap<>();
        for(Currency c : FedorasMarket.getEconomyService().getCurrencies()) {
            currencies.put(c.getName(), c);
        }

        return currencies;
    }

    public static Currency getDefaultCurrency() {
        return FedorasMarket.getEconomyService().getDefaultCurrency();
    }

    /**
     * Attempts to get the currency specified by given name.
     * *IT DOES NOT MATCH CASE*.
     * @param name the name to match
     * @return an optional that either contains the match, or is empty
     */
    public static Optional<Currency> getCurrency(String name) {

        if(name.equalsIgnoreCase("default") || name.equalsIgnoreCase("def"))
            return Optional.of(getDefaultCurrency());

        for(Currency c : FedorasMarket.getEconomyService().getCurrencies())
            if(c.getName().equalsIgnoreCase(name))
                return Optional.of(c);

        return Optional.empty();
    }

    public static void giveItem(ItemStack itemStack, Player player) {
        InventoryTransactionResult itr = player.getInventory().offer(itemStack);
        switch(itr.getType()) {
            case FAILURE:
            case CANCELLED:
                Location<World> location = player.getLocation();
                World world = location.getExtent();
                Optional<Entity> opt_entity = world.createEntity(EntityTypes.ITEM, location.getPosition());
                if(opt_entity.isPresent()) {
                    Entity entity = opt_entity.get();
                    entity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
                    world.spawnEntity(entity, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build())));
                } // else we really done goofed
                break;
        }
    }
}
