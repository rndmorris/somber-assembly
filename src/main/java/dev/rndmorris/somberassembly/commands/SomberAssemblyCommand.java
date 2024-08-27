package dev.rndmorris.somberassembly.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import dev.rndmorris.somberassembly.data.Tuple2;
import dev.rndmorris.somberassembly.lib.CollectionUtil;
import joptsimple.internal.Strings;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.research.ResearchManager;

public class SomberAssemblyCommand extends CommandBase {

    private static final String COMMAND_NAME = "somberassembly";

    private enum Action {

        help,
        forgetResearch,
        listResearch,
        listScanned,
        resetScanned;

        private static final List<String> ARGS_HELP = CollectionUtil.listOf("- <action>: the action to be described. ");

        private static final List<String> ARGS_FORGET_RESEARCH = CollectionUtil
            .listOf("- <player>: the username of the target player", "- <research>: the key of the research to forget");

        private static final List<String> ARGS_LIST_RESEARCH = CollectionUtil.listOf(
            "- <player>: the username of the target player",
            "- <containing>: only return research containing this text (spaces are included).");

        private static final List<String> ARGS_LIST_SCANNED = CollectionUtil.listOf(
            "- <player>: the username of the target player",
            String.format("- <type>: %1s", ScanType.getScanTypeList(", ")),
            "- <containing>: only return things containing this text (spaces are included).");

        private static final List<String> ARGS_RESET_SCANNED = CollectionUtil.listOf(
            "- <player>: the username of the target player",
            String.format("- <type>: %1s", ScanType.getScanTypeList(", ")),
            "- <object>: the name of the thing to unscan");

        public static String getActionList(String delimiter) {
            return Strings.join(
                Arrays.stream(Action.values())
                    .map(Enum::toString)
                    .collect(Collectors.toList()),
                delimiter);
        }

        public static String listAvailableActions() {
            return String.format("Available actions are: %1s", getActionList(", "));
        }

        public String getDescription() {
            return switch (this) {
                case help -> "Provides information about a subcommand";
                case forgetResearch -> "Uncomplete the named research, and its siblings and descendants, for the named player.";
                case listResearch -> "List the named player's known research.";
                case listScanned -> "List the named player's scanned objects, entities, or phenomenon.";
                case resetScanned -> "Remove all scanned objects, entities, or phenomenon for the named player.";
            };
        }

        public String getUsage() {
            final var argString = switch (this) {
                case help -> "[<action>]";
                case forgetResearch -> "<player> <research>";
                case listResearch -> "<player> [<containing>]";
                case listScanned -> "<player> <type> [<containing>]";
                case resetScanned -> "<player> <type>";
            };
            return String.format("/%1s %2s %3s", COMMAND_NAME, this, argString);
        }

        public List<String> getArgumentDescriptions() {
            return switch (this) {
                case help -> ARGS_HELP;
                case forgetResearch -> ARGS_FORGET_RESEARCH;
                case listResearch -> ARGS_LIST_RESEARCH;
                case listScanned -> ARGS_LIST_SCANNED;
                case resetScanned -> ARGS_RESET_SCANNED;
            };
        }

        public static Action fromStringIgnoreCase(String string) {
            for (var action : Action.values()) {
                if (action.toString()
                    .equalsIgnoreCase(string)) {
                    return action;
                }
            }
            return null;
        }

        public static String unknown(String action) {
            return String.format("Unknown action \"%1s\".", action);
        }

        public int minimumArguments() {
            return switch (this) {
                case help -> 1;
                case listResearch -> 2;
                case forgetResearch, listScanned, resetScanned -> 3;
            };
        }
    }

    private interface ActionHandler {

        void handle(ICommandSender sender, String[] args);
    }

