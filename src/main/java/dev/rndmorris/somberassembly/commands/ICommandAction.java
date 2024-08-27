package dev.rndmorris.somberassembly.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;

public interface ICommandAction {

    String getName();

    String getDescription();

    String getArgumentsExample();

    List<String> getArgumentDescriptions();

    int getMinimumArguments();

    boolean execute(ICommandSender sender, List<String> args);

    boolean matchesName(String query);

    String getActionUsage();

    List<String> addTabCompletionOptions(ICommandSender sender, String[] args);
}
