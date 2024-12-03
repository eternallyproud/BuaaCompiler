package frontend.parser.node;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.BinaryOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.semantic.symbol.DataType;
import frontend.lexer.token.Token;
import frontend.lexer.token.TokenType;

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

    public Integer tryCalculateValue() {
        //<MulExp>
        if (addExpNode == null) {
            return mulExpNode.tryCalculateValue();
        }
        //<AddExp> ('+' | '−') <MulExp>
        else {
            Integer operand1 = addExpNode.tryCalculateValue();
            Integer operand2 = mulExpNode.tryCalculateValue();
            if (operand1 == null || operand2 == null) {
                return null;
            }
            //'+'
            if (addToken.getType() == TokenType.PLUS) {
                return operand1 + operand2;
            }
            //'-'
            else {
                return operand1 - operand2;
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
        if (tryCalculateValue() != null) {
            return new Constant.Int(tryCalculateValue());
        }

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
