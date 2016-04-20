package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.persistance.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 2/29/2016.
 */
public class FmShopDetailsExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("details", "cat");

    public static final String NAME = FmShopExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmShopExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Get details about a shop"))
                .permission(PERM)
                .executor(new FmShopDetailsExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void printShopReferenceNice(CommandSource src, ShopReference shopReference, Object data) {

        String author_name = "";

        Optional<UserStorageService> opt_uss = Sponge.getServiceManager().provide(UserStorageService.class);
        if(opt_uss.isPresent()) {

            Optional<User> opt_user = opt_uss.get().get(shopReference.author);

            if(opt_user.isPresent())
                author_name = opt_user.get().getName();
        }

        if(author_name.equals("")){
            author_name = shopReference.author.toString();
        }

        src.sendMessage(Text.of(TextColors.BLUE, "Made by: ", TextColors.WHITE, author_name, TextColors.GREEN, ", ",
                TextColors.BLUE, "Shop: [", TextColors.WHITE, shopReference.instance, TextColors.BLUE, "]", TextColors.GREEN, ", ",
                TextColors.BLUE, " Data: ", TextColors.WHITE, data, TextColors.GREEN, "}"));
    }

    public void OnInteractSecondary(InteractBlockEvent.Secondary event, Player player) {

        event.setCancelled(true);

        BlockSnapshot blockSnapshot = event.getTargetBlock();
        if(blockSnapshot.getState().getType() != BlockTypes.WALL_SIGN) {
            error(player, "Bad block :c . Wall-Sign pls!");
            return;
        }

        // FIX-ME: 2/27/2016 ; actually use optionals pls FIXED?
        Sign sign;
        {
            Optional<Sign> opt_sign = FmUtil.getShopSignFromBlockSnapshot(event.getTargetBlock());
            if(!opt_sign.isPresent()) {
                error(player, "No sign present?");
                return;
            }
            sign = opt_sign.get();
        }

        if(sign.get(FmDataKeys.SHOP_REFERENCE).isPresent()) {
            ShopReference shopReference = sign.get(FmDataKeys.SHOP_REFERENCE).get();

            try(Connection conn = DatabaseManager.getConnection()) {
                ResultSet resultSet = DatabaseManager.selectWithMore(conn, DatabaseQuery.DATA.v, shopReference.author, DatabaseCategory.SHOPDATA, shopReference.instance, "LIMIT 1");
                if(resultSet.next()) {
                    msg(player, "Shop details: ");
                    this.printShopReferenceNice(player, shopReference, resultSet.getObject(DatabaseQuery.DATA.v));
                } else
                    msg(player, "Shop exists, but has no details :c");

                conn.close();

            } catch (SQLException e) {
                throwSafeException("SQL Error", e, player);
                return;
            }
        } else
            error(player, "Data key not supported D:");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        UUID playerId = ((Player) src).getUniqueId();

        //TODO: See if they specified a `Location`

        PlayerInteractListener.toRun.put(playerId, this::OnInteractSecondary);

        msg(src, "Select a block!");

        return CommandResult.success();
    }
}
