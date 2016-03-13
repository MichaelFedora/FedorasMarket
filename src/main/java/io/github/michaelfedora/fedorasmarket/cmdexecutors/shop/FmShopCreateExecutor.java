package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceData;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataManipulatorBuilder;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
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
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 2/27/2016.
 */
public class FmShopCreateExecutor extends FmExecutorBase {

    public static Map<UUID,Tuple<String,String>> to_apply = new HashMap<>();

    @Override
    protected String getName() {
        return "shop create";
    }

    @Listener
    public void OnInteractSecondary(InteractBlockEvent.Secondary event, @First Player player) {
        if(!to_apply.containsKey(player.getUniqueId()))
            return;

        UUID playerId = player.getUniqueId();
        String name = to_apply.get(playerId).getFirst();
        String modifier_name = to_apply.get(playerId).getSecond();
        to_apply.remove(playerId);

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

            ResultSet resultSet = DatabaseManager.tradeFormDB.selectWithMore(conn, playerId, name, "LIMIT 1");

            if(!resultSet.next()) {
                error(player,"Bad name! :c");
                return;
            }

            tradeformData = (SerializedTradeForm) resultSet.getObject("data");

            // =====

            resultSet = DatabaseManager.shopDataDB.select(conn, playerId, name);

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
                DatabaseManager.shopDataDB.insert(conn, playerId, name, instance, new SerializedShopData(tradeformData, ShopModifier.NONE, sign.getLocation().getPosition(), sign.getLocation().getExtent().getUniqueId()));
                msg(player, "Made the shop!");
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
            throw sourceNotPlayerException;

        UUID uuid = ((Player) src).getUniqueId();

        String name = ctx.<String>getOne("formname").orElseThrow(makeParamExceptionSupplier("formname"));
        String modifier_name = ctx.<String>getOne("modifier").orElse("");

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.tradeFormDB.selectWithMore(conn, uuid, name, "LIMIT 1");

            if(!resultSet.next()) {
                error(src,"Didn't find anything :o");
                return CommandResult.empty();
            }

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        to_apply.put(uuid, new Tuple<>(name, modifier_name));

        src.sendMessage(Text.of("Select a block!"));

        return CommandResult.success();
    }
}
