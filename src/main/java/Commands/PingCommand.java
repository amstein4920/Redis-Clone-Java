package Commands;

public class PingCommand implements Command {

    @Override
    public String execute(String[] args) {
        return "+PONG\r\n";
    }

}
