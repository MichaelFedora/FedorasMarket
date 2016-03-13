package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import com.google.common.collect.Lists;
import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Michael on 2/29/2016.
 */
public class FmShopHelpExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "shop help";
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        HashMap<List<String>,CommandSpec> transSubCommands = FedorasMarket.getGrandChildCommands("shop").orElseThrow(makeExceptionSupplier("Can't find the subcommands :o"));

        List<Text> helpList = Lists.newArrayList();

        for(List<String> aliases : transSubCommands.keySet()) {
            CommandSpec commandSpec = transSubCommands.get(aliases);
            Text commandHelp = Text.builder()
                    .append(Text.builder()
                            .append(Text.of(TextColors.GREEN, aliases, ": "))
                            .append(Text.of(TextColors.BLUE,
                                    commandSpec.getUsage(src), "\n"))
                            .append(Text.of(TextColors.WHITE, "    ",
                                    commandSpec.getShortDescription(src).get(), "\n"))
                            /*.append(Text.of(TextColors.AQUA, "Perm: ",
                                    (commandSpec.testPermission(src)) ? TextColors.GREEN : TextColors.RED,
                                    commandSpec.toString().substring(commandSpec.toString().lastIndexOf("permission") + 11,
                                            commandSpec.toString().indexOf("argumentParser") - 2)))*/
                            .build())
                    .build();
            helpList.add(commandHelp);
        }

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder paginationBuilder = paginationService.builder().title(Text.of(TextColors.GOLD, PluginInfo.NAME + " Help")).padding(Text.of("=")).contents(helpList);
        paginationBuilder.sendTo(src);
        return CommandResult.success();
    }
}
