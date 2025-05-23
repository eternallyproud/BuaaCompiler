package optimize.ir;

import config.Configuration;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.Instruction;
import utils.Tools;

import java.util.ArrayList;

public class DeleteDeadCode {
    public final static DeleteDeadCode DELETE_DEAD_CODE = new DeleteDeadCode();

    private Module module;
    private boolean hasChange;

    private DeleteDeadCode() {
    }

    public void optimize(Module module) {
        this.module = module;

        Tools.printOptimizeInfo("死代码删除优化", Configuration.DELETE_DEAD_CODE_OPTIMIZATION);

        optimize();
    }

    public boolean optimize() {
        hasChange = false;
        if (Configuration.DELETE_DEAD_CODE_OPTIMIZATION) {
            for (Function function : module.getFunctions()) {
                for (BasicBlock basicBlock : function.getBasicBlocks()) {
                    optimize(basicBlock);
                }
            }
        }
        return hasChange;
    }

    public void optimize(BasicBlock basicBlock) {
        boolean changed;
        do {
            changed = false;
            ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
            for (Instruction instruction : instructions) {
                if (instruction.disposable() && instruction.getUserList().isEmpty()) {
                    //remove the instruction
                    basicBlock.removeInstruction(instruction);
                    instruction.removeAllUse();
                    hasChange = true;
                    changed = true;
                }
            }
        } while (changed);
    }
}
