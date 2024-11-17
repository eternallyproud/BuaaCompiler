package frontend.node;

import frontend.IRBuilder;
import frontend.ir.value.Value;
import frontend.ir.value.instruction.other.ICmp;
import frontend.ir.value.type.ScalarValueType;
import frontend.token.Token;

import java.util.Objects;

//<RelExp> ::= <AddExp> | <RelExp> ('<' | '>' | '<=' | '>=') <AddExp>
public class RelExpNode extends Node {
    private final RelExpNode relExpNode;
    private final Token relToken;
    private final AddExpNode addExpNode;

    public RelExpNode(RelExpNode relExpNode, Token relToken, AddExpNode addExpNode) {
        super(NodeType.REL_EXP);
        this.relExpNode = relExpNode;
        this.relToken = relToken;
        this.addExpNode = addExpNode;
    }

    @Override
    public void checkSemantic() {
        //<AddExp>
        if (relExpNode == null) {
            addExpNode.checkSemantic();
        }
        //<RelExp> ('<' | '>' | '<=' | '>=') <AddExp>
        else {
            relExpNode.checkSemantic();
            addExpNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        //<AddExp>
        if (relExpNode == null) {
            return addExpNode.buildIR();
        }
        //<RelExp> ('<' | '>' | '<=' | '>=') <AddExp>
        else {
            Value operand1 = relExpNode.buildIR().convertTo(ScalarValueType.INT32);
            Value operand2 = addExpNode.buildIR().convertTo(ScalarValueType.INT32);

            //icmp
            ICmp icmp = new ICmp(IRBuilder.IR_BUILDER.getLocalVarName(), relToken.getContent(), operand1, operand2);
            IRBuilder.IR_BUILDER.addInstruction(icmp);
            return icmp;
        }
    }

    @Override
    public String toString() {
        return Objects.toString(relExpNode, "") +
                Objects.toString(relToken, "") + addExpNode + nodeType;
    }
}
