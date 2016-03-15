package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.function.Supplier;

/**
 * Created by Michael on 2/29/2016.
 */
public abstract class FmExecutorBase implements CommandExecutor {

    protected abstract String getName();

    public void error(MessageReceiver src, String msg) {
        src.sendMessage(FmUtil.makeMessageError(getName(), msg));
    }

    public void warn(MessageReceiver src, String msg) {
        src.sendMessage(FmUtil.makeMessageWarn(getName(), msg));
    }

    public void exception(MessageReceiver src, String msg, Exception e) {

        src.sendMessage(FmUtil.makeMessageError(getName(), msg + ": See Console."));

        FedorasMarket.getLogger().error(msg, this, e);
    }

    public void msg(MessageReceiver src, String msg) {
        src.sendMessage(FmUtil.makeMessage(getName(), msg));
    }

    public void msgf(MessageReceiver src, Text text) {
        src.sendMessage(Text.of(FmUtil.makePrefix(), text));
    }

    public static void log(String msg, Object... objs) { FedorasMarket.getLogger().info(msg, objs); }
    public static void logError(String msg, Object... objs) { FedorasMarket.getLogger().error(msg, objs); }
    public static void logWarn(String msg, Object... objs) { FedorasMarket.getLogger().warn(msg, objs);}

    public void throwSafeException(String msg, Throwable cause, CommandSource src) {
        FedorasMarket.getLogger().error(msg + " by " + src + " with [" + getName() + "]", cause);
        error(src, msg + ": See Console.");
    }

    public CommandException makeException(String msg, Throwable cause, CommandSource src) {
        FedorasMarket.getLogger().error(msg + " by " + src + " with [" + getName() + "]", cause);
        return new CommandException(FmUtil.makeMessageError(getName(), msg), cause);
    }

    public CommandException makeException(String msg, Throwable cause) {
        FedorasMarket.getLogger().error(msg, cause);
        return new CommandException(FmUtil.makeMessageError(getName(), msg), cause);
    }

    public CommandException makeException(String msg, CommandSource src, boolean console) {
        if(console)
            FedorasMarket.getLogger().error(msg + " by " + src + " with [" + getName() + "]");
        return new CommandException(FmUtil.makeMessageError(getName(), msg));
    }

    public CommandException makeException(String msg, CommandSource src) {
        return makeException(msg, src, false);
    }

    public CommandException makeException(String msg) {
        return new CommandException(FmUtil.makeMessageError(getName(), msg));
    }

    public Supplier<CommandException> makeExceptionSupplier(String msg, Throwable cause, CommandSource src) {
        return () -> makeException(msg, cause, src);
    }

    public Supplier<CommandException> makeExceptionSupplier(String msg, Throwable cause) {
        return () -> makeException(msg, cause);
    }

    public Supplier<CommandException> makeExceptionSupplier(String msg, CommandSource src) {
        return () -> makeException(msg, src);
    }

    public Supplier<CommandException> makeExceptionSupplier(String msg) {
        return () -> makeException(msg);
    }

    public CommandException makeSourceNotPlayerException() {
        return makeException("Source not player! Sorry :<");
    }

    public Supplier<CommandException> makeParamExceptionSupplier(String key) {
        return makeExceptionSupplier("Bad param '" + key + "'");
    }
}
