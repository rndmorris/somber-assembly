package dev.rndmorris.somberassembly.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import dev.rndmorris.somberassembly.commands.actions.ActionForgetResearch;
import dev.rndmorris.somberassembly.commands.actions.ActionHelp;
import dev.rndmorris.somberassembly.commands.actions.ActionListPotions;
import dev.rndmorris.somberassembly.commands.actions.ActionResetScanned;
import dev.rndmorris.somberassembly.lib.CollectionUtil;

public class SomberAssemblyCommand extends CommandBase {

    private static final String COMMAND_NAME = "somberassembly";

    private final List<ICommandAction> actions;
    private final String commandUsage;

    public ICommandAction findAction(String actionName) {
        final var found = this.actions.stream()
            .filter(a -> a.matchesName(actionName))
            .limit(1)
            .collect(Collectors.toList());
        if (!found.isEmpty()) {
            return found.get(0);
        }
        return null;
    }

    public SomberAssemblyCommand() {
        this.actions = CollectionUtil.listOf(
            new ActionHelp(this),
            new ActionForgetResearch(this),
            new ActionForgetResearch(this),
            new ActionListPotions(this),
            new ActionResetScanned(this));
        this.commandUsage = String
            .format("Command usage: /%1s <%2s> [<arg1>, <arg2>, ...]", COMMAND_NAME, listActionNames(" | "));
    }

    public String listActionNames(String delimiter) {
        final var iter = Objects.requireNonNull(actions)
            .iterator();
        final var builder = new StringBuilder();
        while (iter.hasNext()) {
            final var action = iter.next();
            builder.append(action.getName());
            if (iter.hasNext()) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public List<String> getCommandAliases() {
        return CollectionUtil.listOf(COMMAND_NAME, "somber");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return commandUsage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, getCommandUsage(sender));
            return;
        }

        final var actionName = args[0];
        final var actionOption = findAction(actionName);
        if (actionOption == null) {
            sendMessage(
                sender,
                String.format(
                    "%1s Available actions: %2s",
                    CommandActionBase.unknownMessage(actionName),
                    listActionNames(", ")));
            return;
        }
        final var actionArgs = Arrays.asList(args)
            .subList(1, args.length);
        actionOption.execute(sender, actionArgs);
    }

    // Communication

    private static void sendMessage(ICommandSender sendTo, String... messages) {
        sendMessage(sendTo, Arrays.stream(messages));
    }

    private static void sendMessage(ICommandSender sendTo, Stream<String> messages) {
        messages.map(ChatComponentText::new)
            .forEach(sendTo::addChatMessage);
    }
}
