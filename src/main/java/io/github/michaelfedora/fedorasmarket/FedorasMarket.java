/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
package io.github.michaelfedora.fedorasmarket;

import com.google.inject.Inject;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.auction.FmAuctionExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.auction.FmAuctionHelpExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.depot.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.modifier.FmModifierExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.modifier.FmModifierHelpExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.quickshop.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.quicktrade.FmQuickTradeExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.quicktrade.FmQuickTradeHelpExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.shop.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.trade.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform.*;
import io.github.michaelfedora.fedorasmarket.config.FmConfig;
import io.github.michaelfedora.fedorasmarket.persistance.shopreference.ImmutableShopReferenceData;
import io.github.michaelfedora.fedorasmarket.persistance.shopreference.ShopReferenceBuilder;
import io.github.michaelfedora.fedorasmarket.persistance.shopreference.ShopReferenceData;
import io.github.michaelfedora.fedorasmarket.persistance.shopreference.ShopReferenceDataManipulatorBuilder;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import me.flibio.updatifier.Updatifier;
import net.minecrell.mcstats.SpongeStatsLite;
import org.slf4j.Logger;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.nio.file.Path;
import java.util.*;

/**
 * The main class yo!
 */
@Updatifier(repoName = "FedorasMarket", repoOwner = "MichaelFedora", version = PluginInfo.VERSION)
@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION, description = PluginInfo.DESCRIPTION, authors = PluginInfo.AUTHORS)
public class FedorasMarket {

    private static FedorasMarket instance;

    public static final String ACCOUNT_VIRTUAL_OWNER_PREFIX = "fedorasmarket:v_o_acc_";
    public static final String ACCOUNT_VIRTUAL_CUSTOMER_PREFIX = "fedorasmarket:v_c_acc_";

    @Inject
    private Logger logger;
    public static Logger getLogger() { return instance.logger; }

    private ConsoleSource console;
    public static ConsoleSource getConsole() { return instance.console; }

    @Inject
    private SpongeStatsLite stats;

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path sharedConfigDir;
    public static Path getSharedConfigDir() { return instance.sharedConfigDir; }

