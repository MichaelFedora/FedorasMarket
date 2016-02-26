package io.github.michaelfedora.fedorasmarket;

/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */

import com.google.inject.Inject;

import com.typesafe.config.ConfigException;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.*;
import io.github.michaelfedora.fedorasmarket.data.PartyType;
import io.github.michaelfedora.fedorasmarket.data.TradeType;
import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import me.flibio.updatifier.Updatifier;
import org.slf4j.Logger;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Updatifier(repoName = "FedorasMarket", repoOwner = "MichaelFedora", version = PluginInfo.VERSION)
@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION)
public class FedorasMarket {

    private static FedorasMarket instance; // TODO: Set to an optional ;3

    public static final String ACCOUNT_SERVER_ID = "fedorasmarket:server_account";
    public static final String ACCOUNT_VIRTUAL_OWNER_ID_PREFIX = "fedorasmarket:virtual_owner_account_"; //TODO: Read up to see if these are too big
    public static final String ACCOUNT_VIRTUAL_CUSTOMER_ID_PREFIX = "fedorasmarket:virtual_customer_account_";

    public static final String DB_TRANSACTION_ID = "jdbc:h2:./mods/FedorasData/market.db";

    @Inject
    private Logger logger;
    public static Logger getLogger() { return instance.logger; }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir; //TODO: Implement config
    public static Path getConfigDir() { return instance.configDir; }

