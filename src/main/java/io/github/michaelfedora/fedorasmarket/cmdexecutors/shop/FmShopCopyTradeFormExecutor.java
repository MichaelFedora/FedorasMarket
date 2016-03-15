package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Michael on 2/29/2016.
 */
public class FmShopCopyTradeFormExecutor extends FmExecutorBase {
    @Override
    protected String getName() {
        return "shop copytradeform";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        return CommandResult.success();
    }
}
