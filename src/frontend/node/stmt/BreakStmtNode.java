package frontend.node.stmt;

import frontend.IRBuilder;
import frontend.ir.ValueTable;
import frontend.ir.value.Value;
import frontend.ir.value.instruction.terminator.Br;
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
    public Value buildIR() {
        Br br = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), ValueTable.VALUE_TABLE.getLoopInfo().getLoopEnd());
        IRBuilder.IR_BUILDER.addInstruction(br);

        return null;
    }

    @Override
    public String toString() {
        return "" + breakToken + semicnToken + super.toString();
    }
}
