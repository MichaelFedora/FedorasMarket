# Fedora's Market
###### Built for `Sponge 4.1.0-SNAPSHOT`, and off of `JDK 1.8.0_71`.

A Market plugin, currently in dev, that [will include] chest shops, p2p trading, auctioning, and other things.

###Requirements
- An Economy Implementation
 - I use [Fedora's Economy](https://github.com/MichaelFedora/FedorasEconomy) (*ob*viously `:P`).

---

### Features
- Saving Shop Presets (Trade-forms)
- Player to Player trading
- Chest Shops
- Sign Shops (for currency transactions)
- H2 Database backend
- Permissions

---

### Command List

#### Legend

```
[group, of, aliases]
(optionalArg)
<requiredArg>
```

Permissions are pretty easy to deduce from the commands.
- **Plugin** commands are `fedorasmarket.[first_alias_of_command]` or `fedorasmarket.[category].[first_alias_of_command]`

For the "category" commands or the root command (`/fm`), instead of `[first_alias_of_command]` they are `.use`.

#### Plugin Commands
##### Root
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/[fm, fedorasmarket]` | `fedorasmarket.use` | Root command, shows version info
| `/fm [setconfig, setcfg] [--mis, --maxItemStacks <>] [--cos, --cleanOnStartup <>] [--abt, --addBlockType <>] [--rbt, --remBlockType <>]` | `fedorasmarket.setconfig` | Sets the main-config, via flags
| `/fm [getconfig, getcfg] <configArg>` | `fedorasmarket.getconfig` | Gets and shows the config value specified
| `/fm [help, ?] (cmd)` | `fedorasmarket.help` | Lists help for the specific function, or lists subcommands & usages
| `/fm tips` | `fedorasmarket.tips` | Shows some tips for working with this command
| `/fm setalias` | `fedorasmarket.setalias` | Sets your shop alias (displays on your shop signs)
| `/fm [tradeform, tf] ...` | `fedorasmarket.tradeform.[...]` | The Tradeform category (see below)
| `/fm [modifier, mod] ...` | `fedorasmarket.modifier.[...]` | The Modifier category (see below)
| `/fm [shop, sh] ...` | `fedorasmarket.shop.[...]` | The Shop category (see below)
| `/fm [quickshop, qs] ...` | `fedorasmarket.quickshop.[...]` | The Quickshop category (see below)
| `/fm [trade, tr] ...` | `fedorasmarket.trade.[...]` | The Trade category (see below)
| `/fm [quicktrade, qt] ...` | `fedorasmarket.quicktrade.[...]` | The Quicktrade category (see below)
| `/fm [depot, dp] ...` | `fedorasmarket.depot.[...]` | The Depot category (see below)
| `/fm [auction, auc] ...` | `fedorasmarket.auction.[...]` | The Auction category (see below)

##### Tradeform
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fm [tradeform, tf]` | `fedorasmarket.tradeform.use` | The Tradeform category main-command, lists subcommands
| `/fm tf [help, ?] (cmd)` | `fedorasmarket.tradeform.help` | Lists help for the specific function, or lists subcommands & usages
| `/fm tf [create, new] <name> (type)` | `fedorasmarket.tradeform.create` | Creates a new tradeform with name <name>, and optional shop-type <type> (default is CUSTOM)
| `/fm tf [delete, del] <name>*` | `fedorasmarket.tradeform.delete` | Deletes a tradeform (or multiple; i.e. `/fm tf del test test2 test3`)
| `/fm tf [list, l]` | `fedorasmarket.tradeform.list` | Lists all the tradeforms you have saved
| `/fm tf [details, cat] <name>` | `fedorasmarket.tradeform.details` | Show the details of a particular tradeform
| `/fm tf [copy, cp] <name>` | `fedorasmarket.tradeform.copy` | Either clones own of your own tradeforms or copies one from a physical shop
| `/fm tf [rename, ren] <oldname> <newname>` | `fedorasmarket.tradeform.rename` | Renames a tradeform to a different name
| `/fm tf [settradetype, settype] <name> <type>` | `fedorasmarket.tradeform.settradetype` | Sets the TradeType of a particular tradeform
| `/fm tf [additem, addi] <name> <party> <amount> <item>` | `fedorasmarket.tradeform.additem` | Adds an amount of an item to the specified tradeform's party
| `/fm tf [setitem, seti] <name> <party> <amount> <item>` | `fedorasmarket.tradeform.setitem` | Sets the amount of an item of the specified tradeform's party
| `/fm tf [remitem, remi] <name> <party> <item>` | `fedorasmarket.tradeform.remitem` | Removes the item from the specified tradeform's party
| `/fm tf [addcurrency, addc] <name> <party> <amount> <currency>` | `fedorasmarket.tradeform.addcurrency` | Adds an amount of an currency to the specified tradeform's party
| `/fm tf [setcurrency, setc] <name> <party> <amount> <currency>` | `fedorasmarket.tradeform.setcurrency` | Sets the amount of an currency of the specified tradeform's party
| `/fm tf [remcurrency, remc] <name> <party> <currency>` | `fedorasmarket.tradeform.remcurrency` | Removes the currency from the specified tradeform's party

##### Modifier
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fm [modifier, mod]` | `fedorasmarket.modifier.use` | The modifier category main-command, lists subcommands
| `/fm mod [help, ?] (cmd)` | `fedorasmarket.modifier.help` | Lists help for the specific function, or lists subcommands & usages

##### Shop
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fm [shop, sh]` | `fedorasmarket.shop.use` | The shop category main-command, lists subcommands
| `/fm sh [help, ?] (cmd)` | `fedorasmarket.shop.help` | Lists help for the specific function, or lists subcommands & usages
| `/fm sh [create, new] <formname> (modifiername) [-s, --server]` | `fedorasmarket.shop.create` | Creates a new shop with the tradeform <formname> and the optional modifier (modifiername) (default is NONE)
| `/fm sh [remove, rem]` | `fedorasmarket.shop.remove` | Removes a (physical) shop
| `/fm sh [list, l]` | `fedorasmarket.shop.list` | Lists all the shops you own
| `/fm sh [details, cat]` | `fedorasmarket.shop.details` | Show the details of a particular shop
| `/fm sh [settradeform, settf] <tradeform>` | Sets the shop's tradeform
| `/fm sh [setmodifier, setmod] <modifier>` | Sets the shop's modifier

##### Quickshop
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fm [quickshop, qs]` | `fedorasmarket.quickshop.use` | The quickshop category main-command, lists subcommands
| `/fm qs [help, ?] (cmd)` | `fedorasmarket.quickshop.help` | Lists help for the specific function, or lists subcommands & usages
| `/fm qs [itembuy, ibuy] <item_amt> <item> <currency_amt> <currency> [-s, --server]` | `fedorasmarket.quickshop.itembuy` | Quickly create an ItemBuy shop
| `/fm qs [itemsell, isell] <currency_amt> <currency> <item_amt> <item> [-s, --server]` | `fedorasmarket.quickshop.itemsell` | Quickly create an ItemSell shop
| `/fm qs [itemtrade, itrade] <item1_amt> <item1> <item2_amt> <item2> [-s, --server]` | `fedorasmarket.quickshop.itemtrade` | Quickly create an ItemTrade shop
| `/fm qs [currencytrade, ctrade] <currency1_amt> <currency1> <currency2_amt> <currency2> [-s, --server]` | `fedorasmarket.quickshop.currencytrade` | Quickly create an CurrencyTrade shop

##### Trade
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fm [trade, tr]` | `fedorasmarket.trade.use` | The trade category main-command, lists subcommands
| `/fm tr [help, ?] (cmd)` | `fedorasmarket.trade.help` | Lists help for the specific function, or lists subcommands & usages
| `/fm tr list (filter [sent, recieved, all])` | `fedorasmarket.trade.list` | Lists different trade requests. Default filter is `all`
| `/fm tr accept <num>` | `fedorasmarket.trade.accept` | Accepts a trade & applies it
| `/fm tr deny <num>` | `fedorasmarket.trade.deny` | Denies a trade & throws it away
| `/fm tr [send, s] <tradeform> <reciever>` | `fedorasmarket.trade.send` | Sends a trade "request" to another player
| `/fm tr [cancel, rem] <num>` | `fedorasmarket.trade.cancel` | Cancels a trade request you've sent (`<num>` via `/fm tr list`)
| `/fm tr [negotiate, neg] <sender> <num>` | `fedorasmarket.trade.negotiate` | Copies and allows you to edit a trade sent by another player

##### Quicktrade
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fm [quicktrade, qt]` | `fedorasmarket.quicktrade.use` | The quicktrade category main-command, lists subcommands
| `/fm qt [help, ?] (cmd)` | `fedorasmarket.quicktrade.help` | Lists help for the specific function, or lists subcommands & usages

##### Auction
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fm [aucion, auc]` | `fedorasmarket.auction.use` | The auction category main-command, lists subcommands
| `/fm auc [help, ?] (cmd)` | `fedorasmarket.auction.help` | Lists help for the specific function, or lists subcommands & usages

##### Depot
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fm [depot, dp]` | `fedorasmarket.depot.use` | The depot category main-command, lists subcommands
| `/fm dp [help, ?] (cmd)` | `fedorasmarket.depot.help` | Lists help for the specific function, or lists subcommands & usages
| `/fm dp [list, l] ` | `fedorasmarket.depot.list` | Lists the items in the depot
| `/fm dp [claim, get] (num)` | `fedorasmarket.depot.claim` | Claims an item from the depot (either the top one in the stack, or the selected) and puts it in your inventory
| `/fm dp toss (num)` | `fedorasmarket.depot.toss` | Tosses an item from the depot (either the top one in the stack, or the selected)

---

### TODO
- MySQL Support (low priority)

- Refractor database, for each table to be `[category]:[uuid]`, and then to have `(name, data)` entries.
 - i.e. `shop:{uuid}`, `tradeform:{uuid}`, `userdata:{uuid}`...

---

### Most recent changelog(s)
#### v1.0-SNAPSHOT
- I did nothing `:(`

---

### Links

[Github Source](https://github.com/MichaelFedora/FedorasMarket)

[Downloads](https://github.com/MichaelFedora/FedorasMarket/releases)

---

## Big thanks to a couple of people
 - **God**, for creating this awesome place to live in, and for giving me the time and talents to create this. All glory to him.
 - The **SpongePowered** team for making their API (including all their contributors)
 - And the **SpongeDocs** team, for making it easy to learn!
 - And.. the IRC People, yay. Help is good `:P`

I couldn't have made this without you guys. :heart:

---

Enjoy!

![stats or something](http://i.mcstats.org/FedorasMarket/Global+Statistics.png)

<right><sup>*Soli deo gloria*</sup></right>
