package frontend.node.stmt;

import frontend.node.ExpNode;
import frontend.token.Token;

import java.util.Objects;

//<ExpStmt> ::= [ <Exp> ] ';'
public class ExpStmtNode extends StmtNode {
    private final ExpNode expNode;
    private final Token semicnToken;

    public ExpStmtNode(ExpNode expNode, Token semicnToken) {
        this.expNode = expNode;
        this.semicnToken = semicnToken;
    }

    @Override
    public String toString() {
        return Objects.toString(expNode, "") +
                semicnToken + super.toString();
    }
}
