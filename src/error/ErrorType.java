package error;

public enum ErrorType {
    INVALID_TOKEN_ERROR('a'),
    MISSING_SEMICN_ERROR('i'),
    MISSING_RPARENT_ERROR('j'),
    MISSING_RBRACK_ERROR('k'),
    UNEXPECTED_ERROR('u'),
    ;

    public final char errorCode;

    ErrorType(char errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return String.valueOf(errorCode);
    }
}
