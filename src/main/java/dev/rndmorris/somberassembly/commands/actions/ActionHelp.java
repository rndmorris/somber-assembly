package dev.rndmorris.somberassembly.commands.actions;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import net.minecraft.command.ICommandSender;

import dev.rndmorris.somberassembly.commands.CommandActionBase;
import dev.rndmorris.somberassembly.commands.SomberAssemblyCommand;
import dev.rndmorris.somberassembly.lib.CollectionUtil;

public class ActionHelp extends CommandActionBase<SomberAssemblyCommand> {

    public ActionHelp(SomberAssemblyCommand owningCommand) {
        super(owningCommand, "help");
    }

    @Override
    public String getDescription() {
        return "Provides information about a subcommand";
    }

    @Override
    public String getArgumentsExample() {
        return "[<action>]";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public List<String> getArgumentDescriptions() {
        return CollectionUtil.listOf("<action>: the action to be described.");
    }

    @Override
    public boolean execute(ICommandSender sender, List<String> args) {
        if (!super.execute(sender, args)) {
            return false;
        }
        Objects.requireNonNull(sender);
        Objects.requireNonNull(args);

        if (args.isEmpty()) {
            sendMessage(sender, owningCommand.getCommandUsage(sender));
            return true;
        }
        final var lookupActionString = args.get(0);
        final var foundAction = owningCommand.findAction(lookupActionString);
        if (foundAction == null) {
            sendMessage(
                sender,
                CommandActionBase.unknownMessage(lookupActionString),
                String.format("Available actions are: %1s", owningCommand.listActionNames(", ")));
            return false;
        }
        sendMessage(
            sender,
            CollectionUtil.concat(
                Stream.of(foundAction.getDescription(), foundAction.getActionUsage()),
                foundAction.getArgumentDescriptions()
                    .stream()
                    .map(d -> "  - " + d)));
        return true;
    }
}
