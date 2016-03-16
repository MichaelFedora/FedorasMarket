package io.github.michaelfedora.fedorasmarket.cmdexecutors.depot;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmDepotClaimExecutor extends FmExecutorBase {

    public static List<String> aliases = Arrays.asList("claim", "get");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Claim an item from your depot"))
                .permission(PluginInfo.DATA_ROOT + ".depot.claim")
                .arguments(GenericArguments.integer(Text.of("number")))
                .executor(new FmDepotClaimExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "depot claim";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        UUID playerId = ((Player) src).getUniqueId();

        int num = ctx.<Integer>getOne("number").orElseThrow(makeParamExceptionSupplier("number"));

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectAll(conn, playerId, DatabaseCategory.DEPOTITEM, "LIMIT 1 OFFSET " + num);

        } catch(SQLException e) {

            throw makeException("SQLError", e, src);
        }

        return CommandResult.success();
    }
}
