package frontend.ir.value;

import frontend.ir.value.instruction.Instruction;
import frontend.ir.value.type.OtherValueType;
import utils.Tools;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions;

    public BasicBlock(String name) {
        super(OtherValueType.BASIC_BLOCK, name);
        instructions = new ArrayList<>();
    }

    public boolean isEmpty(){
        return instructions.isEmpty();
    }

    public Instruction getLastInstruction(){
        return instructions.get(instructions.size() - 1);
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    @Override
    public String toString() {
        return name + ":\n" +
                Tools.arrayListToString(instructions, "\n");
    }
}
