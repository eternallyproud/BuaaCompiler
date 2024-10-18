package frontend.symbol;

public enum DataType {
    CONST_CHAR("ConstChar"),
    CONST_INT("ConstInt"),
    CONST_CHAR_ARRAY("ConstCharArray"),
    CONST_INT_ARRAY("ConstIntArray"),
    CHAR("Char"),
    INT("Int"),
    CHAR_ARRAY("CharArray"),
    INT_ARRAY("IntArray"),
    VOID_FUNC("VoidFunc"),
    INT_FUNC("IntFunc"),
    CHAR_FUNC("CharFunc"),
    VOID("Void"),
    UNEXPECTED("Unexpected");
    public final String typeName;

    DataType(String typeName) {
        this.typeName = typeName;
    }

    public DataType getReducedDataType() {
        return switch (this) {
            case INT_ARRAY -> INT;
            case CHAR_ARRAY -> CHAR;
            case CONST_INT_ARRAY -> CONST_INT;
            case CONST_CHAR_ARRAY -> CONST_CHAR;
            default -> UNEXPECTED;
        };
    }

    public DataType getRaisedDataType() {
        return switch (this) {
            case INT -> INT_ARRAY;
            case CHAR -> CHAR_ARRAY;
            case CONST_INT -> CONST_INT_ARRAY;
            case CONST_CHAR -> CONST_CHAR_ARRAY;
            default -> UNEXPECTED;
        };
    }

    public DataType getConstantDataType() {
        return switch (this) {
            case INT -> CONST_INT;
            case CHAR -> CONST_CHAR;
            case INT_ARRAY -> CONST_INT_ARRAY;
            case CHAR_ARRAY -> CONST_CHAR_ARRAY;
            case CONST_INT, CONST_CHAR, CONST_INT_ARRAY, CONST_CHAR_ARRAY -> this;
            default -> UNEXPECTED;
        };
    }

    public DataType getNonConstantDataType() {
        return switch (this) {
            case CONST_INT -> INT;
            case CONST_CHAR -> CHAR;
            case CONST_INT_ARRAY -> INT_ARRAY;
            case CONST_CHAR_ARRAY -> CHAR_ARRAY;
            case INT, CHAR, INT_ARRAY, CHAR_ARRAY -> this;
            default -> UNEXPECTED;
        };
    }

    public DataType getCharToInt() {
        return switch (this) {
            case CHAR -> INT;
            case CONST_CHAR -> CONST_INT;
            default -> this;
        };
    }

    public boolean canBeAssignedTo() {
        return switch (this) {
            case INT, CHAR -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return typeName;
    }
}
