package frontend.ir.value.type;

public class ScalarValueType extends ValueType {
    public static final ScalarValueType VOID = new ScalarValueType("void");
    public static final ScalarValueType INT1 = new ScalarValueType("i1");
    public static final ScalarValueType INT8 = new ScalarValueType("i8");
    public static final ScalarValueType INT32 = new ScalarValueType("i32");
    public static final ScalarValueType UNDEFINED = new ScalarValueType("wtf?");

    private final String name;

    private ScalarValueType(String name) {
        this.name = name;
    }

    public static ScalarValueType getByCType(String cType) {
        return switch (cType) {
            case "void" -> VOID;
            case "bool" -> INT1;
            case "char" -> INT8;
            case "int" -> INT32;
            default -> UNDEFINED;
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
