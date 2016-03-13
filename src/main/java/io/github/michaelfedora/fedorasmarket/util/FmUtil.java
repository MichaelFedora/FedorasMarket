package io.github.michaelfedora.fedorasmarket.util;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmUtil {

    public static Currency getDefaultCurrency() {
        return FedorasMarket.getEconomyService().getDefaultCurrency();
    }

    public static Text makeMessageError(String s) {
        return Text.of(TextStyles.BOLD, TextColors.DARK_RED, "[" + PluginInfo.NAME + "][ERROR]: ", TextColors.RED, TextStyles.RESET, s);
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

    public static Text makeInfoPrefix() {
        return Text.of(TextStyles.BOLD, TextColors.DARK_GRAY, "[" + PluginInfo.NAME + "]", TextStyles.RESET, "[INFO]: ");
    }

    public static Text makeWarnPrefix() {
        return Text.of(TextStyles.BOLD, TextColors.GOLD, "[" + PluginInfo.NAME + "]", TextStyles.RESET, "[WARN]: ");
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

    public static Text makeMessageInfo(String s) {
        return Text.of(makeInfoPrefix(), TextColors.GRAY, s);
    }

    public static Text makeMessageWarn(String s) {
        return Text.of(makeWarnPrefix(), TextColors.YELLOW, TextStyles.RESET, s);
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
}
