package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.List;

/**
 * Created by Michael on 3/14/2016.
 */
public class FmTipsExecutor extends FmExecutorBase {

    public static final List<String> aliases = Collections.singletonList("tips");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Lists some tips with using this plugin."))
                .permission(PluginInfo.DATA_ROOT + ".tips")
                .executor(new FmTipsExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "tips";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        msg(src, "Welcome to the [" + PluginInfo.NAME + "] plugin!");
        msg(src, "I know things can get a little complicated, so here are a couple of tips.");
        msg(src, "1) When inputting a currency name, you can use 'def' or 'default' to use the default currency.");
        msg(src, "2) You can quickly create a shop using the '/fm quickshop' command");
        msg(src, " - - and quickly create a trade using the /fm 'quicktrade' command!");
        msg(src, "3) There are many aliases, i.e. 'sh' for shop, 'tf' for tradeform, 'qc' for quickcreate.");
        msg(src, "4) Every sub command with more subcommands(tf, sh, sh qc) has a help command...");
        msg(src, " - - Hover over an entry for a description, and click to put it into your chat bar!");
        msg(src, "5) Can't destroy your shop? Use '/fm shop remove', and then interact with the sign.");
        msg(src, "I hope you found these tips helpful; otherwise, ping me on the forums/github/something!");
        msg(src, "Good luck, and have fun!");

        return CommandResult.success();
    }
}
