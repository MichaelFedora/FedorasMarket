package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.database.DatabaseQuery;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 2/25/2016.
 */
public class FmTradeFormDetailsExecutor extends FmExecutorBase {

    public static final List<String> aliases = Arrays.asList("details", "cat");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Lists the details about a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.details")
                .arguments(GenericArguments.string(Text.of("name")))
                .executor(new FmTradeFormDetailsExecutor())
                .build();
    }

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
            throw makeSourceNotPlayerException();
        }

        Player player = (Player) src;

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectWithMore(conn, 1, player.getUniqueId(), DatabaseCategory.TRADEFORM, name);

            msg(src, "Transaction [" + name + "] details: ");
            if(resultSet.next()) {
                printResult(src, name, resultSet.getObject(DatabaseQuery.DATA.v));
            }

        } catch (SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
