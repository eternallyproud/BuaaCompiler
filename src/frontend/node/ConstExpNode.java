package frontend.node;

//<ConstExp> ::= <AddExp>
public class ConstExpNode extends Node {
    private final AddExpNode addExpNode;

    public ConstExpNode(AddExpNode addExpNode) {
        super(NodeType.CONST_EXP);
        this.addExpNode = addExpNode;
    }

    @Override
    public void checkSemantic(){
        addExpNode.checkSemantic();
    }

    @Override
    public String toString() {
        return "" + addExpNode + nodeType;
    }
}
