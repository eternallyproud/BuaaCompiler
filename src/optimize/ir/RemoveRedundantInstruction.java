package optimize.ir;

import config.Configuration;
import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.ConversionOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.terminator.Br;
import utils.Tools;

import java.util.ArrayList;

public class RemoveRedundantInstruction {
    public final static RemoveRedundantInstruction REMOVE_REDUNDANT_INSTRUCTION = new RemoveRedundantInstruction();

    private Module module;
    private boolean hasChanged;

    private RemoveRedundantInstruction() {
    }

    public void optimize(Module module) {
        this.module = module;

        Tools.printOptimizeInfo("多余指令移除优化", Configuration.REMOVE_REDUNDANT_INSTRUCTION_OPTIMIZATION);

        optimize();
    }

    public boolean optimize() {
        hasChanged = false;
        if (Configuration.REMOVE_REDUNDANT_INSTRUCTION_OPTIMIZATION) {
            for (Function function : module.getFunctions()) {
                optimize(function);
            }
        }
        return hasChanged;
    }

    private void optimize(Function function) {
        removeRedundantZext(function);
        removeRedundantConversionOperation(function);
        removeRedundantBr(function);
    }

    private void removeRedundantZext(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            boolean hasChanged;
            do {
                ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
                hasChanged = false;
                for (Instruction instruction : instructions) {
                    if (instruction instanceof ConversionOperation conversionOperation && conversionOperation.isZext()) {
                        if (conversionOperation.getUsedValue(0) instanceof Constant constant) {
                            conversionOperation.updateAllUsers(constant.convertTo(conversionOperation.getValueType()));
                            conversionOperation.removeAllUse();
                            basicBlock.removeInstruction(conversionOperation);
                            hasChanged = true;
                            this.hasChanged = true;
                        }
                    }
                }
            } while (hasChanged);
        }
    }

    private void removeRedundantConversionOperation(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            boolean hasChanged;
            do {
                ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
                hasChanged = false;
                for (int i = 0; i < instructions.size() - 1; i++) {
                    Instruction instruction1 = instructions.get(i);
                    Instruction instruction2 = instructions.get(i + 1);
                    if (instruction1 instanceof ConversionOperation conversionOperation1 && instruction2 instanceof ConversionOperation conversionOperation2) {
                        if (conversionOperation1.getUserList().size() <= 1 && conversionOperation2.getUsedValue(0) == conversionOperation1 && conversionOperation1.getUsedValue(0).getValueType().equals(conversionOperation2.getValueType())) {
                            conversionOperation2.updateAllUsers(conversionOperation1.getUsedValue(0));
                            conversionOperation1.removeAllUse();
                            conversionOperation2.removeAllUse();
                            basicBlock.removeInstruction(conversionOperation1);
                            basicBlock.removeInstruction(conversionOperation2);
                            i++;
                            hasChanged = true;
                            this.hasChanged = true;
                        }
                    }
                }
            } while (hasChanged);
        }
    }

    private void removeRedundantBr(Function function) {
        removeRedundantConditionalBr(function);
        removeRedundantUnconditionalBr(function);
    }

    private void removeRedundantConditionalBr(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
            for (Instruction instruction : instructions) {
                if (instruction instanceof Br.ConditionalBr conditionalBr) {
                    if (conditionalBr.getCondValue() instanceof Constant constant) {
                        BasicBlock ifBasicBlock = conditionalBr.getIfBasicBlock();
                        BasicBlock elseBasicBlock = conditionalBr.getElseBasicBlock();
                        BasicBlock desBasicBlock = Integer.parseInt(constant.getName()) == 1 ? ifBasicBlock : elseBasicBlock;
                        BasicBlock yeildBasicBlock = Integer.parseInt(constant.getName()) == 1 ? elseBasicBlock : ifBasicBlock;

                        //remove old br
                        conditionalBr.removeAllUse();
                        basicBlock.removeInstruction(conditionalBr);

                        //add new br
                        Br br = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(function), desBasicBlock);
                        basicBlock.addInstruction(br);

                        //update father-son
                        basicBlock.removeSon(yeildBasicBlock);

                        //update predecessor-successor
                        basicBlock.removeFromSuccessors(yeildBasicBlock);
                        yeildBasicBlock.removeFromPredecessors(basicBlock);

                        this.hasChanged = true;
                    } else if (conditionalBr.getIfBasicBlock() == conditionalBr.getElseBasicBlock()) {
                        //remove old br
                        conditionalBr.removeAllUse();
                        basicBlock.removeInstruction(conditionalBr);

                        //add new br
                        Br br = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(function), conditionalBr.getIfBasicBlock());
                        basicBlock.addInstruction(br);

                        this.hasChanged = true;
                    }
                }
            }
        }
    }

    private void removeRedundantUnconditionalBr(Function function) {
        ArrayList<BasicBlock> basicBlocks = new ArrayList<>(function.getBasicBlocks());
        for (BasicBlock basicBlock : basicBlocks) {
            if (basicBlock.equals(basicBlocks.get(0))) {
                continue;
            }
            if (basicBlock.getInstructions().size() == 1 && basicBlock.getInstructions().get(0) instanceof Br.UnconditionalBr unconditionalBr) {
                BasicBlock destBasicBlock = unconditionalBr.getDestBasicBlock();

                if (basicBlock == destBasicBlock || basicBlock.isUsedByPhi()) {
                    continue;
                }

                unconditionalBr.removeAllUse();
                basicBlock.removeInstruction(unconditionalBr);

                //update predecessors
                for (BasicBlock predecessor : basicBlock.getPredecessors()) {
                    predecessor.updateSuccessors(basicBlock, destBasicBlock);
                }

                //update successors
                for (BasicBlock successor : basicBlock.getSuccessors()) {
                    successor.updatePredecessors(basicBlock, basicBlock.getPredecessors());
                }

                //update father
                BasicBlock fatherBasicBlock = basicBlock.getFather();
                fatherBasicBlock.removeSon(basicBlock);

                //update son
                for (BasicBlock sonBasicBlock : basicBlock.getSon()) {
                    sonBasicBlock.setFather(fatherBasicBlock);
                }

                //delete basic block
                basicBlock.updateAllUsers(destBasicBlock);
                function.removeBasicBlock(basicBlock);

                hasChanged = true;
            }
        }
    }
}
