package frontend.ir.value.instruction.memory;

import frontend.ir.value.instruction.Instruction;
import frontend.ir.value.type.ValueType;

public class MemoryOperation extends Instruction {
    public MemoryOperation(ValueType valueType, String name) {
        super(valueType, name);
    }
}
