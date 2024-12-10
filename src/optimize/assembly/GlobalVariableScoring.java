package optimize.assembly;

import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.Use;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.Instruction;
import utils.Tools;

public class GlobalVariableScoring {
    public final static GlobalVariableScoring GLOBAL_VARIABLE_SCORING = new GlobalVariableScoring();

    private GlobalVariableScoring() {
    }

    public void analyze(Module module){
        Tools.printStartMessage("全局变量打分");

        for (Function function : module.getFunctions()) {
            for(BasicBlock basicBlock : function.getBasicBlocks()){
                for (Instruction instruction : basicBlock.getInstructions()) {
                    if(instruction.usable()){
                        int score = 0;
                        for(Use use : instruction.getUserList()){
                            score += ((Instruction)use.getUser()).getFatherBasicBlock().getLoopDepth() * 100 + 1;
                        }
                        instruction.setScore(score);
                    }
                }
            }
        }

        Tools.printEndMessage("全局变量打分");
    }
}
