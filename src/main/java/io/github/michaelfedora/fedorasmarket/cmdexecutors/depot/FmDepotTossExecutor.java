package io.github.michaelfedora.fedorasmarket.cmdexecutors.depot;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.database.DatabaseQuery;
import io.github.michaelfedora.fedorasmarket.serializeddata.SerializedItemStack;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmDepotTossExecutor extends FmExecutorBase {

    public static final List<String> aliases = Collections.singletonList("toss");
    public static final String base = "depot";

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Tosses an item from the depot (PERMANENTLY)"))
                .extendedDescription(Text.of("Tosses an item from the depot. WARNING, THIS PERMANENTLY REMOVES THE ITEM. USE WITH CARE!"))
                .permission(PluginInfo.DATA_ROOT + '.' + base + '.' + aliases.get(0))
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("num"))))
                .executor(new FmDepotListExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return base + ' ' + aliases.get(0);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        Player player = (Player) src;
        UUID playerId = player.getUniqueId();

        int num = ctx.<Integer>getOne("number").orElse(1) - 1;

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectWithMore(conn, DatabaseQuery.NAME.v, playerId, DatabaseCategory.DEPOTITEM, "LIMIT 1 OFFSET " + num);

            if(!resultSet.next())
                throw makeException("Couldn't find item #" + num + "! :c");

            Object name = resultSet.getObject(DatabaseQuery.NAME.v);

            DatabaseManager.delete(conn, playerId, DatabaseCategory.DEPOTITEM, name);

        } catch(SQLException e) {

            throw makeException("SQLError", e, src);
        }

        return CommandResult.success();
    }
}
