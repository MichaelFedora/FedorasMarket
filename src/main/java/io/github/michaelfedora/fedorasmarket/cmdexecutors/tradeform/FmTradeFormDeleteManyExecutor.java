package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
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
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Created by Michael on 2/26/2016.
 */
public class FmTradeFormDeleteManyExecutor extends FmExecutorBase {

    public static final List<String> aliases = Arrays.asList("deletemany", "delm");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Delete many trade forms"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.deletemany")
                .arguments(GenericArguments.allOf(GenericArguments.string(Text.of("names"))))
                .executor(new FmTradeFormDeleteManyExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "tradeform deletemany";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            throw makeSourceNotPlayerException();
        }

        Player player = (Player) src;

        Collection<String> names = ctx.getAll("names");

        try(Connection conn = DatabaseManager.getConnection()) {

            for(String name : names) {
                DatabaseManager.delete(conn, player.getUniqueId(), DatabaseCategory.TRADEFORM, name);
            }

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
