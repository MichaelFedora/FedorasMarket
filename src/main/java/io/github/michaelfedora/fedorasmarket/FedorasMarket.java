/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
package io.github.michaelfedora.fedorasmarket;

import com.google.inject.Inject;

import com.typesafe.config.ConfigException;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.shop.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.shop.quickcreate.*;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform.*;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ImmutableShopReferenceData;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceBuilder;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceData;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataManipulatorBuilder;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.enumtype.PartyType;
import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import me.flibio.updatifier.Updatifier;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
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
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

@Updatifier(repoName = "FedorasMarket", repoOwner = "MichaelFedora", version = PluginInfo.VERSION)
@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION,description = PluginInfo.DESCRIPTION, authors = PluginInfo.AUTHORS)
public class FedorasMarket {

    private static FedorasMarket instance;

    public static final String ACCOUNT_VIRTUAL_OWNER_PREFIX = "fedorasmarket:v_o_acc_";
    public static final String ACCOUNT_VIRTUAL_CUSTOMER_PREFIX = "fedorasmarket:v_c_acc_";

    public static Set<Class> toRegister = new HashSet<>();

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

        Path file = null;
    }

    private Game game;
    public static Game getGame() { return instance.game; }

    private HashMap<List<String>, CommandSpec> subCommands;
    public static HashMap<List<String>, CommandSpec> getSubCommands() { return instance.subCommands; }
    private HashMap<String, HashMap<List<String>, CommandSpec>> grandChildCommands;
    public static Optional<HashMap<List<String>, CommandSpec>> getGrandChildCommands(String key) {

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
        subCommands = new HashMap<>();
        grandChildCommands = new HashMap<>();

        subCommands.put(FmHelpExecutor.aliases, FmHelpExecutor.create());
        subCommands.put(FmTipsExecutor.aliases, FmTipsExecutor.create());
        subCommands.put(FmSetAliasExecutor.aliases, FmSetAliasExecutor.create());

        HashMap<List<String>, CommandSpec> tradeFormSubCommands = new HashMap<>();

        tradeFormSubCommands.put(FmTradeFormHelpExecutor.aliases, FmTradeFormHelpExecutor.create());
        tradeFormSubCommands.put(FmTradeFormCreateExecutor.aliases, FmTradeFormCreateExecutor.create());
        tradeFormSubCommands.put(FmTradeFormDeleteExecutor.aliases, FmTradeFormDeleteExecutor.create());
        tradeFormSubCommands.put(FmTradeFormDeleteManyExecutor.aliases, FmTradeFormDeleteManyExecutor.create());
        tradeFormSubCommands.put(FmTradeFormListExecutor.aliases, FmTradeFormListExecutor.create());
        tradeFormSubCommands.put(FmTradeFormDetailsExecutor.aliases, FmTradeFormDetailsExecutor.create());

        tradeFormSubCommands.put(FmTradeFormSetTradeTypeExecutor.aliases, FmTradeFormSetTradeTypeExecutor.create());

        tradeFormSubCommands.put(FmTradeFormAddItemExecutor.aliases, FmTradeFormAddItemExecutor.create());
        tradeFormSubCommands.put(FmTradeFormSetItemExecutor.aliases, FmTradeFormSetItemExecutor.create());
        tradeFormSubCommands.put(FmTradeFormRemoveItemExecutor.aliases, FmTradeFormRemoveItemExecutor.create());

        tradeFormSubCommands.put(FmTradeFormAddCurrencyExecutor.aliases, FmTradeFormAddCurrencyExecutor.create());
        tradeFormSubCommands.put(FmTradeFormSetCurrencyExecutor.aliases, FmTradeFormSetCurrencyExecutor.create());
        tradeFormSubCommands.put(FmTradeFormRemoveCurrencyExecutor.aliases, FmTradeFormRemoveCurrencyExecutor.create());

        subCommands.put(FmTradeFormExecutor.aliases, FmTradeFormExecutor.create(tradeFormSubCommands));

        grandChildCommands.put("tradeform", tradeFormSubCommands);

        HashMap<List<String>,CommandSpec> shopSubCommands = new HashMap<>();

        shopSubCommands.put(FmShopHelpExecutor.aliases, FmShopHelpExecutor.create());

        shopSubCommands.put(Arrays.asList("create", "new"), CommandSpec.builder()
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

        shopSubCommands.put(Arrays.asList("details", "cat"), CommandSpec.builder()
                .description(Text.of("Get details about a shop"))
                .permission(PluginInfo.DATA_ROOT + ".shop.details")
                .arguments(GenericArguments.optional(GenericArguments.seq(
                                GenericArguments.string(Text.of("name")),
                                GenericArguments.string(Text.of("instance")))))
                .executor(new FmShopDetailsExecutor())
                .build());

        shopSubCommands.put(Arrays.asList("list", "l"), CommandSpec.builder()
                .description(Text.of("List all shops created by you"))
                .permission(PluginInfo.DATA_ROOT + ".shop.list")
                .executor(new FmShopListExecutor())
                .build());

        shopSubCommands.put(Arrays.asList("remove", "rem"), CommandSpec.builder()
                .description(Text.of("Removes a shop (sign & reference)"))
                .permission(PluginInfo.DATA_ROOT + ".shop.remove")
                .executor(new FmShopRemoveExecutor())
                .build());

        shopSubCommands.put(Collections.singletonList("tips"), CommandSpec.builder()
                .description(Text.of("Cleans up shop \"references\" in the database"))
                .permission(PluginInfo.DATA_ROOT + ".shop.clean")
                .executor(new FmShopCleanExecutor())
                .build());

        HashMap<List<String>,CommandSpec> shopQuickCreateSubCommands = new HashMap<>();

        shopQuickCreateSubCommands.put(FmShopQuickCreateHelpExecutor.aliases, FmShopQuickCreateHelpExecutor.create());
        shopQuickCreateSubCommands.put(FmShopQuickCreateItemBuyExecutor.aliases, FmShopQuickCreateItemBuyExecutor.create());
        shopQuickCreateSubCommands.put(FmShopQuickCreateItemSellExecutor.aliases, FmShopQuickCreateItemSellExecutor.create());
        shopQuickCreateSubCommands.put(FmShopQuickCreateItemTradeExecutor.aliases, FmShopQuickCreateItemTradeExecutor.create());
        shopQuickCreateSubCommands.put(FmShopQuickCreateCurrencyTradeExecutor.aliases, FmShopQuickCreateCurrencyTradeExecutor.create());

        shopSubCommands.put(FmShopQuickCreateExecutor.aliases, FmShopQuickCreateExecutor.create(shopQuickCreateSubCommands));

        grandChildCommands.put("shop quickcreate", shopQuickCreateSubCommands);

        subCommands.put(Arrays.asList("shop", "sh"), CommandSpec.builder()
                .description(Text.of("Do shop things (lists sub commands)"))
                .permission(PluginInfo.DATA_ROOT + ".shop")
                .executor(new FmShopExecutor())
                .children(shopSubCommands)
                .build());

        grandChildCommands.put("shop", shopSubCommands);

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
