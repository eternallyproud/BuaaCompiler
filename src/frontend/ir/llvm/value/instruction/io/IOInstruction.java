package frontend.ir.llvm.value.instruction.io;

import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.ValueType;

public class IOInstruction extends Instruction {
    public IOInstruction(ValueType valueType, String name) {
        super(valueType, name);
    }
}