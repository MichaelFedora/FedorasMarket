package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.xml.ws.Holder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Michael on 3/14/2016.
 */
public class FmSetAliasExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("setalias", "setname", "nick");

    public static final String NAME = ALIASES.get(0);
    public static final String PERM = FmExecutor.PERM + '.' + ALIASES.get(0);


    public static CommandSpec create() {
        return CommandSpec.builder()
                .arguments(GenericArguments.string(Text.of("alias")))
                .description(Text.of("Set your alias that is displayed on your shops"))
                .extendedDescription(Text.of("Set your alias that is displayed on your shops. I.e. [FM Shop: Steve]"))
                .permission(PERM)
                .executor(new FmSetAliasExecutor())
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

        UUID playerId = ((Player) src).getUniqueId();

        String alias = ctx.<String>getOne("alias").orElseThrow(makeParamExceptionSupplier("alias"));

        try(Connection conn = DatabaseManager.getConnection()) {

            DatabaseManager.insert(conn, playerId, DatabaseCategory.USERDATA, "alias", new Holder<>(alias));

        } catch (SQLException e) {

            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
