package frontend.parser.node;

import frontend.ir.llvm.value.BasicBlock;

//<Cond> ::= <LOrExp>
public class CondNode extends Node {
    private final LOrExpNode lOrExpNode;

    public CondNode(LOrExpNode lOrExpNode) {
        super(NodeType.COND);
        this.lOrExpNode = lOrExpNode;
    }

    public void buildIRForBranch(BasicBlock ifBasicBlock, BasicBlock elseBasicBlock){
        lOrExpNode.buildIRForBranch(ifBasicBlock, elseBasicBlock);
    }

    @Override
    public void checkSemantic(){
        lOrExpNode.checkSemantic();
    }

    @Override
    public String toString() {
        return "" + lOrExpNode + nodeType;
    }
}
