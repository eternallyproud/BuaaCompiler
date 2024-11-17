package frontend.node;

import frontend.IRBuilder;
import frontend.ir.value.Value;
import frontend.ir.value.instruction.memory.Store;
import frontend.ir.value.type.ValueType;
import frontend.token.Token;

//<AssignStmt> ::= <LVal> '=' <Exp>
public class ForAssignNode extends Node {
    private final LValNode lValNode;
    private final Token assignToken;
    private final ExpNode expNode;

    public ForAssignNode(LValNode lValNode, Token assignToken, ExpNode expNode) {
        super(NodeType.FOR_ASSIGN);
        this.lValNode = lValNode;
        this.assignToken = assignToken;
        this.expNode = expNode;
    }

    @Override
    public void checkSemantic() {
        lValNode.checkSemantic();
        lValNode.tryAssignTo();
        expNode.checkSemantic();
    }

    @Override
    public Value buildIR() {
        //lVal value
        Value lValValue = lValNode.buildIRForAssign();

        //expected value type
        ValueType expectedValueType = lValValue.getValueType().getPointerReferenceValueType();

        //convert
        Value assignValue = expNode.buildIR().convertTo(expectedValueType);

        //store
        Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(), assignValue, lValValue);
        IRBuilder.IR_BUILDER.addInstruction(store);

        return store;
    }

    @Override
    public String toString() {
        return "" + lValNode + assignToken + expNode + nodeType;
    }
}
