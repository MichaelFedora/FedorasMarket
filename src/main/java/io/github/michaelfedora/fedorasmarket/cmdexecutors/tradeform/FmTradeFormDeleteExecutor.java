package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTradeFormDeleteExecutor extends FmExecutorBase {

    public static final List<String> aliases = Arrays.asList("delete", "del");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Delete a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.delete")
                .arguments(GenericArguments.string(Text.of("name")))
                .executor(new FmTradeFormDeleteExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "tradeform delete";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            throw sourceNotPlayerException;
        }

        Player player = (Player) src;

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        try(Connection conn = DatabaseManager.getConnection()) {

            DatabaseManager.tradeFormDB.delete(conn, player.getUniqueId(), name);

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
