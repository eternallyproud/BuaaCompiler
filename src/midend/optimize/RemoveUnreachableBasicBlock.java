package midend.optimize;

import config.Configuration;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import utils.Tools;

import java.util.HashMap;

public class RemoveUnreachableBasicBlock {
    public final static RemoveUnreachableBasicBlock REMOVE_UNREACHABLE_BASIC_BLOCK = new RemoveUnreachableBasicBlock();
    private Module module;

    public RemoveUnreachableBasicBlock() {
    }

    public void init(Module module) {
        this.module = module;
    }

    public void optimize() {
        if (Configuration.REMOVE_UNREACHABLE_BASIC_BLOCK_OPTIMIZATION) {
            Tools.printOpenInfo("不可达基本块移除优化");

            for (Function function : module.getFunctions()) {
                optimize(function);
            }
        } else {
            Tools.printCloseInfo("不可达基本块移除优化");
        }
    }


    private void optimize(Function function) {
        HashMap<BasicBlock, Boolean> reachableMap = new HashMap<>();
        function.getBasicBlocks().forEach(basicBlock -> reachableMap.put(basicBlock, false));

        //check reachable
        checkReachable(reachableMap, function.getBasicBlocks().get(0));

        for (BasicBlock basicBlock : reachableMap.keySet()) {
            if (!reachableMap.get(basicBlock)) {
                //remove basicBlock from predecessors of its successors
                for (BasicBlock successorBasicBlock : basicBlock.getSuccessors()) {
                    successorBasicBlock.removeBasicBlockFromPredecessors(basicBlock);
                }

                //remove basicBlock from function
                function.removeBasicBlock(basicBlock);
            }
        }
    }

    private void checkReachable(HashMap<BasicBlock, Boolean> reachableMap, BasicBlock basicBlock) {
        //if is already reachable, return
        if (reachableMap.get(basicBlock)) {
            return;
        }

        //set reachable
        reachableMap.put(basicBlock, true);

        //check successors
        for (BasicBlock successorBasicBlock : basicBlock.getSuccessors()) {
            checkReachable(reachableMap, successorBasicBlock);
        }
    }
}
