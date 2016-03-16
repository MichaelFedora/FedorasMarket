/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
package io.github.michaelfedora.fedorasmarket;

import com.google.inject.Inject;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.quickshop.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.quicktrade.FmQuickTradeExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.shop.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform.*;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ImmutableShopReferenceData;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceBuilder;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceData;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataManipulatorBuilder;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import me.flibio.updatifier.Updatifier;
import org.slf4j.Logger;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.nio.file.Path;
import java.util.*;

@Updatifier(repoName = "FedorasMarket", repoOwner = "MichaelFedora", version = PluginInfo.VERSION)
@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION,description = PluginInfo.DESCRIPTION, authors = PluginInfo.AUTHORS)
public class FedorasMarket {

    private static FedorasMarket instance;

    public static final String ACCOUNT_VIRTUAL_OWNER_PREFIX = "fedorasmarket:v_o_acc_";
    public static final String ACCOUNT_VIRTUAL_CUSTOMER_PREFIX = "fedorasmarket:v_c_acc_";

    @Inject
    private Logger logger;
    public static Logger getLogger() { return instance.logger; }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir; //TODO: Implement config
    public static Path getConfigDir() { return instance.configDir; }

    private EconomyService economyService;
    public static EconomyService getEconomyService() { return instance.economyService; }

