package optimize;

import error.ErrorHandler;
import frontend.ir.llvm.value.Module;
import optimize.assembly.GraphColoringRegisterAllocation;
import optimize.assembly.RemovePhi;
import optimize.ir.ControlFlowGraph;
import optimize.ir.DeleteDeadCode;
import optimize.ir.Dominance;
import optimize.assembly.LiveVariableAnalysis;
import optimize.ir.Mem2Reg;
import optimize.ir.RemoveRedundantInstruction;
import optimize.ir.RemoveUnreachableBasicBlock;
import optimize.ir.RemoveUnreachableInstruction;
import utils.InOut;
import utils.Tools;

public class OptimizeManager {
    public final static OptimizeManager OPTIMIZE_MANAGER = new OptimizeManager();
    private Module module;

    public void init(Module module){
        this.module = module;
    }

    public void optimizeIR(){
        if(!ErrorHandler.ERROR_HANDLER.isEmpty()){
            Tools.printFailMessage("中间代码优化");
        }else{
            Tools.printStartMessage("中间代码优化");

            RemoveUnreachableInstruction.REMOVE_UNREACHABLE_INSTRUCTION.init(module);
            RemoveUnreachableInstruction.REMOVE_UNREACHABLE_INSTRUCTION.optimize();

            ControlFlowGraph.CONTROL_FLOW_GRAPH.init(module);
            ControlFlowGraph.CONTROL_FLOW_GRAPH.build();

            RemoveUnreachableBasicBlock.REMOVE_UNREACHABLE_BASIC_BLOCK.init(module);
            RemoveUnreachableBasicBlock.REMOVE_UNREACHABLE_BASIC_BLOCK.optimize();

            Dominance.DOMINANCE.init(module);
            Dominance.DOMINANCE.build();

            Mem2Reg.MEM2REG.init(module);
            Mem2Reg.MEM2REG.optimize();

            DeleteDeadCode.DELETE_DEAD_CODE.init(module);
            DeleteDeadCode.DELETE_DEAD_CODE.optimize();

            RemoveRedundantInstruction.REMOVE_REDUNDANT_INSTRUCTION.init(module);
            RemoveRedundantInstruction.REMOVE_REDUNDANT_INSTRUCTION.optimize();

            Tools.printEndMessage("中间代码优化");
        }
    }

    public void writeOptimizedIR() {
        InOut.writeIROptimizationResult(module.toString());
    }

    public void optimizeAssembly(){
        if(!ErrorHandler.ERROR_HANDLER.isEmpty()){
            Tools.printFailMessage("汇编代码优化");
        }else{
            Tools.printStartMessage("汇编代码优化");

            LiveVariableAnalysis.LIVE_VARIABLE_ANALYSIS.init(module);
            LiveVariableAnalysis.LIVE_VARIABLE_ANALYSIS.analyze();

            GraphColoringRegisterAllocation.GRAPH_COLORING_REGISTER_ALLOCATION.init(module);
            GraphColoringRegisterAllocation.GRAPH_COLORING_REGISTER_ALLOCATION.optimize();

            RemovePhi.REMOVE_PHI.init(module);
            RemovePhi.REMOVE_PHI.build();

            Tools.printEndMessage("汇编代码优化");
        }
    }
}
