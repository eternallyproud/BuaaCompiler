package frontend.node.stmt;

import frontend.node.ExpNode;
import frontend.symbol.SymbolTable;
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

    public Token getReturnToken() {
        return returnToken;
    }

    @Override
    public void checkReturnVoid() {
        if (expNode != null) {
            SymbolTable.SYMBOL_TABLE.tackle(returnToken);
        }
    }

    @Override
    public void checkSemantic() {
        if (expNode != null) {
            expNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return returnToken +
                Objects.toString(expNode, "") +
                semicnToken + super.toString();
    }
}