    private UserStorageService userStorageService;
    public static UserStorageService getUserStorageService() { return instance.userStorageService; }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if(event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        } else if(event.getService().equals(UserStorageService.class)) {
            userStorageService = (UserStorageService) event.getNewProviderRegistration().getProvider();
        }
    }

    private Game game;
    public static Game getGame() { return instance.game; }

    private LinkedHashMap<List<String>, CommandSpec> subCommands;
    public static LinkedHashMap<List<String>, CommandSpec> getSubCommands() { return instance.subCommands; }
    private HashMap<String, LinkedHashMap<List<String>, CommandSpec>> grandChildCommands;
    public static Optional<LinkedHashMap<List<String>, CommandSpec>> getGrandChildCommands(String key) {

        if(instance.grandChildCommands.containsKey(key))
            return Optional.of(instance.grandChildCommands.get(key));

        return Optional.empty();
    }

    private List<String> chestNames = new ArrayList<>();
    public static List<String> getChestNames() { return instance.chestNames; }

    private int maxItemStacks = 36; // 9 (columns) * 4 (rows) = 36 (slots)
    public static int getMaxItemStacks() { return instance.maxItemStacks; }

    @Listener
    public void onPreInit(GamePreInitializationEvent gpie) {
        instance = this;

        Sponge.getEventManager().registerListeners(this, new PlayerInteractListener());

        Sponge.getDataManager().register(ShopReferenceData.class, ImmutableShopReferenceData.class, new ShopReferenceDataManipulatorBuilder());
        Sponge.getDataManager().registerBuilder(ShopReference.class, new ShopReferenceBuilder());
    }

    @Listener
    public void onInit(GameInitializationEvent gie) {

        getLogger().info("== " + PluginInfo.NAME + " -- GameInitialization ==");

        game = Sponge.getGame();

        //TODO: Add read-config for chest names
        chestNames.add("minecraft:chest");
        chestNames.add("minecraft:trapped_chest");

        getLogger().info("== == FIN == ==");
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent gpie) {
        getLogger().info("== " + PluginInfo.NAME + " - GamePostInitialization ==");
        subCommands = new LinkedHashMap<>();
        grandChildCommands = new LinkedHashMap<>();

        subCommands.put(FmHelpExecutor.aliases, FmHelpExecutor.create());
        subCommands.put(FmTipsExecutor.aliases, FmTipsExecutor.create());
        subCommands.put(FmSetAliasExecutor.aliases, FmSetAliasExecutor.create());

        LinkedHashMap<List<String>, CommandSpec> tradeFormCommands = new LinkedHashMap<>();

        tradeFormCommands.put(FmTradeFormHelpExecutor.aliases, FmTradeFormHelpExecutor.create());
        tradeFormCommands.put(FmTradeFormCreateExecutor.aliases, FmTradeFormCreateExecutor.create());
        tradeFormCommands.put(FmTradeFormDeleteExecutor.aliases, FmTradeFormDeleteExecutor.create());
        tradeFormCommands.put(FmTradeFormDeleteManyExecutor.aliases, FmTradeFormDeleteManyExecutor.create());
        tradeFormCommands.put(FmTradeFormListExecutor.aliases, FmTradeFormListExecutor.create());
        tradeFormCommands.put(FmTradeFormDetailsExecutor.aliases, FmTradeFormDetailsExecutor.create());

        tradeFormCommands.put(FmTradeFormSetTradeTypeExecutor.aliases, FmTradeFormSetTradeTypeExecutor.create());

        tradeFormCommands.put(FmTradeFormAddItemExecutor.aliases, FmTradeFormAddItemExecutor.create());
        tradeFormCommands.put(FmTradeFormSetItemExecutor.aliases, FmTradeFormSetItemExecutor.create());
        tradeFormCommands.put(FmTradeFormRemoveItemExecutor.aliases, FmTradeFormRemoveItemExecutor.create());

        tradeFormCommands.put(FmTradeFormAddCurrencyExecutor.aliases, FmTradeFormAddCurrencyExecutor.create());
        tradeFormCommands.put(FmTradeFormSetCurrencyExecutor.aliases, FmTradeFormSetCurrencyExecutor.create());
        tradeFormCommands.put(FmTradeFormRemoveCurrencyExecutor.aliases, FmTradeFormRemoveCurrencyExecutor.create());

        subCommands.put(FmTradeFormExecutor.aliases, FmTradeFormExecutor.create(tradeFormCommands));

        grandChildCommands.put(FmTradeFormExecutor.aliases.get(0), tradeFormCommands);

        LinkedHashMap<List<String>,CommandSpec> shopCommands = new LinkedHashMap<>();

        shopCommands.put(FmShopHelpExecutor.aliases, FmShopHelpExecutor.create());

        shopCommands.put(Arrays.asList("create", "new"), CommandSpec.builder()
                .description(Text.of("Create a new shop"))
                .permission(PluginInfo.DATA_ROOT + ".shop.create")
                .arguments(
                        GenericArguments.string(Text.of("formname")),
                        GenericArguments.optional(GenericArguments.string(Text.of("modifiername"))),
                        GenericArguments.flags()
                                .flag("s", "-server")
                                .buildWith(GenericArguments.none()))
                .executor(new FmShopCreateExecutor())
                .build());

        shopCommands.put(Arrays.asList("details", "cat"), CommandSpec.builder()
                .description(Text.of("Get details about a shop"))
                .permission(PluginInfo.DATA_ROOT + ".shop.details")
                .arguments(GenericArguments.optional(GenericArguments.seq(
                                GenericArguments.string(Text.of("name")),
                                GenericArguments.string(Text.of("instance")))))
                .executor(new FmShopDetailsExecutor())
                .build());

        shopCommands.put(Arrays.asList("list", "l"), CommandSpec.builder()
                .description(Text.of("List all shops created by you"))
                .permission(PluginInfo.DATA_ROOT + ".shop.list")
                .executor(new FmShopListExecutor())
                .build());

        shopCommands.put(Arrays.asList("remove", "rem"), CommandSpec.builder()
                .description(Text.of("Removes a shop (sign & reference)"))
                .permission(PluginInfo.DATA_ROOT + ".shop.remove")
                .executor(new FmShopRemoveExecutor())
                .build());

        shopCommands.put(FmShopSetTradeFormExecutor.aliases, FmShopSetTradeFormExecutor.create());

        shopCommands.put(Collections.singletonList("tips"), CommandSpec.builder()
                .description(Text.of("Cleans up shop \"references\" in the database"))
                .permission(PluginInfo.DATA_ROOT + ".shop.clean")
                .executor(new FmShopCleanExecutor())
                .build());

        subCommands.put(FmShopExecutor.aliases, FmShopExecutor.create(shopCommands));

        grandChildCommands.put(FmShopExecutor.aliases.get(0), shopCommands);

        LinkedHashMap<List<String>,CommandSpec> quickShopCommands = new LinkedHashMap<>();

        quickShopCommands.put(FmQuickShopHelpExecutor.aliases, FmQuickShopHelpExecutor.create());
        quickShopCommands.put(FmQuickShopCreateItemBuyExecutor.aliases, FmQuickShopCreateItemBuyExecutor.create());
        quickShopCommands.put(FmQuickShopCreateItemSellExecutor.aliases, FmQuickShopCreateItemSellExecutor.create());
        quickShopCommands.put(FmQuickShopCreateItemTradeExecutor.aliases, FmQuickShopCreateItemTradeExecutor.create());
        quickShopCommands.put(FmQuickShopCurrencyTradeExecutor.aliases, FmQuickShopCurrencyTradeExecutor.create());

        subCommands.put(FmQuickShopExecutor.aliases, FmQuickShopExecutor.create(quickShopCommands));

        grandChildCommands.put(FmQuickShopExecutor.aliases.get(0), quickShopCommands);

        LinkedHashMap<List<String>,CommandSpec> quickTradeCommands = new LinkedHashMap<>();

        /*quickTradeCommands.put(FmQuickTradHelpeExecutor.aliases, FmQuickTradeHelpExecutor.create());
        quickTradeCommands.put(FmQuickTradeCreateItemBuyExecutor.aliases, FmQuickTradeCreateItemBuyExecutor.create());
        quickTradeCommands.put(FmQuickTradeCreateItemSellExecutor.aliases, FmQuickTradeCreateItemSellExecutor.create());
        quickTradeCommands.put(FmQuickTradeCreateItemTradeExecutor.aliases, FmQuickTradeCreateItemTradeExecutor.create());
        quickTradeCommands.put(FmQuickTradeCurrencyTradeExecutor.aliases, FmQuickTradeCurrencyTradeExecutor.create());*/

        subCommands.put(FmQuickTradeExecutor.aliases, FmQuickTradeExecutor.create(quickShopCommands));

        grandChildCommands.put(FmQuickShopExecutor.aliases.get(0), quickShopCommands);

        /*subCommands.put(Arrays.asList("offertrade", "otrade"), CommandSpec.builder()
                .description(Text.of("Offer to Trade to another player the item in your hand"))
                .permission(PluginInfo.DATA_ROOT + ".offertrade")
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.catalogedElement(Text.of("itemname"), ItemType.class),
                        GenericArguments.integer(Text.of("amount")))
                .executor(new FmTradeExecutor())
                .build());

        subCommands.put(Arrays.asList("offersell", "osell"), CommandSpec.builder()
                .description(Text.of("Offer to Trade to another player the item in your hand"))
                .permission(PluginInfo.DATA_ROOT + ".offersell")
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.catalogedElement(Text.of("itemname"), ItemType.class),
                        GenericArguments.integer(Text.of("amount")))
                .executor(new FmSellExecutor())
                .build());*/


        CommandSpec fmCommandSpec = CommandSpec.builder()
                .description(Text.of("FedorasMarket Command"))
                .permission(PluginInfo.DATA_ROOT + ".use")
                .children(subCommands)
                .executor(new FmExecutor())
                .build();

        Sponge.getCommandManager().register(this, fmCommandSpec, "fedmarket", "fm");

        getLogger().info("== == FIN == ==");
    }

    @Listener
    public void onLoadComplete(GameLoadCompleteEvent glce) {
        getLogger().info("== " + PluginInfo.NAME + " - GameLoadComplete ==");

        DatabaseManager.initialize();

        FmShopCleanExecutor.cleanAll();

        getLogger().info("== == FIN == ==");
    }
}
