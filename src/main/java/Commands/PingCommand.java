package Commands;

import Utils.RESP;

public class PingCommand implements Command {

    @Override
    public String execute(String[] args) {
        return RESP.simpleString("PONG");
    }

}
