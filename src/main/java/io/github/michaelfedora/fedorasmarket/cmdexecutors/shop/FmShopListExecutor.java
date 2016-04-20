package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.shop.SerializedShopData;
import io.github.michaelfedora.fedorasmarket.shop.ShopData;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Michael on 2/29/2016.
 */
public class FmShopListExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("list", "l");

    public static final String NAME = FmShopExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmShopExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Lists all shops you own"))
                .permission(PERM)
                .executor(new FmShopListExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            throw makeSourceNotPlayerException();
        }

        Player player = (Player) src;

       try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectAll(conn, player.getUniqueId(), DatabaseCategory.SHOPDATA);

            src.sendMessage(FmUtil.makeMessage("All Shops (for your id);"));

            Text.Builder tb = Text.builder();
            while (resultSet.next()) {
                String shopName = "";
                Optional<ShopData> opt_shopData = ((SerializedShopData) resultSet.getObject(DatabaseQuery.DATA.v)).safeDeserialize();
                if(opt_shopData.isPresent())
                    shopName = opt_shopData.get().getLocation().toString();
                else
                    shopName = resultSet.getString(DatabaseQuery.NAME.v);

                tb.append(Text.of(TextColors.BLUE, "[", TextColors.WHITE, shopName, TextColors.BLUE, "]"));

                if (!resultSet.isLast()) {
                    tb.append(Text.of(TextColors.GRAY, ", "));
                }
            }
            src.sendMessage(tb.build());

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
