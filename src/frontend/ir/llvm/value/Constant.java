package frontend.ir.llvm.value;

import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.ir.llvm.value.type.ValueType;

public class Constant extends Value {

    public Constant(ScalarValueType scalarValueType, int objectValue) {
        super(scalarValueType, String.valueOf(scalarValueType == ScalarValueType.INT32 ? objectValue : objectValue % 256));
    }

    public static class Int extends Constant {
        public Int(int objectValue) {
            super(ScalarValueType.INT32, objectValue);
        }
    }

    public static class Char extends Constant {
        public Char(int objectValue) {
            super(ScalarValueType.INT8, objectValue);
        }
    }

    public static class Undefined extends Constant {
        public Undefined(ScalarValueType expectedType) {
            super(expectedType, 0);
        }
    }

    @Override
    public Value convertTo(ValueType expectedValueType) {
        if (valueType == expectedValueType) {
            return this;
        } else {
            return new Constant((ScalarValueType) expectedValueType, Integer.parseInt(name));
        }
    }
}
