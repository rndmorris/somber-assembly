package dev.rndmorris.somberassembly.commands.actions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import dev.rndmorris.somberassembly.commands.CommandActionBase;
import dev.rndmorris.somberassembly.commands.SomberAssemblyCommand;
import dev.rndmorris.somberassembly.lib.CollectionUtil;
import joptsimple.internal.Strings;
import thaumcraft.common.Thaumcraft;

public class ActionResetScanned extends CommandActionBase<SomberAssemblyCommand> {

    public ActionResetScanned(SomberAssemblyCommand owningCommand) {
        super(owningCommand, "resetScanned");
    }

    @Override
    public String getDescription() {
        return "Reset the named player's list of scanned things, allowing them to scan them again.";
    }

    @Override
    public String getArgumentsExample() {
        return "<player> <scanType>";
    }

    @Override
    public List<String> getArgumentDescriptions() {
        return CollectionUtil
            .listOf("<player>: the username of the target player", "<scanType>: the list of things to reset");
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
        final var findScanType = args.get(1);
        final var scanType = ScanType.fromStringIgnoreCase(findScanType);
        if (scanType == null) {
            sendMessage(sender, ScanType.unknown(findScanType));
            return false;
        }
        resetScanned(sender, player, scanType);
        return true;
    }

    private void resetScanned(ICommandSender sender, EntityPlayerMP player, ScanType scanType) {
        final var playerName = player.getCommandSenderName();
        final var playerKnowledge = Thaumcraft.proxy.getPlayerKnowledge();

        final var resetMaps = switch (scanType) {
            case objects -> Stream.of(playerKnowledge.objectsScanned);
            case entities -> Stream.of(playerKnowledge.entitiesScanned);
            case phenomenon -> Stream.of(playerKnowledge.phenomenaScanned);
            case all -> Stream
                .of(playerKnowledge.objectsScanned, playerKnowledge.entitiesScanned, playerKnowledge.phenomenaScanned);
        };

        final var removed = resetMaps.map(map -> map.get(playerName))
            .map(scanList -> {
                if (scanList != null) {
                    final var size = scanList.size();
                    scanList.removeIf(i -> true);
                    return size;
                }
                return 0;
            })
            .mapToInt(Integer::intValue)
            .sum();

        final var scanTypeString = switch (scanType) {
            case all -> "objects, entities, and phenomenon";
            default -> scanType.toString();
        };

        sendMessage(
            sender,
            String.format("Success! Unscanned %1s scanned %2s for %3s.", removed, scanTypeString, playerName));
        sendMessage(
            player,
            String.format(
                "%1s removed all %2s of your scanned %3s.",
                sender.getCommandSenderName(),
                removed,
                scanTypeString));
    }

    private enum ScanType {

        objects,
        entities,
        phenomenon,
        all;

        public static String unknown(String type) {
            return String.format("Unknown scanType \"%1s\".", type);
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
}
