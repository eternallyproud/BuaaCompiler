package frontend.node;

//<Cond> ::= <LOrExp>
public class CondNode extends Node {
    private final LOrExpNode lOrExpNode;

    public CondNode(LOrExpNode lOrExpNode) {
        super(NodeType.COND);
        this.lOrExpNode = lOrExpNode;
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
