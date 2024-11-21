package frontend.ir.llvm.value;

import backend.AssemblyBuilder;
import backend.assembly.Label;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.OtherValueType;
import utils.Tools;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions;

    //Control Flow Graph
    private ArrayList<BasicBlock> predecessors;
    private ArrayList<BasicBlock> successors;

    public BasicBlock(String name) {
        super(OtherValueType.BASIC_BLOCK, name);
        instructions = new ArrayList<>();
    }

    public boolean isEmpty(){
        return instructions.isEmpty();
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public Instruction getLastInstruction(){
        return instructions.get(instructions.size() - 1);
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void setPredecessors(ArrayList<BasicBlock> predecessors) {
        this.predecessors = predecessors;
    }

    public void removeBasicBlockFromPredecessors(BasicBlock basicBlock){
        predecessors.remove(basicBlock);
    }

    public void setSuccessors(ArrayList<BasicBlock> successors) {
        this.successors = successors;
    }

    public ArrayList<BasicBlock> getSuccessors() {
        return successors;
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
