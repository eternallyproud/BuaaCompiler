package frontend.parser.node;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.other.ICmp;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.lexer.token.Token;

import java.util.Objects;

//<EqExp> ::= <RelExp> | <EqExp> ('==' | '!=') <RelExp>
public class EqExpNode extends Node {
    private final EqExpNode eqExpNode;
    private final Token eqToken;
    private final RelExpNode relExpNode;

    public EqExpNode(EqExpNode eqExpNode, Token eqToken, RelExpNode relExpNode) {
        super(NodeType.EQ_EXP);
        this.eqExpNode = eqExpNode;
        this.eqToken = eqToken;
        this.relExpNode = relExpNode;
    }

    @Override
    public void checkSemantic() {
        //<RelExp>
        if (eqExpNode == null) {
            relExpNode.checkSemantic();
        }
        //<EqExp> ('==' | '!=') <RelExp>
        else {
            eqExpNode.checkSemantic();
            relExpNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        //<RelExp>
        if (eqExpNode == null) {
            return relExpNode.buildIR();
        }
        //<EqExp> ('==' | '!=') <RelExp>
        else {
            Value operand1 = eqExpNode.buildIR().convertTo(ScalarValueType.INT32);
            Value operand2 = relExpNode.buildIR().convertTo(ScalarValueType.INT32);

            //icmp
            ICmp icmp = new ICmp(IRBuilder.IR_BUILDER.getLocalVarName(), eqToken.getContent(), operand1, operand2);
            IRBuilder.IR_BUILDER.addInstruction(icmp);
            return icmp;
        }
    }

    @Override
    public String toString() {
        return Objects.toString(eqExpNode, "") +
                Objects.toString(eqToken, "") + relExpNode + nodeType;
    }
}
