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

    public Integer tryCalculateValue() {
        //<UnaryExp>
        if (mulExpNode == null) {
            return unaryExpNode.tryCalculateValue();
        }
        //<MulExp> ('*' | '/' | '%') <UnaryExp>
        else {
            Integer operand1 = mulExpNode.tryCalculateValue();
            Integer operand2 = unaryExpNode.tryCalculateValue();
            if (operand1 == null || operand2 == null) {
                return null;
            }
            //'*'
            if(mulToken.getType() == TokenType.MULT) {
                return operand1 * operand2;
            }
            //'/'
            else if(mulToken.getType() == TokenType.DIV) {
                return operand1 / operand2;
            }
            //'%'
            else {
                return operand1 % operand2;
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
        if(tryCalculateValue()!=null){
            return new Constant.Int(tryCalculateValue());
        }

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
