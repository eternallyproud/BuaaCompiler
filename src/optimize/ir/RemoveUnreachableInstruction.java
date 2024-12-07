package optimize.ir;

import config.Configuration;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.terminator.TerminatorInstruction;
import utils.Tools;

import java.util.Iterator;

public class RemoveUnreachableInstruction {
    public final static RemoveUnreachableInstruction REMOVE_UNREACHABLE_INSTRUCTION = new RemoveUnreachableInstruction();
    private Module module;

    private RemoveUnreachableInstruction() {
    }

    public void optimize(Module module) {
        this.module = module;

        Tools.printOptimizeInfo("不可达指令移除优化", Configuration.REMOVE_UNREACHABLE_INSTRUCTION_OPTIMIZATION);

        optimize();
    }

    public void optimize() {
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                optimize(basicBlock);
            }
        }
    }

    private void optimize(BasicBlock basicBlock) {
        boolean reachable = true;
        Iterator<Instruction> iterator = basicBlock.getInstructions().iterator();
        while (iterator.hasNext()) {
            Instruction instruction = iterator.next();

            //remove if unreachable
            if (!reachable) {
                iterator.remove();
            }
            //all instructions after a terminator instruction are unreachable
            else if (instruction instanceof TerminatorInstruction) {
                reachable = false;
            }

        }
    }
}
