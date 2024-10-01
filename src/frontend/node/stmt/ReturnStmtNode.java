package frontend.node.stmt;

import frontend.node.ExpNode;
import frontend.token.Token;

import java.util.Objects;

//<ReturnStmt> ::= 'return' [ <Exp> ] ';'
public class ReturnStmtNode extends StmtNode {
    private final Token returnToken;
    private final ExpNode expNode;
    private final Token semicnToken;

    public ReturnStmtNode(Token returnToken, ExpNode expNode, Token semicnToken) {
        this.returnToken = returnToken;
        this.expNode = expNode;
        this.semicnToken = semicnToken;
    }

    @Override
    public String toString() {
        return returnToken +
                Objects.toString(expNode, "") +
                semicnToken + super.toString();
    }
}