    private EconomyService economyService;
    public static EconomyService getEconomyService() { return instance.economyService; }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if(event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    public static final LinkedHashMap<List<String>, CommandSpec> subCommands = new LinkedHashMap<>();;
    public static final HashMap<String, LinkedHashMap<List<String>, CommandSpec>> grandChildCommands = new HashMap<>();
    public static Optional<LinkedHashMap<List<String>, CommandSpec>> getGrandChildCommands(String name) {
        return Optional.ofNullable(grandChildCommands.get(name));
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent gpie) {
        instance = this;
        //this.stats.start();

        Sponge.getEventManager().registerListeners(this, new PlayerInteractListener());

        Sponge.getDataManager().register(ShopReferenceData.class, ImmutableShopReferenceData.class, new ShopReferenceDataManipulatorBuilder());
        Sponge.getDataManager().registerBuilder(ShopReference.class, new ShopReferenceBuilder());
    }

    @Listener
    public void onInit(GameInitializationEvent gie) {

        this.console = Sponge.getServer().getConsole();

        console.sendMessage(Text.of(TextStyles.BOLD, TextColors.GREEN, "===== ",
                TextStyles.RESET, TextColors.AQUA, PluginInfo.NAME, TextColors.GRAY, " v", TextColors.YELLOW, PluginInfo.VERSION, TextColors.RESET, ": Initializing!",
                TextStyles.BOLD, TextColors.GREEN, " ====="));

        FmConfig.initialize();
        console.sendMessage(Text.of("MaxItemStacks: ", FmConfig.getMaxItemStacks()));
        console.sendMessage(Text.of("ValidShopBlockTypes: ", FmConfig.getValidShopBlockTypes()));

        registerCommands();

        DatabaseManager.initialize();

        if(FmConfig.getCleanOnStartup())
            FmShopCleanExecutor.cleanAll();

        console.sendMessage(Text.of(TextStyles.BOLD, TextColors.GREEN, "===== ",
                TextStyles.RESET, TextColors.AQUA, PluginInfo.NAME, TextColors.GRAY, " v", TextColors.YELLOW, PluginInfo.VERSION, TextColors.RESET, ": Done!",
                TextStyles.BOLD, TextColors.GREEN, " ====="));
    }

    private void registerCommands() {

        /// === Sub Commands

        subCommands.put(FmHelpExecutor.ALIASES, FmHelpExecutor.create());
        subCommands.put(FmGetConfigExecutor.ALIASES, FmGetConfigExecutor.create());
        subCommands.put(FmSetConfigExecutor.ALIASES, FmSetConfigExecutor.create());
        subCommands.put(FmSetAliasExecutor.ALIASES, FmSetAliasExecutor.create());
        subCommands.put(FmTipsExecutor.ALIASES, FmTipsExecutor.create());

        /// === TradeForm Commands

        LinkedHashMap<List<String>, CommandSpec> tradeFormCommands = new LinkedHashMap<>();

        tradeFormCommands.put(FmTradeFormHelpExecutor.ALIASES, FmTradeFormHelpExecutor.create());
        tradeFormCommands.put(FmTradeFormCreateExecutor.ALIASES, FmTradeFormCreateExecutor.create());
        tradeFormCommands.put(FmTradeFormDeleteExecutor.ALIASES, FmTradeFormDeleteExecutor.create());
        tradeFormCommands.put(FmTradeFormListExecutor.ALIASES, FmTradeFormListExecutor.create());
        tradeFormCommands.put(FmTradeFormDetailsExecutor.ALIASES, FmTradeFormDetailsExecutor.create());

        tradeFormCommands.put(FmTradeFormCopyExecutor.ALIASES, FmTradeFormCopyExecutor.create());
        tradeFormCommands.put(FmTradeFormRenameExecutor.ALIASES, FmTradeFormRenameExecutor.create());

        tradeFormCommands.put(FmTradeFormSetTradeTypeExecutor.ALIASES, FmTradeFormSetTradeTypeExecutor.create());

        tradeFormCommands.put(FmTradeFormAddItemExecutor.ALIASES, FmTradeFormAddItemExecutor.create());
        tradeFormCommands.put(FmTradeFormSetItemExecutor.ALIASES, FmTradeFormSetItemExecutor.create());
        tradeFormCommands.put(FmTradeFormRemoveItemExecutor.ALIASES, FmTradeFormRemoveItemExecutor.create());

        tradeFormCommands.put(FmTradeFormAddCurrencyExecutor.ALIASES, FmTradeFormAddCurrencyExecutor.create());
        tradeFormCommands.put(FmTradeFormSetCurrencyExecutor.ALIASES, FmTradeFormSetCurrencyExecutor.create());
        tradeFormCommands.put(FmTradeFormRemoveCurrencyExecutor.ALIASES, FmTradeFormRemoveCurrencyExecutor.create());

        subCommands.put(FmTradeFormExecutor.ALIASES, FmTradeFormExecutor.create(tradeFormCommands));

        grandChildCommands.put(FmTradeFormExecutor.NAME, tradeFormCommands);

        /// === Modifier Commands

        LinkedHashMap<List<String>, CommandSpec> modifierCommands = new LinkedHashMap<>();

        modifierCommands.put(FmModifierHelpExecutor.ALIASES, FmModifierHelpExecutor.create());
        subCommands.put(FmModifierExecutor.ALIASES, FmModifierExecutor.create(modifierCommands));

        grandChildCommands.put(FmModifierExecutor.NAME, modifierCommands);

        /// === Shop Commands

        LinkedHashMap<List<String>,CommandSpec> shopCommands = new LinkedHashMap<>();

        shopCommands.put(FmShopHelpExecutor.ALIASES, FmShopHelpExecutor.create());
        shopCommands.put(FmShopCreateExecutor.ALIASES, FmShopCreateExecutor.create());

        shopCommands.put(FmShopRemoveExecutor.ALIASES, FmShopRemoveExecutor.create());

        shopCommands.put(FmShopListExecutor.ALIASES, FmShopListExecutor.create());
        shopCommands.put(FmShopDetailsExecutor.ALIASES, FmShopDetailsExecutor.create());

        shopCommands.put(FmShopCleanExecutor.ALIASES, FmShopCleanExecutor.create());

        shopCommands.put(FmShopSetTradeFormExecutor.ALIASES, FmShopSetTradeFormExecutor.create());
        shopCommands.put(FmShopSetModifierExecutor.ALIASES, FmShopSetModifierExecutor.create());

        subCommands.put(FmShopExecutor.ALIASES, FmShopExecutor.create(shopCommands));

        grandChildCommands.put(FmShopExecutor.NAME, shopCommands);

        /// === QuickShop Commands

        LinkedHashMap<List<String>, CommandSpec> quickShopCommands = new LinkedHashMap<>();

        quickShopCommands.put(FmQuickShopHelpExecutor.ALIASES, FmQuickShopHelpExecutor.create());
        quickShopCommands.put(FmQuickShopItemBuyExecutor.ALIASES, FmQuickShopItemBuyExecutor.create());
        quickShopCommands.put(FmQuickShopItemSellExecutor.ALIASES, FmQuickShopItemSellExecutor.create());
        quickShopCommands.put(FmQuickShopItemTradeExecutor.ALIASES, FmQuickShopItemTradeExecutor.create());
        quickShopCommands.put(FmQuickShopCurrencyTradeExecutor.ALIASES, FmQuickShopCurrencyTradeExecutor.create());

        subCommands.put(FmQuickShopExecutor.ALIASES, FmQuickShopExecutor.create(quickShopCommands));

        grandChildCommands.put(FmQuickShopExecutor.NAME, quickShopCommands);

        /// === Trade Commands

        LinkedHashMap<List<String>, CommandSpec> tradeCommands = new LinkedHashMap<>();

        tradeCommands.put(FmTradeHelpExecutor.ALIASES, FmTradeHelpExecutor.create());
        //tradeCommands.put(FmTradeListExecutor.ALIASES, FmTradeListExecutor.create());
        //tradeCommands.put(FmTradeAcceptExecutor.ALIASES, FmTradeAcceptExecutor.create());
        //tradeCommands.put(FmTradeDenyExecutor.ALIASES, FmTradeDenyExecutor.create());
        tradeCommands.put(FmTradeSendExecutor.ALIASES, FmTradeSendExecutor.create());
        tradeCommands.put(FmTradeCancelExecutor.ALIASES, FmTradeCancelExecutor.create());
        tradeCommands.put(FmTradeNegotiateExecutor.ALIASES, FmTradeNegotiateExecutor.create());


        subCommands.put(FmTradeExecutor.ALIASES, FmTradeExecutor.create(tradeCommands));

        grandChildCommands.put(FmTradeExecutor.NAME, tradeCommands);

        /// === QuickTrade Commands

        LinkedHashMap<List<String>,CommandSpec> quickTradeCommands = new LinkedHashMap<>();

        quickTradeCommands.put(FmQuickTradeHelpExecutor.ALIASES, FmQuickTradeHelpExecutor.create());
        /*quickTradeCommands.put(FmQuickTradeCreateItemBuyExecutor.ALIASES, FmQuickTradeCreateItemBuyExecutor.create());
        quickTradeCommands.put(FmQuickTradeCreateItemSellExecutor.ALIASES, FmQuickTradeCreateItemSellExecutor.create());
        quickTradeCommands.put(FmQuickTradeCreateItemTradeExecutor.ALIASES, FmQuickTradeCreateItemTradeExecutor.create());
        quickTradeCommands.put(FmQuickTradeCurrencyTradeExecutor.ALIASES, FmQuickTradeCurrencyTradeExecutor.create());*/

        subCommands.put(FmQuickTradeExecutor.ALIASES, FmQuickTradeExecutor.create(quickTradeCommands));

        grandChildCommands.put(FmQuickTradeExecutor.NAME, quickTradeCommands);

        /// === Auction Commands

        LinkedHashMap<List<String>, CommandSpec> auctionCommands = new LinkedHashMap<>();

        auctionCommands.put(FmAuctionHelpExecutor.ALIASES, FmAuctionHelpExecutor.create());

        subCommands.put(FmAuctionExecutor.ALIASES, FmAuctionExecutor.create(auctionCommands));

        grandChildCommands.put(FmAuctionExecutor.NAME, auctionCommands);

        /// === Depot Commands

        LinkedHashMap<List<String>, CommandSpec> depotCommands = new LinkedHashMap<>();

        depotCommands.put(FmDepotHelpExecutor.ALIASES, FmDepotHelpExecutor.create());
        depotCommands.put(FmDepotListExecutor.ALIASES, FmDepotListExecutor.create());
        depotCommands.put(FmDepotClaimExecutor.ALIASES, FmDepotClaimExecutor.create());
        depotCommands.put(FmDepotTossExecutor.ALIASES, FmDepotTossExecutor.create());

        subCommands.put(FmDepotExecutor.ALIASES, FmDepotExecutor.create(depotCommands));

        grandChildCommands.put(FmDepotExecutor.NAME, depotCommands);

        /// === Main Command

        Sponge.getCommandManager().register(this, FmExecutor.create(subCommands), FmExecutor.ALIASES);
    }

    /*@Listener
    public void onLoadComplete(GameLoadCompleteEvent glce) {
        getLogger().info("== " + PluginInfo.NAME + " - GameLoadComplete ==");

        DatabaseManager.initialize();

        FmShopCleanExecutor.cleanAll();

        getLogger().info("== == FIN == ==");
    }*/
}
