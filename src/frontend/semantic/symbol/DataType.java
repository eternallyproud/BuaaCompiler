package frontend.semantic.symbol;

import frontend.ir.llvm.value.type.ArrayValueType;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.ir.llvm.value.type.ValueType;

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
    private final String typeName;

    DataType(String typeName) {
        this.typeName = typeName;
    }

    //array -> non-array
    public DataType getReducedDataType() {
        return switch (this) {
            case INT_ARRAY -> INT;
            case CHAR_ARRAY -> CHAR;
            case CONST_INT_ARRAY -> CONST_INT;
            case CONST_CHAR_ARRAY -> CONST_CHAR;
            default -> UNEXPECTED;
        };
    }

    //non-array -> array
    public DataType getRaisedDataType() {
        return switch (this) {
            case INT -> INT_ARRAY;
            case CHAR -> CHAR_ARRAY;
            case CONST_INT -> CONST_INT_ARRAY;
            case CONST_CHAR -> CONST_CHAR_ARRAY;
            default -> UNEXPECTED;
        };
    }

    //non-const -> const
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

    //const -> non-const
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

    //char -> int
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

    public ValueType getValueType(int elementNumber) {
        return switch (this) {
            case CONST_INT, INT -> ScalarValueType.INT32;
            case CONST_CHAR, CHAR -> ScalarValueType.INT8;
            case CONST_INT_ARRAY, INT_ARRAY -> new ArrayValueType(ScalarValueType.INT32, elementNumber);
            case CONST_CHAR_ARRAY, CHAR_ARRAY -> new ArrayValueType(ScalarValueType.INT8, elementNumber);
            case VOID -> ScalarValueType.VOID;
            default -> null;
        };
    }

    public ValueType getElementType() {
        if (this.isArray()) {
            return switch (this) {
                case INT_ARRAY, CONST_INT_ARRAY -> ScalarValueType.INT32;
                case CHAR_ARRAY, CONST_CHAR_ARRAY -> ScalarValueType.INT8;
                default -> null;
            };
        }
        return null;
    }

    public boolean isArray() {
        return switch (this) {
            case INT_ARRAY, CHAR_ARRAY, CONST_INT_ARRAY, CONST_CHAR_ARRAY -> true;
            default -> false;
        };
    }

    public boolean isChar() {
        return switch (this) {
            case CHAR, CONST_CHAR -> true;
            default -> false;
        };
    }

    public boolean isConst() {
        return switch (this) {
            case CONST_INT, CONST_CHAR, CONST_INT_ARRAY, CONST_CHAR_ARRAY -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return typeName;
    }
}