    private static final String COMMAND_USAGE = String
        .format("Command usage: /%1s <%2s> [<arg1>, <arg2>, ...]", COMMAND_NAME, Action.getActionList(" | "));

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
        return COMMAND_USAGE;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }
        final var action = Action.fromStringIgnoreCase(args[0]);
        if (action == null) {
            sendMessage(sender, Action.unknown(args[0]));
            return;
        }
        if (args.length < action.minimumArguments()) {
            sendUsage(sender, action);
            return;
        }
        ActionHandler handler = switch (action) {
            case help -> this::handleHelp;
            case forgetResearch -> this::handleForgetResearch;
            case listResearch -> this::handleListResearch;
            case listScanned -> this::handleListScanned;
            case resetScanned -> this::handleResetScanned;
        };
        handler.handle(sender, args);
    }

    // Parse command action parameters, pass to methods that actually do the things

    private void handleHelp(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, Action.listAvailableActions());
            return;
        }
        final var lookupAction = Action.fromStringIgnoreCase(args[1]);
        if (lookupAction == null) {
            sendMessage(sender, Action.unknown(args[1]), Action.listAvailableActions());
            return;
        }
        sendMessage(
            sender,
            CollectionUtil.concat(
                Stream.of(lookupAction.getDescription(), lookupAction.getUsage()),
                lookupAction.getArgumentDescriptions()
                    .stream()));
    }

    private void handleForgetResearch(ICommandSender sender, String[] args) {
        final var playerName = args[1];
        final var player = findPlayer(sender, playerName);
        final var researchName = args[2];
        forgetResearch(sender, player, researchName);
    }

    private void handleListResearch(ICommandSender sender, String[] args) {
        final var playerName = args[1];
        final var player = findPlayer(sender, playerName);
        if (player == null) {
            return;
        }

        final var searchText = collectSearchText(args, 2);
        listResearch(sender, player, searchText);
    }

    private void handleListScanned(ICommandSender sender, String[] args) {
        final var playerName = args[1];
        final var player = findPlayer(sender, playerName);
        if (player == null) {
            return;
        }
        final var scanType = ScanType.fromStringIgnoreCase(args[2]);
        if (scanType == null) {
            sendMessage(sender, ScanType.unknown(args[2]));
            return;
        }
        final var searchText = collectSearchText(args, 3);
        listScanned(sender, player, scanType, searchText);
    }

    private void handleResetScanned(ICommandSender sender, String[] args) {
        final var playerName = args[1];
        final var player = findPlayer(sender, playerName);
        if (player == null) {
            return;
        }
        final var scanType = ScanType.fromStringIgnoreCase(args[2]);
        if (scanType == null) {
            sendMessage(sender, ScanType.unknown(args[2]));
            return;
        }
        resetScanned(sender, player, scanType);
    }

    // Utility
    private EntityPlayerMP findPlayer(@Nonnull ICommandSender sender, @Nonnull String playerName) {
        try {
            return getPlayer(sender, playerName);
        } catch (PlayerNotFoundException ex) {
            sendMessage(sender, String.format("Player %1s not found.", playerName));
            return null;
        }
    }

    // Communication

    private static void sendMessage(ICommandSender sendTo, String... messages) {
        sendMessage(sendTo, Arrays.stream(messages));
    }

    private static void sendMessage(ICommandSender sendTo, Stream<String> messages) {
        messages.map(ChatComponentText::new)
            .forEach(sendTo::addChatMessage);
    }

    private static void sendList(ICommandSender sendTo, Stream<String> list) {
        sendMessage(
            sendTo,
            CollectionUtil.concat(Stream.of("----Start of List----"), list, Stream.of("---- End of List ----")));
    }

    private void sendUsage(ICommandSender sender) {
        sendMessage(sender, Stream.of(COMMAND_USAGE, Action.listAvailableActions()));
    }

    private void sendUsage(ICommandSender sender, Action action) {
        sendMessage(sender, action.getUsage());
    }

    // Performing actual work

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

    private static String collectSearchText(String[] args, int afterIndex) {
        if (args.length > afterIndex) {
            final var subList = Arrays.asList(args)
                .subList(afterIndex, args.length);
            return Strings.join(subList, " ");
        }
        return null;
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

    private enum ScanType {

        objects,
        entities,
        phenomenon;

        public static String unknown(String type) {
            return String.format("Unrecognized scanType \"%1s\".", type);
        }

        public static String getScanTypeList(String delimiter) {
            return Strings.join(
                Arrays.stream(ScanType.values())
                    .map(Enum::toString)
                    .collect(Collectors.toList()),
                delimiter);
        }

        public static ScanType fromStringIgnoreCase(String string) {
            for (var type : ScanType.values()) {
                if (type.toString()
                    .equalsIgnoreCase(string)) {
                    return type;
                }
            }
            return null;
        }
    }

    private void listScanned(ICommandSender sender, EntityPlayerMP player, ScanType type, String searchText) {
        final var playerName = player.getCommandSenderName();
        final var playerKnowledge = Thaumcraft.proxy.getPlayerKnowledge();

        var scanList = Optional.ofNullable((List<String>) (switch (type) {
            case objects -> playerKnowledge.objectsScanned;
            case entities -> playerKnowledge.entitiesScanned;
            case phenomenon -> playerKnowledge.phenomenaScanned;
        }).get(playerName))
            .orElse(Collections.emptyList())
            .stream();

        if (!Strings.isNullOrEmpty(searchText)) {
            final var search = searchText.toUpperCase();
            scanList = scanList.filter(
                key -> key.toUpperCase()
                    .contains(search));
        }

        sendList(sender, scanList);
    }

    private void resetScanned(ICommandSender sender, EntityPlayerMP player, ScanType scanType) {
        final var playerName = player.getCommandSenderName();
        final var playerKnowledge = Thaumcraft.proxy.getPlayerKnowledge();

        var scanList = (switch (scanType) {
            case objects -> playerKnowledge.objectsScanned;
            case entities -> playerKnowledge.entitiesScanned;
            case phenomenon -> playerKnowledge.phenomenaScanned;
        }).get(playerName);

        final var removed = scanList != null ? scanList.size() : 0;
        if (scanList != null) {
            CollectionUtil.empty(scanList);
        }
        sendMessage(
            sender,
            String.format("Success! Unscanned %1s scanned %2s for %3s.", removed, scanType, playerName));
        sendMessage(
            player,
            String
                .format("%1s removed all %2s of your scanned %3s.", sender.getCommandSenderName(), removed, scanType));
    }
}
