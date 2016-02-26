package io.github.michaelfedora.fedorasmarket.util;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

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

    public static Text makeMessage(String s) {
        return Text.of(TextStyles.BOLD, TextColors.GREEN, "[" + PluginInfo.NAME + "]: ", TextColors.WHITE, TextStyles.RESET, s);
    }

    public static Text makeMessageInfo(String s) {
        return Text.of(TextStyles.BOLD, TextColors.DARK_GRAY, "[" + PluginInfo.NAME + "][INFO]: ", TextColors.GRAY, TextStyles.RESET, s);
    }


    public static Text makeMessageWarn(String s) {
        return Text.of(TextStyles.BOLD, TextColors.GOLD, "[" + PluginInfo.NAME + "][WARN]: ", TextColors.YELLOW, TextStyles.RESET, s);
    }
}
