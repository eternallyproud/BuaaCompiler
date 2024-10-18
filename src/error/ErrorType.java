package error;

public enum ErrorType {
    INVALID_TOKEN_ERROR('a'),
    REDECLARED_IDENT_ERROR('b'),
    UNDECLARED_IDENT_ERROR('c'),
    MISMATCHED_FUNCTION_PARAMETER_NUMBER_ERROR('d'),
    MISMATCHED_FUNCTION_PARAMETER_TYPE_ERROR('e'),
    MISMATCHED_RETURN_TYPE_ERROR('f'),
    MISSING_RETURN_ERROR('g'),
    UNMODIFIABLE_LVALUE('h'),
    MISSING_SEMICN_ERROR('i'),
    MISSING_RPARENT_ERROR('j'),
    MISSING_RBRACK_ERROR('k'),
    MISMATCHED_PRINTF_FORMAT_ERROR('l'),
    REDUNDANT_BREAK_CONTINUE_ERROR('m'),
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
