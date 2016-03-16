package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.database.DatabaseQuery;
import io.github.michaelfedora.fedorasmarket.shop.SerializedShopData;
import io.github.michaelfedora.fedorasmarket.shop.ShopData;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Michael on 2/29/2016.
 */
public class FmShopListExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "shop list";
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
