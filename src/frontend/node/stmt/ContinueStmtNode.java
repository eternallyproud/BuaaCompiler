package frontend.node.stmt;

import frontend.symbol.SymbolTable;
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
    public void checkSemantic() {
        SymbolTable.SYMBOL_TABLE.tackle(continueToken);
    }

    @Override
    public String toString() {
        return "" + continueToken + semicnToken + super.toString();
    }
}
