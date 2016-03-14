package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Map;

/**
 * Get Version info
 */
public class FmExecutor extends FmExecutorBase {

    public static void listSubCommandsFunc(CommandSource src, Map<List<String>, CommandSpec> subCommands, String parentCmd) {
        Text.Builder tb = Text.builder();
        int i = 0;
        String prefix = "/fm " + ((!parentCmd.equals("")) ? parentCmd + " " : "");
        for(Map.Entry<List<String>, CommandSpec> entry : subCommands.entrySet()) {
            tb.append(Text.builder()
                    .onHover(TextActions.showText(entry.getValue().getShortDescription(src).get()))
                    .onClick(TextActions.suggestCommand(prefix + entry.getKey().get(0)))
                    .append(Text.of(TextColors.BLUE, entry.getKey()))
                    .build());
            if(++i < subCommands.entrySet().size()) {
                tb.append(Text.of(TextColors.GRAY, ", "));
            }
        }
        src.sendMessage(Text.of(TextStyles.BOLD, TextColors.GOLD, "[" + parentCmd + "]: ", TextStyles.RESET, tb.build()));
    }

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
