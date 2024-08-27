package dev.rndmorris.somberassembly.messages;

public class PacketScannedToServer {

    public int playerid;
    public int dim;
    public byte type;
    public int id;
    public int md;
    public int entityid;
    public String phenomena;
    public String prefix;

    @Override
    public String toString() {
        return "{ " + String.join(
            ", ",
            new String[] { "playerid: " + playerid, "dim: " + dim, "type: " + type, "id: " + id, "md: " + md,
                "entityid: " + entityid, "phenomena: " + (phenomena == null ? "null" : "\"" + phenomena + "\""),
                "prefix: " + (prefix == null ? "null" : "\"" + prefix + "\""), })
            + " }";
    }
}
