package optimize.ir;

import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.terminator.Br;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;

public class ControlFlowGraph {
    public final static ControlFlowGraph CONTROL_FLOW_GRAPH = new ControlFlowGraph();
    private Module module;

    private ControlFlowGraph() {
    }

    public void build(Module module) {
        Tools.printStartMessage("建立控制流图");

        this.module = module;
        build();

        Tools.printEndMessage("建立控制流图");
    }

    public void build(){
        for (Function function : module.getFunctions()) {
            build(function);
        }
    }

    private void build(Function function) {
        HashMap<BasicBlock, ArrayList<BasicBlock>> predecessorMap = function.getMap(ArrayList::new);
        HashMap<BasicBlock, ArrayList<BasicBlock>> successorMap = function.getMap(ArrayList::new);

        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            Instruction br = basicBlock.getLastInstruction();

            //unconditional br
            if (br instanceof Br.UnconditionalBr unconditionalBr) {
                BasicBlock destBasicBlock = unconditionalBr.getDestBasicBlock();

                //basicBlock is the predecessor of destBasicBlock
                predecessorMap.get(destBasicBlock).add(basicBlock);

                //destBasicBlock is the successor of basicBlock
                successorMap.get(basicBlock).add(destBasicBlock);
            }
            //conditional br
            else if (br instanceof Br.ConditionalBr conditionalBr) {
                BasicBlock ifBasicBlock = conditionalBr.getIfBasicBlock();
                BasicBlock elseBasicBlock = conditionalBr.getElseBasicBlock();

                //basicBlock is the predecessor of ifBasicBlock and elseBasicBlock
                predecessorMap.get(ifBasicBlock).add(basicBlock);
                predecessorMap.get(elseBasicBlock).add(basicBlock);

                //ifBasicBlock and elseBasicBlock are successors of basicBlock
                successorMap.get(basicBlock).add(ifBasicBlock);
                successorMap.get(basicBlock).add(elseBasicBlock);
            }
        }

        //set predecessors and successors of basic blocks
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            basicBlock.setPredecessors(predecessorMap.get(basicBlock));
            basicBlock.setSuccessors(successorMap.get(basicBlock));
        }
    }
}
