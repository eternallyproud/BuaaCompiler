package frontend.ir.value.instruction.terminator;

import frontend.ir.value.instruction.Instruction;
import frontend.ir.value.type.ScalarValueType;

public class TerminatorInstruction extends Instruction {
    public TerminatorInstruction(String name) {
        super(ScalarValueType.VOID, name);
    }

}
