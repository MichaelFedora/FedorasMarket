package io.github.michaelfedora.fedorasmarket.cmdexecutors.depot;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmDepotListExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("list", "l");

    public static final String NAME = FmDepotExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmDepotExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("List the items in the depot"))
                .permission(PERM)
                .executor(new FmDepotListExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        Player player = (Player) src;
        UUID playerId = player.getUniqueId();

        Text.Builder tb = Text.builder();

        try(Connection conn = DatabaseManager.getConnection()) {

            Map<Integer, ItemStack> depot = new TreeMap<>(DatabaseManager.depot.getAllFor(conn, playerId.toString()));

            int i = 0;
            for(Map.Entry entry : depot.entrySet()) {
                tb.append(Text.of(TextStyles.BOLD, TextColors.GREEN, ++i, TextStyles.RESET, TextColors.GRAY, "(", entry.getKey(), ") ", TextColors.BLUE, "[", TextColors.WHITE, entry.getValue(), TextColors.BLUE, "]"));
                if(i < depot.size()) {
                    tb.append(Text.of(TextColors.GRAY, ", "));
                }
            }

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
