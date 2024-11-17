package frontend.node.stmt;

import frontend.IRBuilder;
import frontend.ir.ValueTable;
import frontend.ir.value.Value;
import frontend.ir.value.instruction.terminator.Br;
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
    public Value buildIR() {
        Br br = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), ValueTable.VALUE_TABLE.getLoopInfo().getLoopHead());
        IRBuilder.IR_BUILDER.addInstruction(br);

        return null;
    }

    @Override
    public String toString() {
        return "" + continueToken + semicnToken + super.toString();
    }
}
