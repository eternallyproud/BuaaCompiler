package optimize.assembly;

import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LiveVariableAnalysis {
    public final static LiveVariableAnalysis LIVE_VARIABLE_ANALYSIS = new LiveVariableAnalysis();

    private LiveVariableAnalysis() {
    }

    public void analyze(Module module) {
        Tools.printStartMessage("活跃变量分析");

        for (Function function : module.getFunctions()) {
            analyze(function);
        }

        Tools.printEndMessage("活跃变量分析");
    }

    private void analyze(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            basicBlock.analyzeDefUse();
        }

        analyzeInOut(function);
    }

    private void analyzeInOut(Function function) {
        HashMap<BasicBlock, HashSet<Value>> inMap = function.getMap(HashSet::new);
        HashMap<BasicBlock, HashSet<Value>> outMap = function.getMap(HashSet::new);

        boolean hasUpdate;
        do {
            hasUpdate = false;
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                for (BasicBlock successor : basicBlock.getSuccessors()) {
                    outMap.get(basicBlock).addAll(inMap.get(successor));
                }

                //in = (out - def) + use
                HashSet<Value> newIn = new HashSet<>(outMap.get(basicBlock));
                basicBlock.getDef().forEach(newIn::remove);
                newIn.addAll(basicBlock.getUse());
                if (!newIn.equals(inMap.get(basicBlock))) {
                    inMap.put(basicBlock, newIn);
                    hasUpdate = true;
                }
            }
        } while (hasUpdate);

        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            basicBlock.setIn(new ArrayList<>(inMap.get(basicBlock)));
            basicBlock.setOut(new ArrayList<>(outMap.get(basicBlock)));
        }
    }
}
