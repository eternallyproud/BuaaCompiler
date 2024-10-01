package frontend.node;

import frontend.token.Token;

//<AssignStmt> ::= <LVal> '=' <Exp>
public class ForAssignNode extends Node {
    private final LValNode lvalNode;
    private final Token assignToken;
    private final ExpNode expNode;

    public ForAssignNode(LValNode lvalNode, Token assignToken, ExpNode expNode) {
        super(NodeType.FOR_ASSIGN);
        this.lvalNode = lvalNode;
        this.assignToken = assignToken;
        this.expNode = expNode;
    }

    @Override
    public String toString() {
        return "" + lvalNode + assignToken + expNode + nodeType;
    }
}
