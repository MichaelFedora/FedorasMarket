package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

/**
 * Get your inbox of shipments (i.e. trades or otherwise)
 */
public class FmInboxExecutor implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        return CommandResult.success();
    }
}
