package frontend.ir.value.instruction;

import frontend.ir.value.User;
import frontend.ir.value.type.ValueType;

public class Instruction extends User {
    public Instruction(ValueType valueType, String name) {
        super(valueType, name);
    }

    @Override
    public String toString() {
        return "\t";
    }
}
