package frontend.ir.llvm.value;

import frontend.ir.llvm.value.type.ValueType;

public class Parameter extends Value {
    public Parameter(ValueType valueType, String name) {
        super(valueType, name);
    }

    @Override
    public String toString() {
        return valueType + " " + name;
    }
}
