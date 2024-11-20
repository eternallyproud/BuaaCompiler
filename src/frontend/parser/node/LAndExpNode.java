package frontend.parser.node;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.other.ICmp;
import frontend.ir.llvm.value.instruction.terminator.Br;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.lexer.token.Token;

import java.util.ArrayList;
import java.util.Objects;

//<LAndExp> ::= <EqExp> | <LAndExp> '&&' <EqExp>
public class LAndExpNode extends Node {
    public final LAndExpNode landExpNode;
    public final Token andToken;
    public final EqExpNode eqExpNode;

    public LAndExpNode(LAndExpNode landExpNode, Token andToken, EqExpNode eqExpNode) {
        super(NodeType.L_AND_EXP);
        this.landExpNode = landExpNode;
        this.andToken = andToken;
        this.eqExpNode = eqExpNode;
    }

    private void getAllEqExpNodes(ArrayList<EqExpNode> eqExpNodes) {
        if (landExpNode == null) {
            eqExpNodes.add(eqExpNode);
        } else {
            landExpNode.getAllEqExpNodes(eqExpNodes);
            eqExpNodes.add(eqExpNode);
        }
    }

    @Override
    public void checkSemantic() {
        //<EqExp>
        if (landExpNode == null) {
            eqExpNode.checkSemantic();
        }
        //<LAndExp> '&&' <EqExp>
        else {
            landExpNode.checkSemantic();
            eqExpNode.checkSemantic();
        }
    }

    public void buildIRForBranch(BasicBlock ifBasicBlock, BasicBlock elseBasicBlock) {
        /* this function only deals with the topmost LAndExp node */

        //all EqExp nodes
        ArrayList<EqExpNode> eqExpNodes = new ArrayList<>();
        getAllEqExpNodes(eqExpNodes);

        for (int i = 0; i < eqExpNodes.size(); i++) {
            //<EqExp>
            if (i == eqExpNodes.size() - 1) {
                //value of EqExp node
                Value value = eqExpNodes.get(i).buildIR();

                //convert to i1
                if (value.getValueType() != ScalarValueType.INT1) {
                    value = new ICmp(IRBuilder.IR_BUILDER.getLocalVarName(), "!=", value, new Constant.Int(0));
                    IRBuilder.IR_BUILDER.addInstruction((Instruction) value);
                }

                //br
                Br.ConditionalBr br = new Br.ConditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), value, ifBasicBlock, elseBasicBlock);
                IRBuilder.IR_BUILDER.addInstruction(br);
            }
            //<EqExp> {'||' <EqExp>}
            else {
                //basic block for next EqExp node
                BasicBlock basicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
                IRBuilder.IR_BUILDER.addBasicBlock(basicBlock);

                //value of EqExp node
                Value value = eqExpNodes.get(i).buildIR();

                //convert to i1
                if (value.getValueType() != ScalarValueType.INT1) {
                    value = new ICmp(IRBuilder.IR_BUILDER.getLocalVarName(), "!=", value, new Constant.Int(0));
                    IRBuilder.IR_BUILDER.addInstruction((Instruction) value);
                }

                //br
                Br.ConditionalBr br = new Br.ConditionalBr(IRBuilder.IR_BUILDER.getLocalVarName(), value, basicBlock, elseBasicBlock);
                IRBuilder.IR_BUILDER.addInstruction(br);

                //set basic block for next EqExp node
                IRBuilder.IR_BUILDER.setCurrentBasicBlock(basicBlock);
            }
        }
    }

    @Override
    public String toString() {
        return Objects.toString(landExpNode, "") +
                Objects.toString(andToken, "") + eqExpNode + nodeType;
    }
}
