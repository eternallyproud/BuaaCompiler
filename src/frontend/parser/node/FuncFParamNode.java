package frontend.parser.node;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.ValueTable;
import frontend.ir.llvm.value.Parameter;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.memory.Alloca;
import frontend.ir.llvm.value.instruction.memory.Store;
import frontend.ir.llvm.value.type.PointerValueType;
import frontend.ir.llvm.value.type.ValueType;
import frontend.semantic.symbol.CharArraySymbol;
import frontend.semantic.symbol.CharSymbol;
import frontend.semantic.symbol.IntArraySymbol;
import frontend.semantic.symbol.IntSymbol;
import frontend.semantic.symbol.NumericalSymbol;
import frontend.lexer.token.Token;

import java.util.Objects;

//<FuncFParam> ::= <BType> <Ident> [ '[' ']' ]
public class FuncFParamNode extends Node {
    private final BTypeNode bTypeNode;
    private final Token identToken;
    private final Token lbrackToken;
    private final Token rbrackToken;

    public FuncFParamNode(BTypeNode bTypeNode, Token identToken, Token lbrackToken, Token rbrackToken) {
        super(NodeType.FUNC_F_PARAM);
        this.bTypeNode = bTypeNode;
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.rbrackToken = rbrackToken;
    }

    public NumericalSymbol getSymbol() {
        return switch (lbrackToken == null ? bTypeNode.getBType() : bTypeNode.getBType().getRaisedDataType()) {
            case CHAR -> new CharSymbol(identToken.getContent());
            case INT -> new IntSymbol(identToken.getContent());
            case CHAR_ARRAY -> new CharArraySymbol(identToken.getContent());
            case INT_ARRAY -> new IntArraySymbol(identToken.getContent());
            default -> null;
        };
    }

    public Token getIdentToken() {
        return identToken;
    }

    @Override
    public Value buildIR() {
        //parameter
        ValueType valueType = lbrackToken == null ? bTypeNode.getValueType() : new PointerValueType(bTypeNode.getValueType());
        Parameter parameter = new Parameter(valueType, IRBuilder.IR_BUILDER.getParameterName());
        IRBuilder.IR_BUILDER.addParameter(parameter);

        //non-array
        if (lbrackToken == null) {
            //alloca
            Alloca alloca = new Alloca(IRBuilder.IR_BUILDER.getLocalVarName(), valueType);
            IRBuilder.IR_BUILDER.addInstruction(alloca);

            //store
            Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(), parameter, alloca);
            IRBuilder.IR_BUILDER.addInstruction(store);

            //add to value table
            ValueTable.VALUE_TABLE.add(identToken.getContent(), alloca);
        }
        //array
        else {
            //add to value table
            ValueTable.VALUE_TABLE.add(identToken.getContent(), parameter);
        }

        return null;
    }

    @Override
    public String toString() {
        return "" + bTypeNode + identToken +
                Objects.toString(lbrackToken, "") +
                Objects.toString(rbrackToken, "") +
                nodeType;
    }
}
