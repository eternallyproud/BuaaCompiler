package frontend.node.stmt;

import frontend.symbol.SymbolTable;
import frontend.token.Token;

//<BreakStmt> ::= 'break' ';'
public class BreakStmtNode extends StmtNode {
    private final Token breakToken;
    private final Token semicnToken;

    public BreakStmtNode(Token breakToken, Token semicnToken) {
        this.breakToken = breakToken;
        this.semicnToken = semicnToken;
    }

    @Override
    public void checkSemantic() {
        SymbolTable.SYMBOL_TABLE.tackle(breakToken);
    }

    @Override
    public String toString() {
        return "" + breakToken + semicnToken + super.toString();
    }
}
