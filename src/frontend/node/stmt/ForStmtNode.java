package frontend.node.stmt;

import frontend.node.CondNode;
import frontend.node.ForAssignNode;
import frontend.symbol.SymbolTable;
import frontend.token.Token;

import java.util.Objects;

//<ForStmt> ::= 'for' '(' [<ForAssign>] ';' [<Cond>] ';' [<ForAssign>] ')' <Stmt>
public class ForStmtNode extends StmtNode {
    public final Token forToken;
    public final Token lparenToken;
    public final ForAssignNode forAssignNode1;
    public final Token semicnToken1;
    public final CondNode condNode;
    public final Token semicnToken2;
    public final ForAssignNode forAssignNode2;
    public final Token rparenToken;
    public final StmtNode stmtNode;

    public ForStmtNode(Token forToken, Token lparenToken, ForAssignNode forAssignNode1, Token semicnToken1, CondNode condNode, Token semicnToken2, ForAssignNode forAssignNode2, Token rparenToken, StmtNode stmtNode) {
        this.forToken = forToken;
        this.lparenToken = lparenToken;
        this.forAssignNode1 = forAssignNode1;
        this.semicnToken1 = semicnToken1;
        this.condNode = condNode;
        this.semicnToken2 = semicnToken2;
        this.forAssignNode2 = forAssignNode2;
        this.rparenToken = rparenToken;
        this.stmtNode = stmtNode;
    }

    @Override
    public void checkReturnVoid() {
        stmtNode.checkReturnVoid();
    }

    @Override
    public void checkSemantic() {
        if (forAssignNode1 != null) {
            forAssignNode1.checkSemantic();
        }
        if (condNode != null) {
            condNode.checkSemantic();
        }
        if (forAssignNode2 != null) {
            forAssignNode2.checkSemantic();
        }
        SymbolTable.SYMBOL_TABLE.addLoopDepth();
        stmtNode.checkSemantic();
        SymbolTable.SYMBOL_TABLE.reduceLoopDepth();
    }

    @Override
    public String toString() {
        return "" + forToken + lparenToken +
                Objects.toString(forAssignNode1, "") + semicnToken1 +
                Objects.toString(condNode, "") + semicnToken2 +
                Objects.toString(forAssignNode2, "") + rparenToken + stmtNode + super.toString();
    }
}
