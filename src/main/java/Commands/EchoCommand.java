package Commands;

public class EchoCommand implements Command {

    @Override
    public String execute(String[] args) {
        return String.format("$%d\r\n%s\r\n", args[0].length(), args[0]);
    }

}