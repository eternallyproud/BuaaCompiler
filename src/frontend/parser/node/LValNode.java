package frontend.parser.node;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.ValueTable;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.GlobalVariable;
import frontend.ir.llvm.value.instruction.memory.GetElementPtr;
import frontend.ir.llvm.value.instruction.memory.Load;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.semantic.symbol.DataType;
import frontend.semantic.SymbolTable;
import frontend.lexer.token.Token;

import java.util.Objects;

//<LVal> ::= <Ident> [ '[' <Exp> ']' ]
public class LValNode extends Node {
    private final Token identToken;
    private final Token lbrackToken;
    private final ExpNode expNode;
    private final Token rbrackToken;
    private DataType dataType;

    public LValNode(Token identToken, Token lbrackToken, ExpNode expNode, Token rbrackToken) {
        super(NodeType.L_VAL);
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.expNode = expNode;
        this.rbrackToken = rbrackToken;
    }

    public DataType getDataType() {
        return SymbolTable.SYMBOL_TABLE.getNumericalDataType(identToken, lbrackToken != null).getCharToInt();
    }

    public void tryAssignTo() {
        SymbolTable.SYMBOL_TABLE.tackle(identToken, lbrackToken != null);
    }

    public Integer tryCalculateValue() {
        Value identValue = ValueTable.VALUE_TABLE.get(identToken.getContent());

        //non-array
        if (!dataType.isArray()) {
            //const
            if (dataType.isConst()) {
                return Integer.parseInt(identValue.getName());
            }
            //non-const
            else {
                return null;
            }
        }
        //array
        else {
            //return array pointer
            if (expNode == null) {
                return null;
            }
            //return array element
            else {
                if (dataType.isConst() && identValue instanceof GlobalVariable globalVariable) {
                    Integer exp = expNode.tryCalculateValue();
                    if(exp == null){
                        return null;
                    }
                    return globalVariable.getInitialValue(exp);
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public void checkSemantic() {
        SymbolTable.SYMBOL_TABLE.tackle(identToken);
        dataType = SymbolTable.SYMBOL_TABLE.getNumericalDataType(identToken, false);
        if (expNode != null) {
            expNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        Value identValue = ValueTable.VALUE_TABLE.get(identToken.getContent());

        //non-array
        if (!dataType.isArray()) {
            //const
            if (dataType.isConst()) {
                return identValue.convertTo(ScalarValueType.INT32);
            }
            //non-const
            else {
                //load
                Load load = new Load(IRBuilder.IR_BUILDER.getLocalVarName(), identValue);
                IRBuilder.IR_BUILDER.addInstruction(load);
                return load.convertTo(ScalarValueType.INT32);
            }
        }
        //array
        else {
            //return array pointer
            if (expNode == null) {
                //getelementptr
                GetElementPtr getElementPtr = new GetElementPtr(dataType.getElementType(), IRBuilder.IR_BUILDER.getLocalVarName(), identValue, new Constant.Int(0));
                IRBuilder.IR_BUILDER.addInstruction(getElementPtr);
                return getElementPtr;
            }
            //return array element
            else {
                //getelementptr
                GetElementPtr getElementPtr = new GetElementPtr(dataType.getElementType(), IRBuilder.IR_BUILDER.getLocalVarName(), identValue, expNode.buildIR());
                IRBuilder.IR_BUILDER.addInstruction(getElementPtr);

                //load
                Load load = new Load(IRBuilder.IR_BUILDER.getLocalVarName(), getElementPtr);
                IRBuilder.IR_BUILDER.addInstruction(load);
                return load.convertTo(ScalarValueType.INT32);
            }
        }
    }

    public Value buildIRForAssign() {
        Value identValue = ValueTable.VALUE_TABLE.get(identToken.getContent());

        //non-array
        if (!dataType.isArray()) {
            return identValue;
        }
        //array
        else {
            //getelementptr
            GetElementPtr getElementPtr = new GetElementPtr(dataType.getElementType(), IRBuilder.IR_BUILDER.getLocalVarName(), identValue, expNode.buildIR());
            IRBuilder.IR_BUILDER.addInstruction(getElementPtr);
            return getElementPtr;
        }
    }

    @Override
    public String toString() {
        return identToken +
                Objects.toString(lbrackToken, "") +
                Objects.toString(expNode, "") +
                Objects.toString(rbrackToken, "") + nodeType;
    }
}
