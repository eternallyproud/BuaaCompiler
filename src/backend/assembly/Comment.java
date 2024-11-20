package backend.assembly;

public class Comment extends Assembly {
    private final String comment;

    public Comment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "\n\t# " + comment;
    }
}
