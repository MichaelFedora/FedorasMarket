package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTradeFormListExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "tradeform list";
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            throw sourceNotPlayerException;
        }

        Player player = (Player) src;

        try(Connection conn = DatabaseManager.getConnection()) {
            ResultSet resultSet = DatabaseManager.tradeFormDB.select(conn, player.getUniqueId());

            src.sendMessage(FmUtil.makeMessage("All Transactions (for your id);"));

            Text.Builder tb = Text.builder();
            while(resultSet.next()) {
                tb.append(Text.of(TextColors.BLUE, "[", TextColors.WHITE, resultSet.getString("name"), TextColors.BLUE, "]"));
                if(!resultSet.isLast()) {
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