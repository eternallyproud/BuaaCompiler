package optimize;

import backend.AssemblyRecord;
import config.Configuration;
import error.ErrorHandler;
import frontend.ir.llvm.value.Module;
import optimize.assembly.GlobalVariableScoring;
import optimize.assembly.GraphColoringRegisterAllocation;
import optimize.assembly.PeepHole;
import optimize.assembly.RemovePhi;
import optimize.ir.ConstantFolding;
import optimize.ir.ControlFlowGraph;
import optimize.ir.DeleteDeadCode;
import optimize.ir.Dominance;
import optimize.assembly.LiveVariableAnalysis;
import optimize.ir.GlobalVariableNumbering;
import optimize.ir.Mem2Reg;
import optimize.ir.RemoveRedundantInstruction;
import optimize.ir.RemoveUnreachableBasicBlock;
import optimize.ir.RemoveUnreachableInstruction;
import utils.InOut;
import utils.Tools;

public class OptimizeManager {
    public final static OptimizeManager OPTIMIZE_MANAGER = new OptimizeManager();
    private Module module;

    public void init(Module module) {
        this.module = module;
    }

    public void optimizeIR() {
        if (!ErrorHandler.ERROR_HANDLER.isEmpty()) {
            Tools.printFailMessage("中间代码优化");
        } else {
            Tools.printStartMessage("中间代码优化");

            RemoveUnreachableInstruction.REMOVE_UNREACHABLE_INSTRUCTION.optimize(module);
            ControlFlowGraph.CONTROL_FLOW_GRAPH.build(module);
            RemoveUnreachableBasicBlock.REMOVE_UNREACHABLE_BASIC_BLOCK.optimize(module);
            Dominance.DOMINANCE.build(module);
            Mem2Reg.MEM2REG.optimize(module);
            DeleteDeadCode.DELETE_DEAD_CODE.optimize(module);
            GlobalVariableNumbering.GLOBAL_VARIABLE_NUMBERING.optimize(module);
            ConstantFolding.CONSTANT_FOLDING.optimize(module);
            RemoveRedundantInstruction.REMOVE_REDUNDANT_INSTRUCTION.optimize(module);

            boolean hasChanged = true;
            while (hasChanged && Configuration.MULTI_ROUND_OPTIMIZATION) {
                hasChanged = false;
                module.clear();

                if (DeleteDeadCode.DELETE_DEAD_CODE.optimize()) hasChanged = true;
                if (GlobalVariableNumbering.GLOBAL_VARIABLE_NUMBERING.optimize()) hasChanged = true;
                if (ConstantFolding.CONSTANT_FOLDING.optimize()) hasChanged = true;
                if (RemoveRedundantInstruction.REMOVE_REDUNDANT_INSTRUCTION.optimize()) hasChanged = true;
            }

            ControlFlowGraph.CONTROL_FLOW_GRAPH.build();
            RemoveUnreachableBasicBlock.REMOVE_UNREACHABLE_BASIC_BLOCK.optimize();
            Dominance.DOMINANCE.build();

            Tools.printEndMessage("中间代码优化");
        }
    }

    public void writeOptimizedIR() {
        InOut.writeIROptimizationResult(module.toString());
    }

    public void optimizeAssembly() {
        if (!ErrorHandler.ERROR_HANDLER.isEmpty()) {
            Tools.printFailMessage("汇编前优化");
        } else {
            Tools.printStartMessage("汇编前优化");

            LiveVariableAnalysis.LIVE_VARIABLE_ANALYSIS.init(module);
            LiveVariableAnalysis.LIVE_VARIABLE_ANALYSIS.analyze();

            GlobalVariableScoring.GLOBAL_VARIABLE_SCORING.analyze(module);

            GraphColoringRegisterAllocation.GRAPH_COLORING_REGISTER_ALLOCATION.init(module);
            GraphColoringRegisterAllocation.GRAPH_COLORING_REGISTER_ALLOCATION.optimize();

            RemovePhi.REMOVE_PHI.init(module);
            RemovePhi.REMOVE_PHI.build();

            Tools.printEndMessage("汇编前优化");
        }
    }

    public void optimizeAssembly(AssemblyRecord record){
        if (!ErrorHandler.ERROR_HANDLER.isEmpty()) {
            Tools.printFailMessage("汇编代码优化");
        } else {
            Tools.printStartMessage("汇编代码优化");

            System.out.print(record);

            PeepHole.PEEP_HOLE.optimize(record);

            Tools.printEndMessage("汇编代码优化");
        }
    }
}
