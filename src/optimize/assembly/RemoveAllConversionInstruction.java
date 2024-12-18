package optimize.assembly;

import config.Configuration;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.ConversionOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import utils.Tools;

import java.util.ArrayList;

public class RemoveAllConversionInstruction {
    public final static RemoveAllConversionInstruction REMOVE_ALL_CONVERSION_INSTRUCTION = new RemoveAllConversionInstruction();

    private RemoveAllConversionInstruction() {
    }

    public void optimize(Module module) {
        if (Configuration.REMOVE_ALL_CONVERSION_INSTRUCTION_OPTIMIZATION) {
            Tools.printOpenInfo("移除全部类型转换指令优化");

            for (Function function : module.getFunctions()) {
                for (BasicBlock basicBlock : function.getBasicBlocks()) {
                    ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
                    for (Instruction instruction : instructions) {
                        if (instruction instanceof ConversionOperation) {
                            instruction.updateAllUsers(instruction.getUsedValue(0));
                            instruction.removeAllUse();
                            basicBlock.removeInstruction(instruction);
                        }
                    }
                }
            }
        } else {
            Tools.printCloseInfo("移除全部类型转换指令优化");
        }
    }
}
