package frontend.ir.value;

import frontend.ir.value.type.ValueType;

public class Parameter extends Value {
    public Parameter(ValueType valueType, String name) {
        super(valueType, name);
    }

    @Override
    public String toString() {
        return valueType + " " + name;
    }
}
