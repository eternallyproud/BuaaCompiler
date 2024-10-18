package frontend.node.stmt;

import frontend.node.CondNode;
import frontend.token.Token;

import java.util.Objects;

//<IfStmt> ::= 'if' '(' <Cond> ')' <Stmt> [ 'else' <Stmt> ]
public class IfStmtNode extends StmtNode {
    private final Token ifToken;
    private final Token lparenToken;
    private final CondNode condNode;
    private final Token rparenToken;
    private final StmtNode ifstmtNode;
    private final Token elseToken;
    private final StmtNode elseStmtNode;

    public IfStmtNode(Token ifToken, Token lparenToken, CondNode condNode, Token rparenToken, StmtNode ifstmtNode, Token elseToken, StmtNode elseStmtNode) {
        this.ifToken = ifToken;
        this.lparenToken = lparenToken;
        this.condNode = condNode;
        this.rparenToken = rparenToken;
        this.ifstmtNode = ifstmtNode;
        this.elseToken = elseToken;
        this.elseStmtNode = elseStmtNode;
    }

    @Override
    public void checkReturnVoid() {
        ifstmtNode.checkReturnVoid();
        if (elseStmtNode != null) {
            elseStmtNode.checkReturnVoid();
        }
    }

    @Override
    public void checkSemantic() {
        if (condNode != null) {
            condNode.checkSemantic();
        }
        ifstmtNode.checkSemantic();
        if (elseStmtNode != null) {
            elseStmtNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return "" + ifToken + lparenToken + condNode + rparenToken + ifstmtNode +
                Objects.toString(elseToken, "") +
                Objects.toString(elseStmtNode, "") + super.toString();
    }
}
