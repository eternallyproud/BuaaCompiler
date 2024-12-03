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
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            removeRedundantZext(basicBlock);
        }
    }

    private void removeRedundantZext(BasicBlock basicBlock) {
        boolean hasChanged;
        do {
            ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
            hasChanged = false;
            for (Instruction instruction : instructions) {
                if (instruction instanceof ConversionOperation conversionOperation && conversionOperation.isZext()) {
                    if (conversionOperation.getUsedValue(0) instanceof Constant constant) {
                        conversionOperation.updateAllUsers(constant.convertTo(conversionOperation.getValueType()));
                        basicBlock.removeInstruction(conversionOperation);
                        hasChanged = true;
                    }
                }
            }
        } while (hasChanged);
    }
}
