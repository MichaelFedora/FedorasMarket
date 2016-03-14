package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop.quickcreate;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceData;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataManipulatorBuilder;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.shop.SerializedShopData;
import io.github.michaelfedora.fedorasmarket.shop.ShopModifier;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.util.Tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 3/13/2016.
 */
public abstract class FmShopQuickCreateBase extends FmExecutorBase {

    public static Map<UUID, Tuple<String, TradeForm>> to_apply = new HashMap<>();
    public static Set<UUID> as_server = new HashSet<>();

    public void OnInteractSecondary(InteractBlockEvent.Secondary event, Player player) {

        event.setCancelled(true);

        UUID playerId = player.getUniqueId();
        String name = to_apply.get(playerId).getFirst();
        TradeForm tradeForm = to_apply.get(playerId).getSecond();
        to_apply.remove(playerId);
        boolean isServerOwned = as_server.contains(playerId);
        as_server.remove(playerId);


        BlockSnapshot blockSnapshot = event.getTargetBlock();
        if(blockSnapshot.getState().getType() != BlockTypes.WALL_SIGN) {
            error(player, "Bad block :c . I need a wall-sign!");
            return;
        }

        Sign sign;
        {
            Optional<Sign> opt_sign = FmUtil.getSignFromBlockSnapshot(blockSnapshot);
            if(!opt_sign.isPresent()) {
                error(player, "Bad block :c . I need a sign! (but should've already been checked?)");
                return;
            }
            sign = opt_sign.get();
        }

        UUID instance;

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.shopDataDB.select(conn, playerId, name);

            boolean foundUnique;
            UUID otherId;
            while(true) { // find a unique uuid;

                instance = UUID.randomUUID();

                foundUnique = true;
                while (resultSet.next()) {

                    otherId = (UUID) resultSet.getObject("instance");

                    if (instance.equals(otherId)) {
                        foundUnique = false;
                        break;
                    }
                }

                if (foundUnique)
                    break;
            }

            // =====

            ShopReferenceDataManipulatorBuilder builder = (ShopReferenceDataManipulatorBuilder) Sponge.getDataManager().getManipulatorBuilder(ShopReferenceData.class).get();
            ShopReferenceData data = builder.createFrom(new ShopReference(playerId, name, instance));
            DataTransactionResult dtr = sign.offer(data);

            if(dtr.isSuccessful()) {
                DatabaseManager.shopDataDB.insert(conn, playerId, name, instance, new SerializedShopData(tradeForm.serialize(), ShopModifier.NONE, sign.getLocation().getPosition(), sign.getLocation().getExtent().getUniqueId(), isServerOwned));
                msg(player, "Made the " + ((isServerOwned) ? "server-" : "")+ "shop!");
            } else {
                error(player, "Could not pass data to sign!");
                return;
            }

        } catch(SQLException e) {
            throwSafeException("SQL Error", e, player);
            return;
        }
    }

}
