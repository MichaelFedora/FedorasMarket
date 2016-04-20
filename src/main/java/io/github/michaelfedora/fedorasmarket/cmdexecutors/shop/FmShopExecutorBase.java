package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.shop.ShopData;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.util.Tuple;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 3/15/2016.
 */
public abstract class FmShopExecutorBase extends FmExecutorBase {

    public static Map<UUID, Tuple<TradeForm, Boolean>> to_apply = new HashMap<>();

    public void OnInteractSecondary(InteractBlockEvent.Secondary event, Player player) {

        if(!to_apply.containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);

        UUID playerId = player.getUniqueId();
        TradeForm tradeForm = to_apply.get(playerId).getFirst();
        boolean sudo = to_apply.get(playerId).getSecond();
        to_apply.remove(playerId);

        BlockSnapshot blockSnapshot = event.getTargetBlock();
        Optional<Sign> opt_sign = FmUtil.getShopSignFromBlockSnapshot(blockSnapshot);

        if(!opt_sign.isPresent()) {
            error(player, "Bad block :c . I need a wall-sign!");
            return;
        }

        Sign sign = opt_sign.get();
        Shop shop = Shop.fromSign(sign).orElse(null);
        if(shop == null)
            return;

        ShopData data = shop.getData().setTradeForm(tradeForm);

        try(Connection conn = DatabaseManager.getConnection()) {

            DatabaseManager.update(conn, data.serialize(), (sudo) ? null : playerId, DatabaseCategory.SHOPDATA, shop.getInstance());

        } catch(SQLException e) {
            throwSafeException("SQL Error", e, player);
            return;
        }
    }

}
