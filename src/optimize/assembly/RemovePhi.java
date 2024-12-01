package optimize.assembly;

import backend.Register;
import config.Configuration;
import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.optimize.Move;
import frontend.ir.llvm.value.instruction.other.ParallelCopy;
import frontend.ir.llvm.value.instruction.optimize.Phi;
import frontend.ir.llvm.value.instruction.terminator.Br;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemovePhi {
    public final static RemovePhi REMOVE_PHI = new RemovePhi();
    private Module module;

    private RemovePhi() {
    }

    public void init(Module module) {
        this.module = module;
    }


    public void build() {
        if (Configuration.MEM2REG_OPTIMIZATION) {
            Tools.printStartMessage("Phi指令移除");

            for (Function function : module.getFunctions()) {
                //remove phi, add parallel copy
                removePhi(function);

                //remove parallel copy, add move
                removeParallelCopy(function);
            }

            Tools.printEndMessage("Phi指令移除");
        }
    }

    private void removePhi(Function function) {
        //there are basic block added to function during the process, so we need to use a new ArrayList
        for (BasicBlock basicBlock : new ArrayList<>(function.getBasicBlocks())) {
            //only process basic blocks with phi
            if (!(basicBlock.getInstructions().get(0) instanceof Phi)) {
                continue;
            }

            //save the parallel copies
            ArrayList<ParallelCopy> parallelCopies = new ArrayList<>();

            //there are predecessors altered during the process, so we need to use a new ArrayList
            for (BasicBlock predecessor : new ArrayList<>(basicBlock.getPredecessors())) {
                //parallel copy
                ParallelCopy parallelCopy = new ParallelCopy(IRBuilder.IR_BUILDER.getLocalVarName(function));
                parallelCopies.add(parallelCopy);

                insertParallelCopy(parallelCopy, predecessor, basicBlock);
            }

            for (Instruction instruction : new ArrayList<>(basicBlock.getInstructions())) {
                if (instruction instanceof Phi phi) {
                    for (ParallelCopy parallelCopy : parallelCopies) {
                        Value value = phi.getValueWithBasicBlock(parallelCopy.getFatherBasicBlock());

                        //only process defined value
                        if (value == null || value instanceof Constant.Undefined) {
                            continue;
                        }

                        //add a copy
                        parallelCopy.addCopy(phi, value);
                    }

                    //phi is only removed formally, because it will still be used as a value in the future (it will not be build anyway)
                }
            }

        }
    }

    private void insertParallelCopy(ParallelCopy parallelCopy, BasicBlock predecessor, BasicBlock successor) {
        Function function = successor.getFatherFunction();

        //no critical edges
        if (predecessor.getSuccessors().size() == 1) {
            predecessor.addBeforeLast(parallelCopy);
            parallelCopy.setFatherBasicBlock(predecessor);
        }
        //has critical edges
        else {
            BasicBlock newBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
            newBasicBlock.setFatherFunction(function);
            function.addBasicBlock(newBasicBlock);

            //add parallel copy to new basic block
            newBasicBlock.addInstruction(parallelCopy);
            parallelCopy.setFatherBasicBlock(newBasicBlock);

            //get the conditional br at the end of predecessor
            Br.ConditionalBr conditionalBr = (Br.ConditionalBr) predecessor.getLastInstruction();
            BasicBlock ifBasicBlock = conditionalBr.getIfBasicBlock();

            //modify the conditional br
            if (ifBasicBlock.equals(successor)) {
                conditionalBr.setIfBasicBlock(newBasicBlock);
            } else {
                conditionalBr.setElseBasicBlock(newBasicBlock);
            }

            //add unconditional br
            Br.UnconditionalBr unconditionalBr = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(function), successor);
            IRBuilder.IR_BUILDER.addInstructionToBasicBlock(unconditionalBr, newBasicBlock);

            //update predecessor and successor
            predecessor.updateSuccessors(successor, newBasicBlock);
            successor.updatePredecessors(predecessor, newBasicBlock);

            //update the predecessors and successors of new basic block
            newBasicBlock.setPredecessors(new ArrayList<>(List.of(predecessor)));
            newBasicBlock.setSuccessors(new ArrayList<>(List.of(successor)));


            //update the basic block in phi
            for (int i = 0; ; i++) {
                if (successor.getInstructions().get(i) instanceof Phi phi) {
                    phi.updateBasicBlock(predecessor, newBasicBlock);
                } else {
                    break;
                }
            }
        }
    }

    private void removeParallelCopy(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            ArrayList<Instruction> instructions = basicBlock.getInstructions();
            if (instructions.size() >= 2 && instructions.get(instructions.size() - 2) instanceof ParallelCopy parallelCopy) {
                basicBlock.removeInstruction(parallelCopy);

                insertMove(parallelCopy, basicBlock, function);
            }
        }
    }

    private void insertMove(ParallelCopy parallelCopy, BasicBlock basicBlock, Function function) {
        ArrayList<Phi> phiList = parallelCopy.getPhiList();
        ArrayList<Value> sourceValueList = parallelCopy.getSourceValueList();

        ArrayList<Move> moveList = new ArrayList<>();
        for (int i = 0; i < phiList.size(); i++) {
            Move move = new Move(IRBuilder.IR_BUILDER.getLocalVarName(function), phiList.get(i), sourceValueList.get(i));
            moveList.add(move);
        }

        ArrayList<Move> tempMoveList = new ArrayList<>();
        ArrayList<Value> tempValueList = new ArrayList<>();

        HashMap<Value, Register> valueToRegister = function.getValueToRegister();

        for (int i = moveList.size() - 1; i >= 0; i--) {
            Value sourceValue = moveList.get(i).getUsedValue(1);
            if (!tempValueList.contains(sourceValue) && !(sourceValue instanceof Constant)) {
                boolean registerConflict = false;
                for (int j = 0; j < i; j++) {
                    if (valueToRegister != null && valueToRegister.get(sourceValue) != null &&
                            valueToRegister.get(moveList.get(j).getUsedValue(0)) == valueToRegister.get(sourceValue)) {
                        registerConflict = true;
                        break;
                    }
                }
                if (registerConflict) {
                    Value tempValue = new Value(sourceValue.getValueType(), IRBuilder.IR_BUILDER.getLocalVarName(function));
                    for (Move move : moveList) {
                        if (move.getUsedValue(1).equals(sourceValue)) {
                            move.setFromValue(tempValue);
                        }
                    }
                    Move move = new Move(IRBuilder.IR_BUILDER.getLocalVarName(function), tempValue, sourceValue);
                    move.setFatherBasicBlock(basicBlock);
                    tempMoveList.add(move);
                }
                tempValueList.add(sourceValue);
            }
        }

        for (Move move : tempMoveList) {
            moveList.add(0, move);
        }

        for (Move move : moveList) {
            basicBlock.addBeforeLast(move);
            move.setFatherBasicBlock(basicBlock);
        }
    }
}
