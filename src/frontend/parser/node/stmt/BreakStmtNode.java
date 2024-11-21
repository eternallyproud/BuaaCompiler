package frontend.parser.node.stmt;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.ValueTable;
import frontend.ir.llvm.value.BasicBlock;
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
        //create a new basic block for break
        BasicBlock breakBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
        IRBuilder.IR_BUILDER.addBasicBlock(breakBasicBlock);

        //add br to the current basic block
        Br br = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), breakBasicBlock);
        IRBuilder.IR_BUILDER.addInstruction(br);

        //break is the only instruction in its basic block
        IRBuilder.IR_BUILDER.setCurrentBasicBlock(breakBasicBlock);
        br = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), ValueTable.VALUE_TABLE.getLoopInfo().getLoopEnd());
        IRBuilder.IR_BUILDER.addInstruction(br);

        //create a new basic block for future instructions
        BasicBlock newBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
        IRBuilder.IR_BUILDER.addBasicBlock(newBasicBlock);
        IRBuilder.IR_BUILDER.setCurrentBasicBlock(newBasicBlock);

        return null;
    }

    @Override
    public String toString() {
        return "" + breakToken + semicnToken + super.toString();
    }
}
