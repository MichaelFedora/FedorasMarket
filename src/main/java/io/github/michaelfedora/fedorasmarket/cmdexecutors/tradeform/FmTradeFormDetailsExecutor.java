package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
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
 * Created by Michael on 2/25/2016.
 */
public class FmTradeFormDetailsExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "tradeform details";
    }

    private void printResult(CommandSource src, Object name, Object data) {
        src.sendMessage(Text.of(TextColors.GREEN, "{",
                TextColors.BLUE, "name=", TextColors.WHITE, name, TextColors.GREEN, ", ",
                TextColors.BLUE, "data=", TextColors.WHITE, data, TextColors.GREEN, "}"));
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            throw sourceNotPlayerException;
        }

        Player player = (Player) src;

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.tradeFormDB.selectWithMore(conn, player.getUniqueId(), name, "LIMIT 1");

            msg(src, "Transaction [" + name + "] details: ");
            if(resultSet.next()) {
                printResult(src, name, resultSet.getObject("data"));
            }

        } catch (SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
