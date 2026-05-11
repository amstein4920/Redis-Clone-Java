package Commands;

import Configuration.ServerData;
import Utils.RESP;

public class InfoCommand implements Command {
    private ServerData serverData;

    public InfoCommand(ServerData serverData) {
        this.serverData = serverData;
    }

    @Override
    public String execute(String[] args) {
        if (args[0] != null && "replication".equals(args[0].toLowerCase())) {
            return RESP.bulkString(getReplicationBlock());
        }
        return "";
    }

    private String getReplicationBlock() {
        // Will be added to over time
        StringBuilder builder = new StringBuilder("# Replication\r\n");
        builder.append("role:" + serverData.getEntity("role") + "\r\n");

        return builder.toString();
    }
}
