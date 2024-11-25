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

    private DeleteDeadCode() {
    }

    public void init(Module module) {
        this.module = module;
    }

    public void optimize() {
        if (Configuration.DELETE_DEAD_CODE_OPTIMIZATION) {
            Tools.printOpenInfo("死代码删除优化");

            for (Function function : module.getFunctions()) {
                for (BasicBlock basicBlock : function.getBasicBlocks()) {
                    optimize(basicBlock);
                }
            }
        } else {
            Tools.printCloseInfo("死代码删除优化");
        }
    }

    public void optimize(BasicBlock basicBlock) {
        ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
        for(Instruction instruction : instructions) {
            if(instruction.disposable() && instruction.getUserList().isEmpty()){
                //remove the instruction
                basicBlock.removeInstruction(instruction);
                instruction.removeAllUse();
            }
        }
    }
}
