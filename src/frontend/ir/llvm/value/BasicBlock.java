package frontend.ir.llvm.value;

import backend.AssemblyBuilder;
import backend.assembly.Label;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.optimize.Phi;
import frontend.ir.llvm.value.type.OtherValueType;
import utils.Tools;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions;
    private Function fatherFunction;

    //Control Flow Graph
    private ArrayList<BasicBlock> predecessors;
    private ArrayList<BasicBlock> successors;

    //Dominance
    private ArrayList<BasicBlock> dominated;
    private BasicBlock father;
    private ArrayList<BasicBlock> son;
    private ArrayList<BasicBlock> dominanceFrontier;

    //Live Variable Analysis
    private ArrayList<Value> def;
    private ArrayList<Value> use;
    private ArrayList<Value> in;
    private ArrayList<Value> out;

    public BasicBlock(String name) {
        super(OtherValueType.BASIC_BLOCK, name);
        instructions = new ArrayList<>();
    }

    public boolean isEmpty() {
        return instructions.isEmpty();
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addPhi(Phi phi) {
        instructions.add(0, phi);
    }

    public void addBeforeLast(Instruction instruction) {
        instructions.add(instructions.size() - 1, instruction);
    }

    public void removeInstruction(Instruction instruction) {
        instructions.remove(instruction);
    }

    public Instruction getLastInstruction() {
        return instructions.get(instructions.size() - 1);
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void setFatherFunction(Function fatherFunction) {
        this.fatherFunction = fatherFunction;
    }

    public Function getFatherFunction() {
        return fatherFunction;
    }

    public void setPredecessors(ArrayList<BasicBlock> predecessors) {
        this.predecessors = predecessors;
    }

    public void removeBasicBlockFromPredecessors(BasicBlock basicBlock) {
        predecessors.remove(basicBlock);
    }

    public void updatePredecessors(BasicBlock oldPredecessor, BasicBlock newPredecessor) {
        predecessors.set(predecessors.indexOf(oldPredecessor), newPredecessor);
    }

    public ArrayList<BasicBlock> getPredecessors() {
        return predecessors;
    }

    public void setSuccessors(ArrayList<BasicBlock> successors) {
        this.successors = successors;
    }

    public void updateSuccessors(BasicBlock oldSuccessor, BasicBlock newSuccessor) {
        successors.set(successors.indexOf(oldSuccessor), newSuccessor);
    }

    public ArrayList<BasicBlock> getSuccessors() {
        return successors;
    }

    public void setDominated(ArrayList<BasicBlock> dominated) {
        this.dominated = dominated;
    }

    public boolean isImmediateDominatorOf(BasicBlock basicBlock) {
        //strict dominance: dominator basic block must not be equal to dominated basic block
        if (this == basicBlock) {
            return false;
        }

        //immediate dominance: dominator basic block must be directly dominated by dominated basic block
        for (BasicBlock dominatedBasicBlock : dominated) {
            if (!dominatedBasicBlock.equals(this) && !dominatedBasicBlock.equals(basicBlock) && dominatedBasicBlock.dominates(basicBlock)) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<BasicBlock> getDominated() {
        return dominated;
    }

    public boolean dominates(BasicBlock basicBlock) {
        return dominated.contains(basicBlock);
    }

    public void setFather(BasicBlock father) {
        this.father = father;
    }

    public BasicBlock getFather() {
        return father;
    }

    public void setSon(ArrayList<BasicBlock> son) {
        this.son = son;
    }

    public ArrayList<BasicBlock> getSon() {
        return son;
    }

    public void setDominanceFrontier(ArrayList<BasicBlock> dominanceFrontier) {
        this.dominanceFrontier = dominanceFrontier;
    }

    public ArrayList<BasicBlock> getDominanceFrontier() {
        return dominanceFrontier;
    }

    public void analyzeDefUse(){
        def = new ArrayList<>();
        use = new ArrayList<>();

        for (Instruction instruction : instructions) {
            for(Value usedValue : instruction.getUsedValueList()){
                if(!def.contains(usedValue) && !(usedValue instanceof Constant)){
                    use.add(usedValue);
                }
            }
            if(!use.contains(instruction)&&instruction.usable()){
                def.add(instruction);
            }
        }
    }

    public ArrayList<Value> getDef() {
        return def;
    }

    public ArrayList<Value> getUse() {
        return use;
    }

    public void setIn(ArrayList<Value> in) {
        this.in = in;
    }

    public ArrayList<Value> getIn() {
        return in;
    }

    public void setOut(ArrayList<Value> out) {
        this.out = out;
    }

    public ArrayList<Value> getOut() {
        return out;
    }

    @Override
    public void buildAssembly() {
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(new Label(name));
        for (Instruction instruction : instructions) {
            instruction.buildAssembly();
        }
    }

    @Override
    public String toString() {
        return name + ":\n" +
                Tools.arrayListToString(instructions, "\n");
    }
}
