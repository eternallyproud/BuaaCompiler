package frontend.ir.llvm.value;

import backend.AssemblyBuilder;
import backend.assembly.Label;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.OtherValueType;
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
    public void buildAssembly() {
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(new Label(name));
        for(Instruction instruction : instructions){
            instruction.buildAssembly();
        }
    }

    @Override
    public String toString() {
        return name + ":\n" +
                Tools.arrayListToString(instructions, "\n");
    }
}
