package frontend.ir.llvm.value;

public class Use {
    private final User user;
    private final Value used;

    public Use(User user, Value used) {
        this.user = user;
        this.used = used;
    }

    public User getUser() {
        return user;
    }

    public Value getUsed() {
        return used;
    }

    @Override
    public String toString() {
        return "" + user + used;
    }
}