    private EconomyService economyService;
    public static EconomyService getEconomyService() { return instance.economyService; }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if(event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
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

    private List<String> chestNames = new ArrayList<String>();
    public static List<String> getChestNames() { return instance.chestNames; }

    private int maxItemStacks = 36;
    public static int getMaxItemStacks() { return instance.maxItemStacks; }

    private SqlService sql;
    public static javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
        if(instance.sql == null) {
            instance.sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return instance.sql.getDataSource(jdbcUrl);
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent gpie) {
        instance = this;
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

        subCommands.put(Arrays.asList("help", "?"), CommandSpec.builder()
                .description(Text.of("Help Command"))
                .permission(PluginInfo.DATA_ROOT + ".help")
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page no"))))
                .executor(new FmHelpExecutor())
                .build());

        HashMap<List<String>, CommandSpec> transSubCommands = new HashMap<>();

        transSubCommands.put(Arrays.asList("help", "?"), CommandSpec.builder()
                .description(Text.of("Help Command"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.help")
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page no")))) // is this used I dun even
                .executor(new FmTransactionHelpExecutor())
                .build());

        transSubCommands.put(Arrays.asList("create", "make", "new"), CommandSpec.builder()
                .description(Text.of("Create a transaction"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.create")
                .arguments(
                        GenericArguments.string(Text.of("trans_name")),
                        GenericArguments.optional(GenericArguments.enumValue(Text.of("trade_type"), TradeType.class)))
                .executor(new FmTransactionCreateExecutor())
                .build());

        transSubCommands.put(Arrays.asList("delete", "del"), CommandSpec.builder()
                .description(Text.of("Delete a transaction"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.delete")
                .arguments(GenericArguments.string(Text.of("trans_name")))
                .executor(new FmTransactionDeleteExecutor())
                .build());

        transSubCommands.put(Arrays.asList("deletemany", "delm"), CommandSpec.builder()
                .description(Text.of("Delete many transactions"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.deletemany")
                .arguments(GenericArguments.allOf(GenericArguments.string(Text.of("trans_names"))))
                .executor(new FmTransactionDeleteManyExecutor())
                .build());

        transSubCommands.put(Arrays.asList("list", "l"), CommandSpec.builder()
                .description(Text.of("Lists all transactions"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.list")
                .executor(new FmTransactionListExecutor())
                .build());

        transSubCommands.put(Arrays.asList("details", "cat"), CommandSpec.builder()
                .description(Text.of("Lists the details about a transaction"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.details")
                .arguments(GenericArguments.string(Text.of("trans_name")))
                .executor(new FmTransactionDetailsExecutor())
                .build());

        transSubCommands.put(Arrays.asList("settradetype", "settype"), CommandSpec.builder()
                .description(Text.of("Set the TradeType of the transaction"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.settradetype")
                .arguments(
                        GenericArguments.string(Text.of("trans_name")),
                        GenericArguments.enumValue(Text.of("type"), TradeType.class))
                .executor(new FmTransactionSetTradeTypeExecutor())
                .build());

        //TODO: Add "set" commands (because add commands do actually add...)
        transSubCommands.put(Arrays.asList("additem", "addi"), CommandSpec.builder()
                .description(Text.of("Add an item amount to a transaction"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.additem")
                .arguments(
                        GenericArguments.string(Text.of("trans_name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class),
                        GenericArguments.integer(Text.of("amount")))
                .executor(new FmTransactionAddItemExecutor())
                .build()); // perhaps SELECT? and then edit? Hmm...

        transSubCommands.put(Arrays.asList("removeitem", "remi"), CommandSpec.builder()
                .description(Text.of("Remove an item entry from a transaction"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.removeitem")
                .arguments(
                        GenericArguments.string(Text.of("trans_name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class))
                .executor(new FmTransactionRemoveItemExecutor())
                .build());

        Map<String, Currency> currencies = new TreeMap<>();
        for(Currency c : this.economyService.getCurrencies()) {
            currencies.put(c.getDisplayName().toPlain(), c);
        }

        transSubCommands.put(Arrays.asList("addcurrency", "addc"), CommandSpec.builder()
                .description(Text.of("Add a currency amount to a transaction"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.addcurrency")
                .arguments(
                        GenericArguments.string(Text.of("trans_name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.optional(GenericArguments.choices(Text.of("currency"), currencies, true)))
                .executor(new FmTransactionAddCurrencyExecutor())
                .build());

        transSubCommands.put(Arrays.asList("removecurrency", "remc"), CommandSpec.builder()
                .description(Text.of("Remove a currency from a transaction"))
                .permission(PluginInfo.DATA_ROOT + ".transaction.removecurrency")
                .arguments(
                        GenericArguments.string(Text.of("trans_name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.optional(GenericArguments.choices(Text.of("currency"), currencies, true)))
                .executor(new FmTransactionRemoveCurrencyExecutor())
                .build());


        subCommands.put(Arrays.asList("transaction", "trans", "tr"), CommandSpec.builder()
                .description(Text.of("Do transaction things (lists sub commands)"))
                .permission(PluginInfo.DATA_ROOT + ".transaction")
                .executor(new FmTransactionExecutor())
                .children(transSubCommands)
                .build());

        grandChildCommands.put("transaction", transSubCommands);

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
        try {
            Connection conn = getDataSource(DB_TRANSACTION_ID).getConnection();

            try {
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS fm_transactions(id uuid, trans_name varchar(255), data object)").execute();
                ResultSet resultSet = conn.prepareStatement("SELECT * FROM fm_transactions").executeQuery();
                while(resultSet.next()) {
                    getLogger().info("" + resultSet.getObject(1) + " | " + resultSet.getString(2) + " | " + ((TradeTransaction.Data) resultSet.getObject(3)).deserialize());
                }
            } finally {
                conn.close();
            }

        } catch(SQLException e) {
            getLogger().error("OnLoadComplete(:c)", e);
        }
        getLogger().info("== == FIN == ==");
    }

    //@Listener void onSignChangeevent(ChangeSignEvent event) { } // Do I still need this?

    public void setupShop(Sign sign, Player player, List<String> lines) {
        getLogger().info("Setting up shop for sign `" + sign + "`!");

        BlockState sign_bs = sign.getBlock();

        //== If both same action (buy/sell), put larger quantity on top
        //== If different actions, put sell on bottom (primary)
        //== Register a shop by placing a book with parameters inside the chest, and then a sign outside with [FCS-Register]
        //== == Book Shop Params: item ; buy/sell:amount:price ; buy/sell:amount:price
        //== == Book Trade Params: take_item ; give_item ; take:give ratio
        //== == Book Custom Params: { {item, itemAmt}, ... }, { {currency, currencyAmt}, ... };
        //== == == page 1 for owner, 2 for customer
        //== == == i.e.: minecraft:trapped_chest ; buy:1:10 ; sell:1:25 ;
        //== == == i.e.: minecraft:iron_ingot ; minecraft:gold_nugget ; 1:14
        //== == Sign Params: [FedChestShop] ; flags
        //== == Shop-Sign becomes: [FCS][Shop] ; [ScrollingItemName] ; B/S:#:$ /*Secondary*/ ; B/S:#:$ /*Primary*/
        //== == Colors:            Black Bold  ; Black               ; Green/Red   ; Green/Red [Bold & Dark if Larger Quantity & same op]
        //== == Trade-Sign becomes: [FCS][Trade] ; [ScrollingTakeItemName] ; ratio ; [ScrollingGiveItemName]
        //== == Colors:             Purple Bold  ; Dark Red (Bold)         ; red:green ; Dark Green (Bold)

        /*
         * Different shop types:
         * -- ITEM_BUY, ITEM_SELL, ITEM_TRADE, CURRENCY_TRADE, CUSTOM
         * Flags are the same, disregards case of letters (toUpper), matches via (i.e.) Shop.TradeType.ITEM_BUY.name();
         *
         * Register a shop by right clicking a sign with a signed book with the name "FCS-REGISTER"
         *      [TradeType]
         *
         *
         *
         */

        {
            boolean attached;
            {
                Optional<Boolean> opt_attached = sign_bs.get(Keys.ATTACHED);
                if(!opt_attached.isPresent())
                    return;
                attached = opt_attached.get();
            }
            getLogger().info("Attached: " + attached);
            if (!attached)
                return;
        }

        Direction dir;
        {
            Optional<Direction> opt_dir = sign_bs.get(Keys.DIRECTION);
            if (!opt_dir.isPresent())
                return;
            dir = opt_dir.get();
        }

        Location<World> chest_loc = sign.getLocation().getRelative(dir.getOpposite());
        BlockState chest_bs = chest_loc.getBlock();

        getLogger().info("Direction is " + dir + ", " +
                "Opposite is " + dir.getOpposite() + ", " +
                "Block is " + chest_bs.getType());

        /*getLogger().info("Chest's key's are: " + chest_bs.getKeys());

        {
            String s = "Chest's Traits: [ ";

            for (BlockTrait<?> bt : chest_bs.getTraits()) {
                s += bt.getName() + " ";
            }
            s += "]";
            getLogger().info(s);

        }*/

        {
            String name = chest_bs.getType().getName();
            getLogger().info("Chest's name is: `" + name + "`");

            boolean bad = true;

            for(String chestName : chestNames) {
                if(name.equals(chestName)) {
                    bad = false;
                    break;
                }
            }

            if(bad)
                return;
        }

        getLogger().info("Chest is in the list!");

        TileEntity te;
        {
            Optional<TileEntity> opt_te = chest_loc.getTileEntity();
            if(!opt_te.isPresent())
                return;
            te = opt_te.get();
        }

        if(!(te instanceof TileEntityCarrier))
            return;

        TileEntityCarrier tec = (TileEntityCarrier) te;

        getLogger().info("Chest is a tile entity carrier!");

        Account account;
        {
            Optional<UniqueAccount> opt_uacc = economyService.getAccount(player.getUniqueId());
            if(!opt_uacc.isPresent())
                return;
            account = opt_uacc.get();
        }
        //Shop.makeShop(sign, account, tec.getInventory(), TradeType.ITEM_BUY, new TradeTransaction(new TradeParty().addDefaultCurrency(BigDecimal.valueOf(100)), new TradeParty().addItem(ItemTypes.COBBLESTONE, 32)), new MultiAction(64));

        //=== SETTING UP THE SIGN

        ListValue<Text> sign_lines = sign.getSignData().lines(); // get the text

        //TileEntityCarrier tec = (TileEntityCarrier) te;
        //Inventory inv = tec.getInventory(); //TODO: Implement Inventory (once sponge has it)
    }

    private void trySetup(Sign sign, Player player, ItemStack is) {

        List<String> lines = new ArrayList<String>();
        {
            List<Text> pages;
            {
                Optional<List<Text>> opt_pages = is.get(Keys.BOOK_PAGES);
                if (!opt_pages.isPresent())
                    return;
                pages = opt_pages.get();
            }
            String page = pages.get(0).toPlain();
            String[] list = page.split("\n");
            for (int i = 0; i < list.length; i++)
                lines.set(i, list[i]);
        }

        setupShop(sign, player, lines);
    }

    public static Optional<Sign> getSignFromLocation(Location<World> loc) {
        TileEntity te;
        {
            Optional<TileEntity> opt_te = loc.getTileEntity();
            if(!opt_te.isPresent())
                return Optional.empty(); //TODO: Add errors to all these optional-returns
            te = opt_te.get();
        }
        if(te instanceof Sign)
            return Optional.of((Sign) te);

        return Optional.empty();
    }

    public static Optional<Sign> getSignFromBlockSnapshot(BlockSnapshot bsnap) {

        Optional<Location<World>> opt_loc = bsnap.getLocation();
        if (opt_loc.isPresent())
            return getSignFromLocation(opt_loc.get());

        return Optional.empty();
    }

    @Listener
    public void onBlockInteractPrimary(InteractBlockEvent.Primary event) {

        BlockSnapshot sign_bsnap = event.getTargetBlock();

        Sign sign;
        {
            Optional<Sign> opt_sign = getSignFromBlockSnapshot(sign_bsnap);
            if(!opt_sign.isPresent())
                return;
            sign = opt_sign.get();
        }

        Shop shop;
        {
            Optional<Shop> opt_shop = sign.get(FmDataKeys.SHOP_DATA);
            if (!opt_shop.isPresent())
                return;
            shop = opt_shop.get();
        }

        Player player;
        {
            Optional<Player> opt_player = event.getCause().first(Player.class);
            if(!opt_player.isPresent())
                return;
            player = opt_player.get();
        }

        shop.doSecondary(player);
    }

    @Listener
    public void onBlockInteractSecondary(InteractBlockEvent.Secondary event) {

        BlockSnapshot sign_bsnap = event.getTargetBlock();
        Sign sign;
        {
            Optional<Sign> opt_sign = getSignFromBlockSnapshot(sign_bsnap);
            if(!opt_sign.isPresent())
                return; //TODO: Add note to these things?
            sign = opt_sign.get();
        }

        Player player;
        {
            Optional<Player> opt_player = event.getCause().first(Player.class);
            if(!opt_player.isPresent())
                return;
            player = opt_player.get();
        }


        {
            Optional<ItemStack> opt_is = player.getItemInHand();
            if (opt_is.isPresent()) {
                if (opt_is.get().getItem().getName().equals("minecraft:writable_book")) {
                    trySetup(sign, player, opt_is.get());
                    return;
                }
            }
        }

        // if not setting up shop, lets check if the sign is valid in the first place
        Shop shop;
        {
            Optional<Shop> opt_shop = sign.get(FmDataKeys.SHOP_DATA);
            if (!opt_shop.isPresent())
                return;
            shop = opt_shop.get();
        }

        shop.doPrimary(player);
    }
}
