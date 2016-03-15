package io.github.michaelfedora.fedorasmarket.util;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
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

    public static Currency getDefaultCurrency() {
        return FedorasMarket.getEconomyService().getDefaultCurrency();
    }

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

    public static Optional<Sign> getSignFromLocation(Location<World> loc) {
        Optional<TileEntity> opt_te = loc.getTileEntity();
        if(!opt_te.isPresent())
            return Optional.empty();
        if(opt_te.get() instanceof Sign)
            return Optional.of((Sign) opt_te.get());

        return Optional.empty();
    }

    public static Optional<Sign> getSignFromBlockSnapshot(BlockSnapshot bsnap) {

        Optional<Location<World>> opt_loc = bsnap.getLocation();
        if (opt_loc.isPresent())
            return getSignFromLocation(opt_loc.get());

        return Optional.empty();
    }

    public static Map<String, Currency> getCurrenciesByName() {

        Map<String, Currency> currencies = new TreeMap<>();
        for(Currency c : FedorasMarket.getEconomyService().getCurrencies()) {
            currencies.put(c.getName(), c);
        }

        return currencies;
    }
}
