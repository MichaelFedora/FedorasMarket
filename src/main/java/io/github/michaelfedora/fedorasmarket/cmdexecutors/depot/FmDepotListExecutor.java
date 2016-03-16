package io.github.michaelfedora.fedorasmarket.cmdexecutors.depot;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.database.DatabaseQuery;
import io.github.michaelfedora.fedorasmarket.serializeddata.SerializedItemStack;
import javafx.scene.text.TextBuilder;
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
import java.util.UUID;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmDepotListExecutor extends FmExecutorBase {

    public static final List<String> aliases = Arrays.asList("list", "l");
    public static final String base = "depot";

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("List the items in the depot"))
                .permission(PluginInfo.DATA_ROOT + '.' + base + '.' + aliases.get(0))
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

        Text.Builder tb = Text.builder();

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.select(conn, DatabaseQuery.DATA.v, playerId, DatabaseCategory.DEPOTITEM);

            while(resultSet.next()) {
                tb.append(Text.of(TextColors.BLUE, "[", TextColors.WHITE, resultSet.getObject(DatabaseQuery.DATA.v), TextColors.BLUE, "]"));
                if(!resultSet.isLast()) {
                    tb.append(Text.of(TextColors.GRAY, ", "));
                }
            }

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
