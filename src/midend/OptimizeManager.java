package midend;

import frontend.ir.llvm.value.Module;
import midend.optimize.RemoveUnreachableBasicBlock;
import midend.optimize.ControlFlowGraph;
import midend.optimize.RemoveUnreachableInstruction;
import utils.InOut;
import utils.Tools;

public class OptimizeManager {
    public final static OptimizeManager OPTIMIZE_MANAGER = new OptimizeManager();
    private Module module;

    public void init(Module module){
        this.module = module;
    }

    public void optimize(){
        Tools.printStartMessage("中间代码优化");

        RemoveUnreachableInstruction.REMOVE_UNREACHABLE_INSTRUCTION.init(module);
        RemoveUnreachableInstruction.REMOVE_UNREACHABLE_INSTRUCTION.optimize();

        ControlFlowGraph.CONTROL_FLOW_GRAPH.init(module);
        ControlFlowGraph.CONTROL_FLOW_GRAPH.build();

        RemoveUnreachableBasicBlock.REMOVE_UNREACHABLE_BASIC_BLOCK.init(module);
        RemoveUnreachableBasicBlock.REMOVE_UNREACHABLE_BASIC_BLOCK.optimize();

        Tools.printEndMessage("中间代码优化");
    }

    public void writeOptimizedIR() {
        InOut.writeIROptimizationResult(module.toString());
    }
}
