package optimize.ir;

import config.Configuration;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.ConversionOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import utils.Tools;

import java.util.ArrayList;

public class RemoveRedundantInstruction {
    public final static RemoveRedundantInstruction REMOVE_REDUNDANT_INSTRUCTION = new RemoveRedundantInstruction();
    private Module module;

    private RemoveRedundantInstruction() {
    }

    public void init(Module module) {
        this.module = module;
    }

    public void optimize() {
        if (Configuration.REMOVE_REDUNDANT_INSTRUCTION_OPTIMIZATION) {
            Tools.printOpenInfo("多余指令移除优化");

            for (Function function : module.getFunctions()) {
                optimize(function);
            }
        } else {
            Tools.printCloseInfo("多余指令移除优化");
        }
    }

    private void optimize(Function function) {
        removeRedundantZext(function);
        removeRedundantConversionOperation(function);
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
                        }
                    }
                }
            } while (hasChanged);
        }
    }
}
