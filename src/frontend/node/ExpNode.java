package frontend.node;

import frontend.symbol.DataType;

//<Exp> ::= <AddExp>
public class ExpNode extends Node {
    private final AddExpNode addExpNode;

    public ExpNode(AddExpNode addExpNode) {
        super(NodeType.EXP);
        this.addExpNode = addExpNode;
    }

    public DataType getDataType() {
        return addExpNode.getDataType();
    }

    @Override
    public void checkSemantic() {
        addExpNode.checkSemantic();
    }

    @Override
    public String toString() {
        return "" + addExpNode + nodeType;
    }
}
