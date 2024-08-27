package dev.rndmorris.somberassembly.lib;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.Utils;
import joptsimple.internal.Strings;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.research.ResearchManager;

public class SomberAssemblyCommand extends CommandBase {

    private static final String COMMAND_NAME = "somberassembly";
    private static final String SUBCOMMAND_HELP = "help";
    private static final String SUBCOMMAND_FORGET_RESEARCH = "forgetResearch";
    private static final String SUBCOMMAND_LIST_RESEARCH = "listResearch";

    private static final List<String> SUBCOMMAND_FORGET_RESEARCH_HELPTEXT = Utils.list(
        "Uncomplete the named research, and its siblings and descendants, for the named player.",
        "    /" + COMMAND_NAME + " " + SUBCOMMAND_FORGET_RESEARCH + " <player_name> <research_key>");

    private static final List<String> SUBCOMMAND_LIST_RESEARCH_HELPTEXT = Utils.list(
        "List the named player's known research.",
        "Providing <text> (spaces are included) will only return research containing that text.",
        "    /" + COMMAND_NAME + " " + SUBCOMMAND_LIST_RESEARCH + " <player_name> [<text>]");

    private static final String COMMAND_USAGE = "Command usage: /" + COMMAND_NAME
        + " <subcommand> [<arg1>, <arg2>, ...]";

    private static final List<String> COMMAND_HELP = Utils.append(
        Utils.list(
            COMMAND_USAGE,
            "Display this help text:",
            "    /" + COMMAND_NAME,
            "    /" + COMMAND_NAME + " " + SUBCOMMAND_HELP),
        SUBCOMMAND_FORGET_RESEARCH_HELPTEXT);

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    public List getCommandAliases() {
        return Utils.list(COMMAND_NAME, "somber");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return COMMAND_USAGE;
    }

    private void sendCommandHelp(ICommandSender sender) {
        COMMAND_HELP.stream()
            .map(ChatComponentText::new)
            .forEach(sender::addChatMessage);
    }

    public void sendSubcommandHelp(ICommandSender sender, String subcommand) {
        final var message = switch (subcommand) {
            case SUBCOMMAND_FORGET_RESEARCH -> SUBCOMMAND_FORGET_RESEARCH_HELPTEXT;
            case SUBCOMMAND_LIST_RESEARCH -> SUBCOMMAND_LIST_RESEARCH_HELPTEXT;
            default -> null;
        };
        if (message == null) {
            SomberAssembly.LOG.error("Unhandled subcommand ({}) was encountered.", subcommand);
            sender.addChatMessage(new ChatComponentText("An error has ocurred."));
            return;
        }
        Utils.append(Utils.list("Subcommand usage:"), message)
            .stream()
            .map(ChatComponentText::new)
            .forEach(sender::addChatMessage);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase(SUBCOMMAND_HELP)) {
            sendCommandHelp(sender);
            return;
        }
        if (args[0].equalsIgnoreCase(SUBCOMMAND_FORGET_RESEARCH)) {
            handleForgetResearch(sender, args);
            return;
        }
        if (args[0].equalsIgnoreCase(SUBCOMMAND_LIST_RESEARCH)) {
            handleListResearch(sender, args);
            return;
        }
        sendCommandHelp(sender);
    }

    private void handleForgetResearch(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            sendSubcommandHelp(sender, SUBCOMMAND_FORGET_RESEARCH);
            return;
        }
        final var playerName = args[1];
        EntityPlayerMP player = null;
        try {
            player = getPlayer(sender, playerName);
        } catch (PlayerNotFoundException ex) {
            sender.addChatMessage(new ChatComponentText("Player \"" + playerName + "\" not found."));
            return;
        }
        final var researchName = args[2];
        forgetResearch(sender, player, researchName);
    }

    private void forgetResearch(ICommandSender sender, EntityPlayerMP player, String research) {
        if (ResearchCategories.getResearch(research) != null) {
            this.forgetResearchAndDescendents(player, research);
            PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(player), player);
            player.addChatMessage(
                new ChatComponentText(
                    sender.getCommandSenderName() + " made you forget "
                        + research
                        + " and its descendants and siblings!"));
            sender.addChatMessage(new ChatComponentText("Success!"));
        } else {
            sender.addChatMessage(new ChatComponentText("Research " + research + " does not exist."));
        }
    }

    private void forgetResearchAndDescendents(EntityPlayerMP player, String research) {
        final var playerName = player.getCommandSenderName();
        if (!ResearchManager.isResearchComplete(playerName, research)) {
            return;
        }
        final var playerResearch = Thaumcraft.proxy.getPlayerKnowledge().researchCompleted.get(playerName);
        final var queue = new LinkedList<String>();
        final var visited = new HashSet<String>();
        queue.offer(research);
        visited.add(research);
        while (!queue.isEmpty()) {
            final var currentResearchKey = queue.poll();
            final var currentResearch = ResearchCategories.getResearch(currentResearchKey);
            if (!currentResearch.isAutoUnlock()) {
                playerResearch.remove(currentResearchKey);
            }
            playerResearch.stream()
                .map(ResearchCategories::getResearch)
                .filter(Objects::nonNull)
                .filter(
                    item -> !visited.contains(item.key) && ((item.siblings != null && Arrays.asList(item.siblings)
                        .contains(currentResearchKey)) || (item.parents != null
                            && Arrays.asList(item.parents)
                                .contains(currentResearchKey))
                        || (item.parentsHidden != null && Arrays.asList(item.parentsHidden)
                            .contains(currentResearchKey))))
                .forEach((item) -> {
                    queue.add(item.key);
                    visited.add(item.key);
                });
        }
    }

    private void handleListResearch(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sendSubcommandHelp(sender, SUBCOMMAND_LIST_RESEARCH);
            return;
        }
        final var playerName = args[1];
        EntityPlayerMP player = null;
        try {
            player = getPlayer(sender, playerName);
        } catch (PlayerNotFoundException ex) {
            sender.addChatMessage(new ChatComponentText("Player \"" + playerName + "\" not found."));
            return;
        }
        final var searchText = collectSearchText(args);
        listResearch(sender, player, searchText);
    }

    private String collectSearchText(String[] args) {
        if (args.length > 2) {
            final var subList = Arrays.asList(args)
                .subList(2, args.length);
            return Strings.join(subList, " ");
        }
        return null;
    }

    private void listResearch(ICommandSender sender, EntityPlayerMP player, String searchText) {
        final var playerName = player.getCommandSenderName();
        List<String> playerResearch = Thaumcraft.proxy.getPlayerKnowledge().researchCompleted.get(playerName);
        if (playerResearch == null || playerResearch.isEmpty()) {
            sender.addChatMessage(new ChatComponentText(playerName + " has no completed research."));
            return;
        }
        if (searchText != null && !searchText.isEmpty()) {
            playerResearch = Utils.streamToList(
                playerResearch.stream()
                    .filter(
                        key -> key.toUpperCase()
                            .contains(searchText)));
            if (playerResearch.isEmpty()) {
                sender.addChatMessage(
                    new ChatComponentText(playerName + " has no completed research for filter \"" + searchText + "\""));
                return;
            }
        }
        playerResearch.stream()
            .map(ChatComponentText::new)
            .forEach(sender::addChatMessage);
    }
}
