package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Get Version info
 */
public class FmExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "fedmarket";
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {
        src.sendMessage(Text.of(TextColors.AQUA, "FedorasMarket: ",
                TextColors.GRAY, "Version: ",
                TextColors.GOLD, FedorasMarket.getGame().getPluginManager().getPlugin("FedorasMarket").get().getVersion()));
        return CommandResult.success();
    }
}
