package frontend.ir.value.instruction.io;

import frontend.ir.value.instruction.Instruction;
import frontend.ir.value.type.ValueType;

public class IOInstruction extends Instruction {
    public IOInstruction(ValueType valueType, String name) {
        super(valueType, name);
    }
}