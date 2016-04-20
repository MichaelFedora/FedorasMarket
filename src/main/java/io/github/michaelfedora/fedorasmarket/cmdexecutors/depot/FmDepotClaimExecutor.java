package io.github.michaelfedora.fedorasmarket.cmdexecutors.depot;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.serializeddata.SerializedItemStack;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
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
public class FmDepotClaimExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("claim", "get");

    public static final String NAME = FmDepotExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmDepotExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Claim an item from your depot"))
                .permission(PERM)
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("num"))))
                .executor(new FmDepotClaimExecutor())
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
        SerializedItemStack serializedItemStack;
        ItemStack itemStack;

        int num = ctx.<Integer>getOne("number").orElse(1) - 1;

        try(Connection conn = DatabaseManager.getConnection()) {

            final String columns = DatabaseQuery.NAME.v + ", " + DatabaseQuery.DATA.v;
            ResultSet resultSet = DatabaseManager.select(conn, columns, playerId, DatabaseCategory.DEPOTITEM, "LIMIT 1 OFFSET " + num);

            if(!resultSet.next())
                throw makeException("Couldn't find item #" + num);

            serializedItemStack = ((SerializedItemStack) resultSet.getObject(DatabaseQuery.DATA.v));

            DatabaseManager.delete(conn, playerId, DatabaseCategory.DEPOTITEM, resultSet.getObject(DatabaseQuery.NAME.v));

        } catch(SQLException e) {

            throw makeException("SQLError", e, src);
        }

        itemStack = serializedItemStack.safeDeserialize().orElseThrow(makeExceptionSupplier("Couldn't make item stack :c"));

        FmUtil.giveItem(itemStack, player);

        msgf(src, Text.of("Here ya go! ", TextColors.BLUE, "[" + itemStack + "]"));

        return CommandResult.success();
    }
}
