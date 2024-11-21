package frontend.parser.node.stmt;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.ValueTable;
import frontend.ir.llvm.value.BasicBlock;
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
        //create a new basic block for continue
        BasicBlock continueBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
        IRBuilder.IR_BUILDER.addBasicBlock(continueBasicBlock);

        //add br to the current basic block
        Br br = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), continueBasicBlock);
        IRBuilder.IR_BUILDER.addInstruction(br);

        //continue is the only instruction in its basic block
        IRBuilder.IR_BUILDER.setCurrentBasicBlock(continueBasicBlock);
        br = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), ValueTable.VALUE_TABLE.getLoopInfo().getLoopHead());
        IRBuilder.IR_BUILDER.addInstruction(br);

        //create a new basic block for future instructions
        BasicBlock newBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
        IRBuilder.IR_BUILDER.addBasicBlock(newBasicBlock);
        IRBuilder.IR_BUILDER.setCurrentBasicBlock(newBasicBlock);

        return null;
    }

    @Override
    public String toString() {
        return "" + continueToken + semicnToken + super.toString();
    }
}
