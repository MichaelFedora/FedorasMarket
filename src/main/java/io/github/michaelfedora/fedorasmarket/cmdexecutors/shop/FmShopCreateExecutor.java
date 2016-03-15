package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceData;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataManipulatorBuilder;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseQuery;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
import io.github.michaelfedora.fedorasmarket.shop.SerializedShopData;
import io.github.michaelfedora.fedorasmarket.shop.ShopModifier;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.util.Tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 2/27/2016.
 */
public class FmShopCreateExecutor extends FmExecutorBase {

    public static Map<UUID,Tuple<String,String>> to_apply = new HashMap<>();
    public static Set<UUID> as_server = new HashSet<>();

    @Override
    protected String getName() {
        return "shop create";
    }

    public void OnInteractSecondary(InteractBlockEvent.Secondary event, Player player) {

        event.setCancelled(true);

        UUID playerId = player.getUniqueId();
        String name = to_apply.get(playerId).getFirst();
        String modifier_name = to_apply.get(playerId).getSecond();
        to_apply.remove(playerId);
        boolean isServerOwned = as_server.contains(playerId);
        as_server.remove(playerId);

        Optional<UUID> opt_ownerId = (isServerOwned) ? Optional.empty() : Optional.of(playerId);

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

        SerializedTradeForm tradeformData;
        UUID instance;

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectWithMore(conn, 1, playerId, DatabaseCategory.TRADEFORM, name);

            if(!resultSet.next()) {
                error(player,"Bad name! :c");
                return;
            }

            tradeformData = (SerializedTradeForm) resultSet.getObject(DatabaseQuery.DATA.v);

            // =====

            resultSet = DatabaseManager.selectAll(conn, playerId, DatabaseCategory.SHOPDATA, name);

            boolean foundUnique;
            UUID otherId;
            while(true) { // find a unique uuid;

                instance = UUID.randomUUID();

                foundUnique = true;
                while (resultSet.next()) {

                    otherId = (UUID) resultSet.getObject(DatabaseQuery.NAME.v);

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
            ShopReferenceData data = builder.createFrom(new ShopReference(opt_ownerId.orElse(null), instance));
            DataTransactionResult dtr = sign.offer(data);

            if(dtr.isSuccessful()) {
                DatabaseManager.insert(conn, playerId, DatabaseCategory.SHOPDATA, instance, new SerializedShopData(tradeformData, ShopModifier.NONE, sign.getLocation().getPosition(), sign.getLocation().getExtent().getUniqueId(), opt_ownerId));
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

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        UUID playerId = ((Player) src).getUniqueId();

        String name = ctx.<String>getOne("formname").orElseThrow(makeParamExceptionSupplier("formname"));
        String modifier_name = ctx.<String>getOne("modifier").orElse("");

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectWithMore(conn, 1, playerId, DatabaseCategory.TRADEFORM, name);

            if(!resultSet.next()) {
                error(src,"Didn't find anything :o");
                return CommandResult.empty();
            }

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        to_apply.put(playerId, new Tuple<>(name, modifier_name));

        if(ctx.<Boolean>getOne("s").orElse(false) && src.hasPermission(PluginInfo.DATA_ROOT + ".shop.server"))
            as_server.add(playerId);

        PlayerInteractListener.toRun.put(playerId, this::OnInteractSecondary);

        msg(src, "Select a block!");

        return CommandResult.success();
    }
}
