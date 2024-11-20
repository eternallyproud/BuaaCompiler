package frontend.parser.node;

import frontend.ir.llvm.value.Value;
import frontend.semantic.symbol.DataType;

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

    public int calculateValue() {
        return addExpNode.calculateValue();
    }

    @Override
    public void checkSemantic() {
        addExpNode.checkSemantic();
    }

    @Override
    public Value buildIR() {
        return addExpNode.buildIR();
    }

    @Override
    public String toString() {
        return "" + addExpNode + nodeType;
    }
}
