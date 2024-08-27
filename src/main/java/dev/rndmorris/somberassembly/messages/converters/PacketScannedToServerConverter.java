package dev.rndmorris.somberassembly.messages.converters;

import java.lang.reflect.Field;

import thaumcraft.common.lib.network.playerdata.PacketScannedToServer;

public class PacketScannedToServerConverter {

    // Because using reflection to mine private fields is doubleplusgoodwise

    private static final Field playeridField;
    private static final Field dimField;
    private static final Field typeField;
    private static final Field idField;
    private static final Field mdField;
    private static final Field entityidField;
    private static final Field phenomenaField;
    private static final Field prefixField;

    static {
        try {
            playeridField = PacketScannedToServer.class.getDeclaredField("playerid");
            playeridField.setAccessible(true);
            dimField = PacketScannedToServer.class.getDeclaredField("dim");
            dimField.setAccessible(true);
            typeField = PacketScannedToServer.class.getDeclaredField("type");
            typeField.setAccessible(true);
            idField = PacketScannedToServer.class.getDeclaredField("id");
            idField.setAccessible(true);
            mdField = PacketScannedToServer.class.getDeclaredField("md");
            mdField.setAccessible(true);
            entityidField = PacketScannedToServer.class.getDeclaredField("entityid");
            entityidField.setAccessible(true);
            phenomenaField = PacketScannedToServer.class.getDeclaredField("phenomena");
            phenomenaField.setAccessible(true);
            prefixField = PacketScannedToServer.class.getDeclaredField("prefix");
            prefixField.setAccessible(true);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static dev.rndmorris.somberassembly.messages.PacketScannedToServer convert(PacketScannedToServer message) {
        var result = new dev.rndmorris.somberassembly.messages.PacketScannedToServer();
        try {
            result.playerid = playerid(message);
            result.dim = dim(message);
            result.type = type(message);
            result.id = id(message);
            result.md = md(message);
            result.entityid = entityid(message);
            result.phenomena = phenomena(message);
            result.prefix = prefix(message);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static int playerid(PacketScannedToServer message) throws IllegalAccessException {
        return (int) playeridField.get(message);
    }

    public static int dim(PacketScannedToServer message) throws IllegalAccessException {
        return (int) dimField.get(message);
    }

    public static byte type(PacketScannedToServer message) throws IllegalAccessException {
        return (byte) typeField.get(message);
    }

    public static int id(PacketScannedToServer message) throws IllegalAccessException {
        return (int) idField.get(message);
    }

    public static int md(PacketScannedToServer message) throws IllegalAccessException {
        return (int) mdField.get(message);
    }

    public static int entityid(PacketScannedToServer message) throws IllegalAccessException {
        return (int) entityidField.get(message);
    }

    public static String phenomena(PacketScannedToServer message) throws IllegalAccessException {
        return (String) phenomenaField.get(message);
    }

    public static String prefix(PacketScannedToServer message) throws IllegalAccessException {
        return (String) prefixField.get(message);
    }

}
