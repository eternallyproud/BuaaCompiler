package frontend.node;

import frontend.IRBuilder;
import frontend.ir.value.Value;
import frontend.ir.value.instruction.BinaryOperation;
import frontend.ir.value.instruction.Instruction;
import frontend.symbol.DataType;
import frontend.token.Token;
import frontend.token.TokenType;

import java.util.Objects;

//<MulExp> ::=  <UnaryExp> | <MulExp> ('*' | '/' | '%') <UnaryExp>
public class MulExpNode extends Node {
    public final MulExpNode mulExpNode;
    public final Token mulToken;
    public final UnaryExpNode unaryExpNode;

    public MulExpNode(MulExpNode mulExpNode, Token mulToken, UnaryExpNode unaryExpNode) {
        super(NodeType.MUL_EXP);
        this.mulExpNode = mulExpNode;
        this.mulToken = mulToken;
        this.unaryExpNode = unaryExpNode;
    }

    public DataType getDataType() {
        return unaryExpNode.getDataType();
    }

    public int calculateValue() {
        //<UnaryExp>
        if (mulExpNode == null) {
            return unaryExpNode.calculateValue();
        }
        //<MulExp> ('*' | '/' | '%') <UnaryExp>
        else {
            //'*'
            if(mulToken.getType() == TokenType.MULT) {
                return mulExpNode.calculateValue() * unaryExpNode.calculateValue();
            }
            //'/'
            else if(mulToken.getType() == TokenType.DIV) {
                return mulExpNode.calculateValue() / unaryExpNode.calculateValue();
            }
            //'%'
            else {
                return mulExpNode.calculateValue() % unaryExpNode.calculateValue();
            }
        }
    }

    @Override
    public void checkSemantic() {
        //<UnaryExp>
        if (mulExpNode == null) {
            unaryExpNode.checkSemantic();
        }
        //<MulExp> ('*' | '/' | '%') <UnaryExp>
        else {
            mulExpNode.checkSemantic();
            unaryExpNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        Value operand1 = mulExpNode == null ? null : mulExpNode.buildIR();
        Value operand2 = unaryExpNode.buildIR();

        //<UnaryExp>
        if (mulExpNode == null) {
            return operand2;
        }
        //<MulExp> ('*' | '/' | '%') <UnaryExp>
        else {
            Instruction instruction = new BinaryOperation(IRBuilder.IR_BUILDER.getLocalVarName(), mulToken.getContent(), operand1, operand2);
            IRBuilder.IR_BUILDER.addInstruction(instruction);
            return instruction;
        }
    }

    @Override
    public String toString() {
        return Objects.toString(mulExpNode, "") +
                Objects.toString(mulToken, "") + unaryExpNode + nodeType;
    }
}
