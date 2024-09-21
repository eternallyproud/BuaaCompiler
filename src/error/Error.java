package error;

public class Error {
    private final int line;
    private final ErrorType errorType;

    public Error(int line, ErrorType errorType) {
        this.line = line;
        this.errorType = errorType;
    }

    public int getLine() {
        return line;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return line + " " + errorType;
    }
}
