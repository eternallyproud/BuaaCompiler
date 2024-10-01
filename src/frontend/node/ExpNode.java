package frontend.node;

//<Exp> ::= <AddExp>
public class ExpNode extends Node {
    private final AddExpNode addExpNode;

    public ExpNode(AddExpNode addExpNode) {
        super(NodeType.EXP);
        this.addExpNode = addExpNode;
    }

    @Override
    public String toString() {
        return "" + addExpNode + nodeType;
    }
}
