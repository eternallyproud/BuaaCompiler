package optimize.ir;

import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;

public class Dominance {
    public final static Dominance DOMINANCE = new Dominance();
    private Module module;

    private Dominance() {
    }

    public void build(Module module) {
        Tools.printStartMessage("建立支配关系");

        this.module = module;
        build();


        Tools.printEndMessage("建立支配关系");
    }

    public void build() {
        for (Function function : module.getFunctions()) {
            buildDominance(function);
            buildImmediateDominance(function);
            buildDominanceFrontier(function);
        }
    }

    private void buildDominance(Function function) {
        ArrayList<BasicBlock> basicBlocks = function.getBasicBlocks();
        for (BasicBlock dominator : basicBlocks) {
            ArrayList<BasicBlock> reachable = new ArrayList<>();
            buildReachable(reachable, basicBlocks.get(0), dominator);

            //dominated = all - reachable
            ArrayList<BasicBlock> dominated = new ArrayList<>();
            for (BasicBlock b : basicBlocks) {
                if (!reachable.contains(b)) {
                    dominated.add(b);
                }
            }

            dominator.setDominated(dominated);
        }
    }

    private void buildReachable(ArrayList<BasicBlock> reachable, BasicBlock root, BasicBlock dominator) {
        if (root == dominator) {
            return;
        }
        reachable.add(root);
        for (BasicBlock successor : root.getSuccessors()) {
            if (!reachable.contains(successor)) {
                buildReachable(reachable, successor, dominator);
            }
        }
    }

    private void buildImmediateDominance(Function function) {
        HashMap<BasicBlock, BasicBlock> father = new HashMap<>();
        HashMap<BasicBlock, ArrayList<BasicBlock>> son = function.getMap(ArrayList::new);

        for (BasicBlock dominatorBasicBlock : function.getBasicBlocks()) {
            for (BasicBlock dominatedBasicBlock : dominatorBasicBlock.getDominated()) {
                if (dominatorBasicBlock.isImmediateDominatorOf(dominatedBasicBlock)) {
                    father.put(dominatedBasicBlock, dominatorBasicBlock);
                    son.get(dominatorBasicBlock).add(dominatedBasicBlock);
                }
            }
        }

        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            basicBlock.setFather(father.get(basicBlock));
            basicBlock.setSon(son.get(basicBlock));
        }

        buildGeneration(function.getBasicBlocks().get(0), 0);
    }

    private void buildGeneration(BasicBlock root, int generation) {
        root.setGeneration(generation);
        for (BasicBlock son : root.getSon()) {
            buildGeneration(son, generation + 1);
        }
    }

    private void buildDominanceFrontier(Function function) {
        HashMap<BasicBlock, ArrayList<BasicBlock>> dominanceFrontier = function.getMap(ArrayList::new);

        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            for (BasicBlock successorBasicBlock : basicBlock.getSuccessors()) {
                BasicBlock predecessorBasicBlock = basicBlock;

                //predecessorBasicBlock dominates the predecessor of successorBasicBlock and does not strictly dominate successorBasicBlock
                while (!predecessorBasicBlock.dominates(successorBasicBlock) || predecessorBasicBlock.equals(successorBasicBlock)) {
                    dominanceFrontier.get(predecessorBasicBlock).add(successorBasicBlock);
                    predecessorBasicBlock = predecessorBasicBlock.getFather();
                }
            }
        }

        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            basicBlock.setDominanceFrontier(dominanceFrontier.get(basicBlock));
        }
    }
}
