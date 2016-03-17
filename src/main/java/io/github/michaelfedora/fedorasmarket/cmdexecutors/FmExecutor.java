package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Get Version info
 */
public class FmExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("fedorasmarket", "fm");
    public static final String PERM = PluginInfo.DATA_ROOT;

    public static CommandSpec create(Map<List<String>, ? extends CommandCallable> children) {
        return CommandSpec.builder()
                .description(Text.of(PluginInfo.NAME + " base command (displays plugin info)"))
                .permission(PERM + ".use")
                .children(children)
                .executor(new FmExecutor())
                .build();
    }

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
    public String getName() {
        return ALIASES.get(0);
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {
        src.sendMessage(Text.of(TextColors.AQUA, PluginInfo.NAME, " ", TextColors.GRAY, "Version: ",
                TextColors.GOLD, FedorasMarket.getGame().getPluginManager().getPlugin(PluginInfo.ID).get().getVersion().get()));
        return CommandResult.success();
    }
}
