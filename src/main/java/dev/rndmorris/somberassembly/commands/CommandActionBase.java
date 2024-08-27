package dev.rndmorris.somberassembly.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import dev.rndmorris.somberassembly.lib.CollectionUtil;
import joptsimple.internal.Strings;

public abstract class CommandActionBase<C extends CommandBase> implements ICommandAction {

    public static String unknownMessage(String unknownAction) {
        return String.format("Unknown action \"%1s\".", unknownAction);
    }

    protected static void sendMessage(ICommandSender sendTo, String... messages) {
        sendMessage(sendTo, Arrays.stream(messages));
    }

    protected static void sendMessage(ICommandSender sendTo, Stream<String> messages) {
        messages.map(ChatComponentText::new)
            .forEach(sendTo::addChatMessage);
    }

    protected static void sendList(ICommandSender sendTo, Stream<String> list) {
        sendMessage(
            sendTo,
            CollectionUtil.concat(Stream.of("----Start of List----"), list, Stream.of("---- End of List ----")));
    }

    protected final C owningCommand;
    protected final String name;

    public CommandActionBase(C owningCommand, String name) {
        this.owningCommand = Objects.requireNonNull(owningCommand);
        this.name = Objects.requireNonNull(name);
    }

    public String getName() {
        return this.name;
    }

    public abstract String getDescription();

    public abstract String getArgumentsExample();

    public abstract List<String> getArgumentDescriptions();

    public abstract int getMinimumArguments();

    public boolean execute(ICommandSender sender, List<String> args) {
        final var minArgs = this.getMinimumArguments();
        if (args.size() < minArgs) {
            sendMessage(
                sender,
                String.format("%1s requires at least %2s arguments.", getName(), minArgs),
                getActionUsage());
            return false;
        }
        return true;
    }

    public boolean matchesName(String query) {
        return this.getName()
            .equalsIgnoreCase(query);
    }

    public String getActionUsage() {
        return String
            .format("Action usage: /%1s %2s %3s", owningCommand.getCommandName(), getName(), getArgumentsExample());
    }

    protected EntityPlayerMP findPlayer(@Nonnull ICommandSender sender, @Nonnull String playerName) {
        try {
            return CommandBase.getPlayer(sender, playerName);
        } catch (PlayerNotFoundException ex) {
            sendMessage(sender, String.format("Player %1s not found.", playerName));
            return null;
        }
    }

    /**
     * Combine all arguments after startIndex into a single space-separated string
     * 
     * @param args       The action arguments
     * @param startIndex The index from which to start collecting search text.
     * @return The search text if any exists, otherwise null.
     */
    protected static String collectSearchText(List<String> args, int startIndex) {
        if (args.size() >= startIndex) {
            final var subList = args.subList(startIndex, args.size());
            return Strings.join(subList, " ");
        }
        return null;
    }
}
