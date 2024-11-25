package frontend.ir.llvm.value.instruction.other;

import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.ValueType;

public class Operation extends Instruction {

    public Operation(ValueType valueType, String name) {
        super(valueType, name);
    }
}
