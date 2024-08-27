package dev.rndmorris.somberassembly.commands.actions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import dev.rndmorris.somberassembly.commands.CommandActionBase;
import dev.rndmorris.somberassembly.commands.SomberAssemblyCommand;
import dev.rndmorris.somberassembly.lib.CollectionUtil;
import joptsimple.internal.Strings;
import thaumcraft.common.Thaumcraft;

public class ActionListResearch extends CommandActionBase<SomberAssemblyCommand> {

    public ActionListResearch(SomberAssemblyCommand owningCommand) {
        super(owningCommand, "listResearch");
    }

    @Override
    public String getDescription() {
        return "List the named player's known research.";
    }

    @Override
    public String getArgumentsExample() {
        return "<player> [<containing>]";
    }

    @Override
    public List<String> getArgumentDescriptions() {
        return CollectionUtil.listOf(
            "- <player>: the username of the target player",
            "- <containing>: only return research containing this text (spaces are included).");
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public boolean execute(ICommandSender sender, List<String> args) {
        if (!super.execute(sender, args)) {
            return false;
        }
        final var playerName = args.get(0);
        final var player = findPlayer(sender, playerName);
        if (player == null) {
            return false;
        }

        final var searchText = collectSearchText(args, 2);
        listResearch(sender, player, searchText);
        return true;
    }

    private void listResearch(final ICommandSender sender, final EntityPlayerMP player, final String searchText) {
        final var playerName = player.getCommandSenderName();
        var research = Optional
            .ofNullable((List<String>) Thaumcraft.proxy.getPlayerKnowledge().researchCompleted.get(playerName))
            .orElse(Collections.emptyList())
            .stream();

        if (!Strings.isNullOrEmpty(searchText)) {
            final var search = searchText.toUpperCase();
            research = research.filter(
                key -> key.toUpperCase()
                    .contains(search));
        }
        sendList(sender, research);
    }
}
