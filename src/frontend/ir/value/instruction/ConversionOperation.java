package frontend.ir.value.instruction;

import frontend.ir.value.Value;
import frontend.ir.value.type.ScalarValueType;
import frontend.ir.value.type.ValueType;

//<result> = <operator> <ty> <value> to <ty2>
public class ConversionOperation extends Instruction {
    private enum ConversionOperator {
        TRUNC, ZEXT, WTF;

        public static ConversionOperator getByValueType(ValueType from, ValueType to) {
            if (from == ScalarValueType.INT32 && to == ScalarValueType.INT8) {
                return TRUNC;
            } else if ((from == ScalarValueType.INT8 || from == ScalarValueType.INT1) && to == ScalarValueType.INT32) {
                return ZEXT;
            } else {
                return WTF;
            }
        }
    }

    private final ConversionOperator operator;

    public ConversionOperation(ValueType valueType, String name, Value operand) {
        super(valueType, name);
        addUsed(operand);
        operator = ConversionOperator.getByValueType(operand.getValueType(), valueType);
    }

    @Override
    public String toString() {
        return super.toString() + name + " = " + operator.toString().toLowerCase() + " "
                + getUsed(0).getValueType() + " " + getUsed(0).getName() + " to " + valueType;
    }
}
