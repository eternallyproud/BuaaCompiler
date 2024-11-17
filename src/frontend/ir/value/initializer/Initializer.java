package frontend.ir.value.initializer;

import frontend.ir.value.type.ArrayValueType;
import frontend.ir.value.type.ScalarValueType;
import frontend.ir.value.type.ValueType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Initializer {
    private final int elementNumber;
    private final ArrayList<Integer> values;
    private final ValueType valueType;

    public Initializer(int elementNumber, ValueType valueType, ArrayList<Integer> values) {
        this.elementNumber = elementNumber;
        this.valueType = valueType;
        this.values = values;
        convert();
    }

    private void convert() {
        if (values != null) {
            //char array
            if (valueType instanceof ArrayValueType arrayValueType) {
                if (arrayValueType.getElementType() == ScalarValueType.INT8) {
                    values.replaceAll(integer -> integer % 256);
                }
            }
            //char
            else if (valueType == ScalarValueType.INT8) {
                values.replaceAll(integer -> integer % 256);
            }
        }
    }

    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(valueType).append(" ");
        if (values == null) {
            //array
            if (valueType instanceof ArrayValueType) {
                sb.append("zeroinitializer");
            }
            //non-array
            else {
                sb.append("0");
            }
        } else {
            //array
            if (valueType instanceof ArrayValueType arrayValueType) {
                String s1 = values.stream().map(number -> arrayValueType.getElementType() + " " + number).collect(Collectors.joining(", "));
                String s2 = String.join(", ", java.util.Collections.nCopies(elementNumber - values.size(), arrayValueType.getElementType() + " 0"));
                String s0 = (s1.isEmpty() || s2.isEmpty()) ? "" : ", ";

                sb.append("[").append(s1).append(s0).append(s2).append("]");
            }
            //non-array
            else {
                sb.append(values.get(0));
            }
        }
        return sb.toString();
    }
}
