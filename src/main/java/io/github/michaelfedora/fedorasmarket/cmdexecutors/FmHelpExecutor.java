package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import com.google.common.collect.Lists;
import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.quickshop.FmQuickShopExecutor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Inspired by Polis (though heavily edited)
 */
public class FmHelpExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("help", "?");

    public static final String NAME = ALIASES.get(0);
    public static final String PERM = FmExecutor.PERM + '.' + ALIASES.get(0);

    public static final String DESC = "Get a list of subcommands";
    public static final String EX_DESC = "Get a list of subcommands; optional [cmd] is to display the extended description of the specified command";

    public static CommandSpec create() {
        return CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("cmd"))))
                .description(Text.of(DESC))
                .extendedDescription(Text.of(EX_DESC))
                .permission(PERM)
                .executor(new FmHelpExecutor())
                .build();
    }

    private static Optional<Map.Entry<List<String>, CommandSpec>> findCmd(HashMap<List<String>, CommandSpec> commands, String cmd) {
        for(Map.Entry<List<String>, CommandSpec> entry : commands.entrySet()) {
            if(entry.getKey().contains(cmd))
                return Optional.of(entry);
        }

        return Optional.empty();
    }

    public static void helpFunc(CommandSource src, CommandContext ctx, HashMap<List<String>,CommandSpec> commands, String parentCmd) throws CommandException {

        Optional<String> opt_cmd = ctx.<String>getOne("cmd");
        if(opt_cmd.isPresent()) {
            Optional<Map.Entry<List<String>, CommandSpec>> opt_cmdspec = findCmd(commands, opt_cmd.get());
            if(opt_cmdspec.isPresent()) {
                Map.Entry<List<String>, CommandSpec> cmdspec = opt_cmdspec.get();

                Text.Builder builder = Text.builder();
                builder.append(Text.of(cmdspec.getKey().toString(), ": ", TextColors.BLUE, cmdspec.getValue().getUsage(src)));

                Text t = Text.of(cmdspec.getValue().getHelp(src).orElse(cmdspec.getValue().getShortDescription(src).orElse(Text.EMPTY)));
                if(t != Text.EMPTY)
                    builder.append(t);

                src.sendMessage(builder.build());
                return;
            }
            throw new CommandException(Text.of("No such command: ", TextColors.BLUE, "[", opt_cmd.get(), "]", TextColors.RESET, "!"));
        }

        List<Text> helpList = Lists.newArrayList();
        String prefix = "/fm " + ((!parentCmd.equals("")) ? parentCmd + " " : "");
        for(List<String> aliases : commands.keySet()) {
            CommandSpec commandSpec = commands.get(aliases);
            Text usage = commandSpec.getUsage(src);
            if(!usage.equals(Text.EMPTY))
                usage = Text.of(": ", TextColors.BLUE, usage);
            else
                usage = Text.of(": ", TextColors.RED, "[ ]");
            Text commandHelp = Text.builder()
                    .append(Text.builder()
                            .onHover(TextActions.showText(commandSpec.getShortDescription(src).get()))
                            .onClick(TextActions.suggestCommand(prefix + aliases.get(0)))
                            .append(Text.of(TextColors.GREEN, TextStyles.BOLD, aliases.toString()))
                            .build())
                    .append(Text.of(usage))
                    .build();
            helpList.add(commandHelp);
        }

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        Text parentReferece = Text.EMPTY;
        if(!parentCmd.equals("")) {
            parentReferece = Text.of(TextColors.WHITE, " [" + parentCmd + "]");
        }
        PaginationList.Builder paginationBuilder = paginationService.builder().title(Text.of(TextColors.AQUA, PluginInfo.NAME, parentReferece, TextColors.AQUA, " Help")).padding(Text.of(TextColors.GOLD, "=")).contents(helpList);
        paginationBuilder.sendTo(src);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        helpFunc(src, ctx, FedorasMarket.subCommands, "");

        return CommandResult.success();
    }
}
