package frontend.ir.llvm.value.instruction.memory;

import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.ValueType;

public class MemoryOperation extends Instruction {
    public MemoryOperation(ValueType valueType, String name) {
        super(valueType, name);
    }
}
