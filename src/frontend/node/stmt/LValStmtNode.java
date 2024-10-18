package frontend.node.stmt;

import frontend.node.ExpNode;
import frontend.node.LValNode;
import frontend.token.Token;

import java.util.Objects;

//<Stmt> ::= <LVal> '=' <Exp> ';' | <LVal> '=' 'getint' '(' ')' ';' | <LVal> '=' 'getchar' '(' ')' ';'
public class LValStmtNode extends StmtNode {
    private final LValNode lValNode;
    private final Token assignToken;
    private final Token getintToken;
    private final Token getcharToken;
    private final Token lparenToken;
    private final Token rparenToken;
    private final ExpNode expNode;
    private final Token semicnToken;

    public LValStmtNode(LValNode lValNode, Token assignToken, Token getintToken, Token getcharToken, Token lparenToken, Token rparenToken, ExpNode expNode, Token semicnToken) {
        this.lValNode = lValNode;
        this.assignToken = assignToken;
        this.getintToken = getintToken;
        this.getcharToken = getcharToken;
        this.lparenToken = lparenToken;
        this.rparenToken = rparenToken;
        this.expNode = expNode;
        this.semicnToken = semicnToken;
    }

    @Override
    public void checkSemantic() {
        lValNode.checkSemantic();
        lValNode.tryAssignTo();
        if (expNode != null) {
            expNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return "" + lValNode + assignToken +
                Objects.toString(getintToken, "") +
                Objects.toString(getcharToken, "") +
                Objects.toString(lparenToken, "") +
                Objects.toString(rparenToken, "") +
                Objects.toString(expNode, "") + semicnToken + super.toString();
    }
}
