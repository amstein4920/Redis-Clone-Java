package Commands;

public class GetCommand implements Command {

    private DataStore store;

    public GetCommand(DataStore store) {
        this.store = store;
    }

    @Override
    public String execute(String[] args) {
        String entity = store.getEntity(args[0]);

        if (entity == null) {
            return "$-1\r\n";
        }
        return String.format("$%d\r\n%s\r\n", entity.length(), entity);
    }
}
