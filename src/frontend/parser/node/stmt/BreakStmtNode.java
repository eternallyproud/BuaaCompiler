package frontend.parser.node.stmt;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.ValueTable;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.terminator.Br;
import frontend.semantic.SymbolTable;
import frontend.lexer.token.Token;

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
