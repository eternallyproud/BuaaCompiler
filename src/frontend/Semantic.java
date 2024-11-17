package frontend;

import frontend.node.CompUnitNode;
import utils.Tools;

public class Semantic {
    private final CompUnitNode compUnitNode;

    public Semantic(CompUnitNode compUnitNode) {
        this.compUnitNode = compUnitNode;
    }

    public void doSemanticAnalysis() {
        Tools.printStartMessage("语义分析");
        compUnitNode.checkSemantic();
        Tools.printEndMessage("语义分析");
    }
}
