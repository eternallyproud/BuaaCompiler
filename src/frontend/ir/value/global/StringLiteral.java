package frontend.ir.value.global;

import frontend.ir.value.type.ArrayValueType;
import frontend.ir.value.type.PointerValueType;
import frontend.ir.value.type.ScalarValueType;
import utils.Tools;

public class StringLiteral extends GlobalValue {
    private final String objectValue;

    public StringLiteral(String name, String objectValue) {
        super(new ArrayValueType(ScalarValueType.INT8, getObjectSize(objectValue)), name);
        this.objectValue = objectValue;
    }

    public PointerValueType evaluate() {
        return new PointerValueType(valueType);
    }

    private static int getObjectSize(String str) {
        return str.length()
                - 2 * Tools.findSubstringOccurrences(str, "\\0A")
                - 2 * Tools.findSubstringOccurrences(str, "\\00");
    }

    @Override
    public String toString() {
        return name + " = constant " + valueType + " c\"" + objectValue + "\"";
    }
}
