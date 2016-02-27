package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Michael on 2/25/2016.
 */
public class FmTradeFormDetailsExecutor implements CommandExecutor {

    private static Set<UUID> to_cat = new HashSet<>();

    private void printResult(CommandSource src, Object id, Object name, Object data) {
        src.sendMessage(Text.of(TextColors.GREEN, "{",
                TextColors.BLUE, "id=", TextColors.WHITE, id, TextColors.GREEN, ", ",
                TextColors.BLUE, "name=", TextColors.WHITE, name, TextColors.GREEN, ", ",
                TextColors.BLUE, "data=", TextColors.WHITE, data, TextColors.GREEN, "}"));
    }

    private void printShopReferenceResult(CommandSource src, ShopReference shopReference, Object data) {
        src.sendMessage(Text.of(TextColors.GREEN, "{",
                TextColors.BLUE, "id=", TextColors.WHITE, shopReference.author, TextColors.GREEN, ", ",
                TextColors.BLUE, "name=", TextColors.WHITE, shopReference.name, TextColors.GREEN, ", ",
                TextColors.BLUE, "instance=", TextColors.WHITE, shopReference.instance, TextColors.GREEN, ", ",
                TextColors.BLUE, "data=", TextColors.WHITE, data, TextColors.GREEN, "}"));
    }

    public CommandResult error(MessageReceiver to, String msg) {
        to.sendMessage(FmUtil.makeMessageError("tradeform details", msg));
        return CommandResult.empty();
    }

    @Listener
    public void OnSecondaryInteact(InteractBlockEvent.Secondary event, @First Player player) {
        if(!to_cat.contains(player.getUniqueId()))
            return;

        BlockSnapshot blockSnapshot = event.getTargetBlock();
        if(blockSnapshot.getState().getType() != BlockTypes.WALL_SIGN) {
            error(player, "Bad block :c . Wall-Sign pls!");
            return;
        }

        // FIXME: 2/27/2016 ; actually use optionals pls
        Optional<Sign> opt_sign = FmUtil.getSignFromBlockSnapshot(event.getTargetBlock());
        if(!opt_sign.isPresent()) {
            error(player, "No sign present?");
            return;
        }

        Sign sign = opt_sign.get();

        if(sign.supports(FmDataKeys.SHOP_REFERENCE)) {
            ShopReference shopReference = sign.get(FmDataKeys.SHOP_REFERENCE).get();

            try {

                ResultSet resultSet = DatabaseManager.shopForms.select(shopReference);
                if(resultSet.next()) {
                    player.sendMessage(FmUtil.makeMessage("Transaction [" + sign + "] details: "));
                    this.printShopReferenceResult(player, shopReference, resultSet.getObject("data"));
                }

            } catch (SQLException e) {
                FedorasMarket.getLogger().error("SQL Error: ", this, e);
                error(player, "SQL ERROR: See console :c");
                return;
            }
        } else
            error(player, "Bad data key D:");

        // get shop_data data
        // read db
        // print stuff
        // fin yay
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            return FmTradeFormExecutor.errorNotPlayer(src);
        }

        Player player = (Player) src;

        boolean use_db = false;
        Optional<String> opt_name = ctx.<String>getOne("name");
        use_db = opt_name.isPresent();

        Optional<UUID> opt_uuid = Optional.empty();
        Optional<TradeForm.Data> opt_data = Optional.empty();

        if(!use_db) {

            to_cat.add(player.getUniqueId());

        } else { // use_db = true

            opt_uuid = Optional.of(player.getUniqueId());
            String name = opt_name.get();

            try {

                ResultSet resultSet = DatabaseManager.tradeForms.selectWithMore(player.getUniqueId(), name, "LIMIT 1");

                src.sendMessage(FmUtil.makeMessage("Transaction [" + name + "] details: "));
                if(resultSet.next()) {
                    printResult(src, player.getUniqueId(), name, resultSet.getObject("data"));
                }

            } catch (SQLException e) {
                FedorasMarket.getLogger().error("SQL Error: ", this, e);
                return error(src, "SQL ERROR: See console :c");
            }
        }

        return CommandResult.success();
    }
}
