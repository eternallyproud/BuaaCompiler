package frontend.node;

import frontend.IRBuilder;
import frontend.ir.value.Value;
import frontend.ir.value.instruction.BinaryOperation;
import frontend.ir.value.instruction.Instruction;
import frontend.symbol.DataType;
import frontend.token.Token;
import frontend.token.TokenType;

import java.util.Objects;

//<AddExp> ::=  <MulExp> | <AddExp> ('+' | '−') <MulExp>
public class AddExpNode extends Node {
    public final AddExpNode addExpNode;
    public final Token addToken;
    public final MulExpNode mulExpNode;

    public AddExpNode(AddExpNode addExpNode, Token addToken, MulExpNode mulExpNode) {
        super(NodeType.ADD_EXP);
        this.addExpNode = addExpNode;
        this.addToken = addToken;
        this.mulExpNode = mulExpNode;
    }

    public DataType getDataType() {
        return mulExpNode.getDataType();
    }

    public int calculateValue() {
        //<MulExp>
        if (addExpNode == null) {
            return mulExpNode.calculateValue();
        }
        //<AddExp> ('+' | '−') <MulExp>
        else {
            //'+'
            if (addToken.getType() == TokenType.PLUS) {
                return addExpNode.calculateValue() + mulExpNode.calculateValue();
            }
            //'-'
            else {
                return addExpNode.calculateValue() - mulExpNode.calculateValue();
            }
        }
    }

    @Override
    public void checkSemantic() {
        //<MulExp>
        if (addExpNode == null) {
            mulExpNode.checkSemantic();
        }
        //<AddExp> ('+' | '−') <MulExp>
        else {
            addExpNode.checkSemantic();
            mulExpNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        Value operand1 = addExpNode == null ? null : addExpNode.buildIR();
        Value operand2 = mulExpNode.buildIR();

        //<MulExp>
        if (addExpNode == null) {
            return operand2;
        }
        //<AddExp> ('+' | '−') <MulExp>
        Instruction instruction = new BinaryOperation(IRBuilder.IR_BUILDER.getLocalVarName(), addToken.getContent(), operand1, operand2);
        IRBuilder.IR_BUILDER.addInstruction(instruction);
        return instruction;
    }

    @Override
    public String toString() {
        return Objects.toString(addExpNode, "") +
                Objects.toString(addToken, "") + mulExpNode + nodeType;
    }
}
