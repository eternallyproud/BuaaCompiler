package frontend.ir.llvm.value.instruction.other;

import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.ValueType;

public class OtherOperation extends Instruction {

    public OtherOperation(ValueType valueType, String name) {
        super(valueType, name);
    }
}
