package frontend.ir.llvm.value.instruction.terminator;

import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.ScalarValueType;

public class TerminatorInstruction extends Instruction {
    public TerminatorInstruction(String name) {
        super(ScalarValueType.VOID, name);
    }

}
