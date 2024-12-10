package frontend.parser.node.stmt;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.ValueTable;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.terminator.Br;
import frontend.parser.node.CondNode;
import frontend.parser.node.ForAssignNode;
import frontend.semantic.SymbolTable;
import frontend.lexer.token.Token;

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

        loopDepth = SymbolTable.SYMBOL_TABLE.getLoopDepth();
    }

    @Override
    public Value buildIR() {
        //cond basic block
        BasicBlock condBasicBlock = null;
        if (condNode != null) {
            condBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName(), loopDepth + 1);
            IRBuilder.IR_BUILDER.addBasicBlock(condBasicBlock);
        }

        //loop body basic block
        BasicBlock loopBodyBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName(), loopDepth + 1);
        IRBuilder.IR_BUILDER.addBasicBlock(loopBodyBasicBlock);

        //finish basic block
        BasicBlock finishBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName(), loopDepth);
        IRBuilder.IR_BUILDER.addBasicBlock(finishBasicBlock);

        //update basic block
        BasicBlock updateBasicBlock = null;
        if (forAssignNode2 != null) {
            updateBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName(), loopDepth + 1);
            IRBuilder.IR_BUILDER.addBasicBlock(updateBasicBlock);
        }

        //entrance basic block
        BasicBlock entranceBasicBlock = condBasicBlock == null ? loopBodyBasicBlock : condBasicBlock;
        BasicBlock continueBasicBlock = updateBasicBlock == null ? entranceBasicBlock : updateBasicBlock;

        //push loop info
        ValueTable.VALUE_TABLE.pushLoopInfo(continueBasicBlock, finishBasicBlock);

        //[<ForAssign>]
        if (forAssignNode1 != null) {
            forAssignNode1.buildIR();
        }

        //br
        Br br1 = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), entranceBasicBlock);
        IRBuilder.IR_BUILDER.addInstruction(br1);

        //[<Cond>]
        if (condNode != null) {
            IRBuilder.IR_BUILDER.setCurrentBasicBlock(condBasicBlock);
            condNode.buildIRForBranch(loopBodyBasicBlock, finishBasicBlock);
        }

        //[<Stmt>]
        IRBuilder.IR_BUILDER.setCurrentBasicBlock(loopBodyBasicBlock);
        stmtNode.buildIR();

        //br
        Br br2 = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), continueBasicBlock);
        IRBuilder.IR_BUILDER.addInstruction(br2);

        //[<ForAssign>]
        if (forAssignNode2 != null) {
            IRBuilder.IR_BUILDER.setCurrentBasicBlock(updateBasicBlock);
            forAssignNode2.buildIR();

            Br br3 = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), entranceBasicBlock);
            IRBuilder.IR_BUILDER.addInstruction(br3);
        }

        //pop loop info
        ValueTable.VALUE_TABLE.popLoopInfo();

        IRBuilder.IR_BUILDER.setCurrentBasicBlock(finishBasicBlock);

        return null;
    }

    @Override
    public String toString() {
        return "" + forToken + lparenToken +
                Objects.toString(forAssignNode1, "") + semicnToken1 +
                Objects.toString(condNode, "") + semicnToken2 +
                Objects.toString(forAssignNode2, "") + rparenToken + stmtNode + super.toString();
    }
}
