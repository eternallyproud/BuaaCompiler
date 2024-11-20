package frontend.parser.node.stmt;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.ValueTable;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.terminator.Br;
import frontend.semantic.SymbolTable;
import frontend.lexer.token.Token;

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
