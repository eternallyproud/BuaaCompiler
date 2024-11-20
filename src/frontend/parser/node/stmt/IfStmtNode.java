package frontend.parser.node.stmt;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.terminator.Br;
import frontend.parser.node.CondNode;
import frontend.lexer.token.Token;

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
    public Value buildIR() {
        //if basic block
        BasicBlock ifBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
        IRBuilder.IR_BUILDER.addBasicBlock(ifBasicBlock);

        //if-else
        if(elseToken != null){
            //else basic block
            BasicBlock elseBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
            IRBuilder.IR_BUILDER.addBasicBlock(elseBasicBlock);

            //finish basic block
            BasicBlock finishBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
            IRBuilder.IR_BUILDER.addBasicBlock(finishBasicBlock);

            //buildIR for cond node
            condNode.buildIRForBranch(ifBasicBlock, elseBasicBlock);

            //buildIR for if-stmt node
            IRBuilder.IR_BUILDER.setCurrentBasicBlock(ifBasicBlock);
            ifstmtNode.buildIR();

            //br
            Br br1 = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), finishBasicBlock);
            IRBuilder.IR_BUILDER.addInstruction(br1);

            //buildIR for else-stmt node
            IRBuilder.IR_BUILDER.setCurrentBasicBlock(elseBasicBlock);
            elseStmtNode.buildIR();

            //br
            Br br2 = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), finishBasicBlock);
            IRBuilder.IR_BUILDER.addInstruction(br2);

            //set current basic block
            IRBuilder.IR_BUILDER.setCurrentBasicBlock(finishBasicBlock);
        }
        //if
        else{
            //finish basic block
            BasicBlock finishBasicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
            IRBuilder.IR_BUILDER.addBasicBlock(finishBasicBlock);

            //buildIR for cond node
            condNode.buildIRForBranch(ifBasicBlock, finishBasicBlock);

            //buildIR for if-stmt node
            IRBuilder.IR_BUILDER.setCurrentBasicBlock(ifBasicBlock);
            ifstmtNode.buildIR();

            //br
            Br br = new Br.UnconditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), finishBasicBlock);
            IRBuilder.IR_BUILDER.addInstruction(br);

            //set current basic block
            IRBuilder.IR_BUILDER.setCurrentBasicBlock(finishBasicBlock);
        }

        return null;
    }

    @Override
    public String toString() {
        return "" + ifToken + lparenToken + condNode + rparenToken + ifstmtNode +
                Objects.toString(elseToken, "") +
                Objects.toString(elseStmtNode, "") + super.toString();
    }
}
