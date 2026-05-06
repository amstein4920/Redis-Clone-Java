package Commands;

import Utils.RESP;

public class EchoCommand implements Command {

    @Override
    public String execute(String[] args) {
        return RESP.bulkString(args[0]);
    }

}