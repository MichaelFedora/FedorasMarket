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
| `/fm` | `fedorasmarket.use` | Root command, shows version info
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
##### Modifier
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
##### Shop
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
##### Quickshop
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
##### Trade
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
##### Quicktrade
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
##### Auction
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
##### Depot
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
---
### TODO
 - MySQL Support (low priority)

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
