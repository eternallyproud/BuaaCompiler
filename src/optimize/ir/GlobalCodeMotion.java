package optimize.ir;

import config.Configuration;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.User;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.BinaryOperation;
import frontend.ir.llvm.value.instruction.ConversionOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.memory.GetElementPtr;
import frontend.ir.llvm.value.instruction.memory.Load;
import frontend.ir.llvm.value.instruction.optimize.Phi;
import frontend.ir.llvm.value.instruction.other.Call;
import frontend.ir.llvm.value.instruction.other.ICmp;
import frontend.ir.llvm.value.type.PointerValueType;
import utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/* this optimization must be carry out after removing redundant conversion operation, I really don't know why... */
public class GlobalCodeMotion {
    public final static GlobalCodeMotion GLOBAL_CODE_MOTION = new GlobalCodeMotion();

    private Module module;
    private final HashSet<Instruction> visited;

    private GlobalCodeMotion() {
        visited = new HashSet<>();
    }

    public void optimize(Module module) {
        this.module = module;

        Tools.printOptimizeInfo("全局代码移动优化", Configuration.GLOBAL_CODE_MOTION_OPTIMIZATION);

        optimize();
    }

    public boolean optimize() {
        for (Function function : module.getFunctions()) {
            optimize(function);
        }
        return false;
    }

    public void optimize(Function function) {
        //post order of dominance tree
        ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
        function.getBasicBlocks().get(0).getPostOrderOfDominanceTree(basicBlocks);

        //reverse
        Collections.reverse(basicBlocks);

        ArrayList<Instruction> instructions = new ArrayList<>();
        for (BasicBlock block : basicBlocks) {
            instructions.addAll(block.getInstructions());
        }

        visited.clear();
        for (Instruction instruction : instructions) {
            scheduleEarly(instruction);
        }

        visited.clear();
        Collections.reverse(instructions);
        for (Instruction instruction : instructions) {
            scheduleLate(instruction);
        }
    }

    public void scheduleEarly(Instruction instruction) {
        if (!movable(instruction) || visited.contains(instruction)) {
            return;
        }
        visited.add(instruction);

        //remove from old basic block
        instruction.getFatherBasicBlock().removeInstruction(instruction);

        //add to first basic block
        BasicBlock firstBasicBlock = instruction.getFatherBasicBlock().getFatherFunction().getBasicBlocks().get(0);
        firstBasicBlock.addBeforeLast(instruction);
        instruction.setFatherBasicBlock(firstBasicBlock);

        for (Value operand : instruction.getUsedValueList()) {
            if (operand instanceof Instruction operandInstruction) {
                scheduleEarly(operandInstruction);

                if (instruction.getFatherBasicBlock().getGeneration() < operandInstruction.getFatherBasicBlock().getGeneration()) {
                    //remove from old basic block
                    instruction.getFatherBasicBlock().getInstructions().remove(instruction);

                    //add to operand basic block
                    BasicBlock operandBasicBlock = operandInstruction.getFatherBasicBlock();
                    operandBasicBlock.addBeforeLast(instruction);
                    instruction.setFatherBasicBlock(operandBasicBlock);
                }
            }
        }
    }

    public void scheduleLate(Instruction instruction) {
        if (!movable(instruction) || visited.contains(instruction)) {
            return;
        }
        visited.add(instruction);

        //find the youngest direct ancestor of the instructions that use the value of this instruction
        BasicBlock ancestor = null;
        for (User user : instruction.getUsers()) {
            Instruction userInstruction = (Instruction) user;
            scheduleLate(userInstruction);

            if (userInstruction instanceof Phi phi) {
                for (int i = 0; i < phi.getValues().size(); i++) {
                    Value value = phi.getValues().get(i);
                    if (value == instruction) {
                        ancestor = findYoungestDirectAncestor(ancestor, phi.getBasicBlocks().get(i));
                    }
                }
            } else {
                ancestor = findYoungestDirectAncestor(ancestor, userInstruction.getFatherBasicBlock());
            }
        }

        assert ancestor != null;
        BasicBlock posBasicBlock = ancestor;
        while (ancestor != instruction.getFatherBasicBlock()) {
            ancestor = ancestor.getFather();
            if (ancestor.getLoopDepth() < posBasicBlock.getLoopDepth()) {
                posBasicBlock = ancestor;
            }
        }
        //remove from old basic block
        instruction.getFatherBasicBlock().removeInstruction(instruction);

        //add to pos block
        posBasicBlock.addBeforeLast(instruction);
        instruction.setFatherBasicBlock(posBasicBlock);

        //find an appropriate position
        for (Instruction otherInstruction : posBasicBlock.getInstructions()) {
            if (otherInstruction != instruction && !(otherInstruction instanceof Phi) && otherInstruction.getUsedValueList().contains(instruction)) {
                posBasicBlock.getInstructions().remove(instruction);
                posBasicBlock.addBefore(otherInstruction, instruction);
                break;
            }
        }
    }

    public boolean movable(Instruction instruction) {
        //never used, wait till further analysis
        if (instruction.getUsers().isEmpty()) {
            return false;
        }

        //normal instruction
        if (instruction instanceof BinaryOperation || instruction instanceof GetElementPtr || instruction instanceof ICmp || instruction instanceof ConversionOperation) {
            return true;
        }

        //call
        if (instruction instanceof Call call) {
            Function function = call.getFunction();

            //has side effect
            if (function.hasSideEffect()) {
                return false;
            }

            //recursive
            if(call.getFatherBasicBlock().getFatherFunction() == function){
                return false;
            }

            //used by pointer
            for (Value user : call.getUsers()) {
                if (user instanceof GetElementPtr || user instanceof Load || user.getValueType() instanceof PointerValueType) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public BasicBlock findYoungestDirectAncestor(BasicBlock basicBlock1, BasicBlock basicBlock2) {
        if (basicBlock1 == null) {
            return basicBlock2;
        }
        while (basicBlock1.getGeneration() < basicBlock2.getGeneration()) {
            basicBlock2 = basicBlock2.getFather();
        }
        while (basicBlock2.getGeneration() < basicBlock1.getGeneration()) {
            basicBlock1 = basicBlock1.getFather();
        }
        while (!(basicBlock1 == basicBlock2)) {
            basicBlock1 = basicBlock1.getFather();
            basicBlock2 = basicBlock2.getFather();
        }
        return basicBlock1;
    }
}
