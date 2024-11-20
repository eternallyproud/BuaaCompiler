package frontend.parser.node;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.ValueTable;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.BinaryOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.other.Call;
import frontend.ir.llvm.value.instruction.other.ICmp;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.ir.llvm.value.type.ValueType;
import frontend.semantic.symbol.DataType;
import frontend.semantic.SymbolTable;
import frontend.lexer.token.Token;
import frontend.lexer.token.TokenType;

import java.util.ArrayList;
import java.util.Objects;

//<UnaryExp> ::= <PrimaryExp> | <Ident> '(' [ <FuncRParams> ] ')' | <UnaryOp> <UnaryExp>
public class UnaryExpNode extends Node {
    private final PrimaryExpNode primaryExpNode;
    private final Token identToken;
    private final Token lparenToken;
    private final FuncRParamsNode funcRParamsNode;
    private final Token rparenToken;
    private final UnaryOpNode unaryOpNode;
    private final UnaryExpNode unaryExpNode;

    public UnaryExpNode(PrimaryExpNode primaryExpNode, Token identToken, Token lparenToken, FuncRParamsNode funcRParamsNode, Token rparenToken, UnaryOpNode unaryOpNode, UnaryExpNode unaryExpNode) {
        super(NodeType.UNARY_EXP);
        this.primaryExpNode = primaryExpNode;
        this.identToken = identToken;
        this.lparenToken = lparenToken;
        this.funcRParamsNode = funcRParamsNode;
        this.rparenToken = rparenToken;
        this.unaryOpNode = unaryOpNode;
        this.unaryExpNode = unaryExpNode;
    }

    public DataType getDataType() {
        //<PrimaryExp>
        if (primaryExpNode != null) {
            return primaryExpNode.getDataType();
        }
        //<Ident> '(' [ <FuncRParams> ] ')'
        else if (identToken != null) {
            return SymbolTable.SYMBOL_TABLE.getFunctionReturnDataType(identToken).getNonConstantDataType().getCharToInt();
        }
        //<UnaryOp> <UnaryExp>
        else {
            return unaryExpNode.getDataType();
        }
    }

    public int calculateValue() {
        //<PrimaryExp>
        if (primaryExpNode != null) {
            return primaryExpNode.calculateValue();
        }
        //<UnaryOp> <UnaryExp>
        else {
            //'+'
            if (unaryOpNode.getOpTokenType() == TokenType.PLUS) {
                return unaryExpNode.calculateValue();
            }
            //'-'
            else if (unaryOpNode.getOpTokenType() == TokenType.MINU) {
                return -unaryExpNode.calculateValue();
            }
            //'!'
            else {
                return unaryExpNode.calculateValue() == 1 ? 0 : 1;
            }
        }
    }

    @Override
    public void checkSemantic() {
        //<PrimaryExp>
        if (primaryExpNode != null) {
            primaryExpNode.checkSemantic();
        }
        //<Ident> '(' [ <FuncRParams> ] ')'
        else if (identToken != null) {
            ArrayList<DataType> parameterDataTypes;
            if (funcRParamsNode != null) {
                funcRParamsNode.checkSemantic();
                parameterDataTypes = funcRParamsNode.getParameterDataTypes();
            } else {
                parameterDataTypes = new ArrayList<>();
            }
            SymbolTable.SYMBOL_TABLE.tackle(identToken, parameterDataTypes);
        }
        //<UnaryOp> <UnaryExp>
        else {
            unaryOpNode.checkSemantic();
            unaryExpNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        //<PrimaryExp>
        if (primaryExpNode != null) {
            return primaryExpNode.buildIR();
        }
        //<Ident> '(' [ <FuncRParams> ] ')'
        else if (identToken != null) {
            //function
            Function function = (Function) ValueTable.VALUE_TABLE.getFromGlobalScope(identToken.getContent());

            //parameters
            ArrayList<Value> params = funcRParamsNode != null ? funcRParamsNode.buildValue() : new ArrayList<>();

            //convert
            ArrayList<ValueType> parameterValueTypes = function.getParametersValueType();
            for (int i = 0; i < parameterValueTypes.size(); i++) {
                params.set(i, params.get(i).convertTo(parameterValueTypes.get(i)));
            }

            //call
            Call call = new Call(IRBuilder.IR_BUILDER.getLocalVarName(), function, params);
            IRBuilder.IR_BUILDER.addInstruction(call);

            return call.getValueType() == ScalarValueType.INT8 ? call.convertTo(ScalarValueType.INT32) : call;
        }
        //<UnaryOp> <UnaryExp>
        else {
            Value operand1 = new Constant.Int(0);
            Value operand2 = unaryExpNode.buildIR();

            switch (unaryOpNode.getOpTokenType()) {
                case PLUS -> {
                    return operand2;
                }
                case MINU -> {
                    Instruction instruction = new BinaryOperation(IRBuilder.IR_BUILDER.getLocalVarName(), "-", operand1, operand2);
                    IRBuilder.IR_BUILDER.addInstruction(instruction);
                    return instruction;
                }
                case NOT -> {
                    ICmp iCmp = new ICmp(IRBuilder.IR_BUILDER.getLocalVarName(), "==", operand2, new Constant.Int(0));
                    IRBuilder.IR_BUILDER.addInstruction(iCmp);
                    return iCmp.convertTo(ScalarValueType.INT32);
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return Objects.toString(primaryExpNode, "") +
                Objects.toString(identToken, "") +
                Objects.toString(lparenToken, "") +
                Objects.toString(funcRParamsNode, "") +
                Objects.toString(rparenToken, "") +
                Objects.toString(unaryOpNode, "") +
                Objects.toString(unaryExpNode, "") + nodeType;
    }
}
