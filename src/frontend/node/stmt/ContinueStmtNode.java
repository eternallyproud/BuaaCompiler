package frontend.node.stmt;

import frontend.token.Token;

//<ContinueStmt> ::= 'continue' ';'
public class ContinueStmtNode extends StmtNode {
    public Token continueToken;
    public Token semicnToken;

    public ContinueStmtNode(Token continueToken, Token semicnToken) {
        this.continueToken = continueToken;
        this.semicnToken = semicnToken;
    }

    @Override
    public String toString() {
        return "" + continueToken + semicnToken + super.toString();
    }
}
