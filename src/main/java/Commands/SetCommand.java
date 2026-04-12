package Commands;

public class SetCommand implements Command {
    private DataStore store;

    public SetCommand(DataStore store) {
        this.store = store;
    }

    @Override
    public String execute(String[] args) {
        store.setEntity(args[0], args[1]);
        return "+OK\r\n";
    }

}
