package dev.rndmorris.somberassembly.commands.actions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import dev.rndmorris.somberassembly.commands.CommandActionBase;
import dev.rndmorris.somberassembly.commands.SomberAssemblyCommand;
import dev.rndmorris.somberassembly.data.Tuple2;
import dev.rndmorris.somberassembly.lib.CollectionUtil;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.research.ResearchManager;

public class ActionForgetResearch extends CommandActionBase<SomberAssemblyCommand> {

    public ActionForgetResearch(SomberAssemblyCommand owningCommand) {
        super(owningCommand, "forgetResearch");
    }

    @Override
    public String getDescription() {
        return "Uncomplete the named research, and all research derived from it, for the named player. This may break things.";
    }

    @Override
    public String getArgumentsExample() {
        return "<player> <research>";
    }

    @Override
    public List<String> getArgumentDescriptions() {
        return CollectionUtil
            .listOf("<player>: the username of the target player", "<research>: the key of the research to forget");
    }

    @Override
    public int getMinimumArguments() {
        return 2;
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
        final var researchName = args.get(1);
        forgetResearch(sender, player, researchName);
        return true;
    }

    private void forgetResearch(ICommandSender sender, EntityPlayerMP player, String research) {
        if (ResearchCategories.getResearch(research) != null) {
            this.forgetResearchAndDescendents(player, research);
            PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(player), player);
            sendMessage(
                player,
                String.format(
                    "%1s made you forget %2s and research related to it!",
                    sender.getCommandSenderName(),
                    research));
            sendMessage(sender, "Success!");
        } else {
            sendMessage(sender, String.format("Research %1s does not exist", research));
        }
    }

    private void forgetResearchAndDescendents(EntityPlayerMP player, String research) {
        final var playerName = player.getCommandSenderName();
        if (!ResearchManager.isResearchComplete(playerName, research)) {
            return;
        }
        // If we managed to pass `isResearchComplete()`, the player should have at least an empty list
        final List<String> playerResearch = Thaumcraft.proxy.getPlayerKnowledge().researchCompleted.get(playerName);
        final var researchData = playerResearch.stream()
            .filter(Objects::nonNull)
            .map(key -> new Tuple2<>(key, ResearchCategories.getResearch(key)))
            .filter(tuple -> tuple.value2() != null)
            .collect(Collectors.toMap(Tuple2::value1, Tuple2::value2));

        final var queue = new LinkedList<String>();
        final var visited = new HashSet<String>();

        queue.offer(research);
        visited.add(research);

        while (!queue.isEmpty()) {
            final var currentKey = queue.poll();
            final var currentData = researchData.get(currentKey);

            if (!currentData.isAutoUnlock()) {
                final var foundIndex = CollectionUtil.indexOf(playerResearch, currentKey::equalsIgnoreCase);
                if (foundIndex >= 0) {
                    playerResearch.remove(foundIndex);
                }
            }

            researchData.values()
                .stream()
                .filter(
                    item -> !visited.contains(item.key) && (CollectionUtil.containsIgnoreCase(item.siblings, currentKey)
                        || CollectionUtil.containsIgnoreCase(item.parents, currentKey)
                        || CollectionUtil.containsIgnoreCase(item.parentsHidden, currentKey)))
                .forEach(item -> {
                    queue.add(item.key);
                    visited.add(item.key);
                });
        }
    }
}
