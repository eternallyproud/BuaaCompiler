package frontend.parser.node;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.ValueTable;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.GlobalVariable;
import frontend.ir.llvm.value.initializer.Initializer;
import frontend.ir.llvm.value.instruction.memory.Alloca;
import frontend.ir.llvm.value.instruction.memory.GetElementPtr;
import frontend.ir.llvm.value.instruction.memory.Store;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.ir.llvm.value.type.ValueType;
import frontend.semantic.symbol.DataType;
import frontend.semantic.SymbolTable;
import frontend.lexer.token.Token;

import java.util.ArrayList;
import java.util.Objects;

//<ConstDef> ::= <Ident> [ '[' <ConstExp> ']' ] '=' <ConstInitVal>
public class ConstDefNode extends Node {
    private final Token identToken;
    private final Token lbrackToken;
    private final ConstExpNode constExpNode;
    private final Token rbrackToken;
    private final Token assignToken;
    private final ConstInitValNode constInitValNode;
    private DataType bType;

    public ConstDefNode(Token identToken, Token lbrackToken, ConstExpNode constExpNode, Token rbrackToken, Token assignToken, ConstInitValNode constInitValNode) {
        super(NodeType.CONST_DEF);
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.constExpNode = constExpNode;
        this.rbrackToken = rbrackToken;
        this.assignToken = assignToken;
        this.constInitValNode = constInitValNode;
    }

    public void setBType(DataType bType) {
        this.bType = lbrackToken == null ? bType : bType.getRaisedDataType();
    }

    @Override
    public void checkSemantic() {
        if (constExpNode != null) {
            constExpNode.checkSemantic();
        }
        constInitValNode.checkSemantic();
        SymbolTable.SYMBOL_TABLE.tackle(identToken, bType);
    }

    @Override
    public Value buildIR() {
        //value type
        int elementNumber = constExpNode == null ? 1 : constExpNode.calculateValue();
        ValueType valueType = bType.getValueType(elementNumber);

        //initialize values
        ArrayList<Integer> values = constInitValNode.calculateValue(lbrackToken == null ? bType : bType.getReducedDataType());

        //non-array
        if (constExpNode == null) {
            //constant
            ValueTable.VALUE_TABLE.add(identToken.getContent(), new Constant((ScalarValueType) valueType, values.get(0)));
        }
        //array
        else {
            //global variable
            if (ValueTable.VALUE_TABLE.isGlobal()) {
                //initializer
                Initializer initializer = new Initializer(elementNumber, valueType, values);

                //global variable
                String name = IRBuilder.IR_BUILDER.getGlobalVarName();
                GlobalVariable globalVariable = new GlobalVariable(name, valueType, initializer, true);

                //add to ir builder
                IRBuilder.IR_BUILDER.addGlobalVariable(globalVariable);

                //add to value table
                ValueTable.VALUE_TABLE.add(identToken.getContent(), globalVariable);
            }
            //local variable
            else {
                //element type
                ScalarValueType elementValueType = (ScalarValueType) valueType.getArrayElementValueType();

                //initializer
                Initializer initializer = new Initializer(elementNumber, valueType, values);

                //alloca
                Alloca alloca = new Alloca(IRBuilder.IR_BUILDER.getLocalVarName(), valueType, initializer);
                IRBuilder.IR_BUILDER.addInstruction(alloca);

                int valueNum = values == null ? 0 : values.size();

                for (int i = 0; i < valueNum; i++) {
                    //getelementptr
                    GetElementPtr getElementPtr = new GetElementPtr(bType.getElementType(), IRBuilder.IR_BUILDER.getLocalVarName(), alloca, new Constant.Int(i));
                    IRBuilder.IR_BUILDER.addInstruction(getElementPtr);

                    //store
                    Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(), new Constant(elementValueType, values.get(i)), getElementPtr);
                    IRBuilder.IR_BUILDER.addInstruction(store);
                }

                //add '\0' to the end of char array
                if (elementValueType == ScalarValueType.INT8) {
                    for (int i = valueNum; i < elementNumber; i++) {
                        //getelementptr
                        GetElementPtr getElementPtr = new GetElementPtr(elementValueType, IRBuilder.IR_BUILDER.getLocalVarName(), alloca, new Constant.Int(i));
                        IRBuilder.IR_BUILDER.addInstruction(getElementPtr);

                        //store
                        Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(), new Constant(ScalarValueType.INT8, 0), getElementPtr);
                        IRBuilder.IR_BUILDER.addInstruction(store);
                    }
                }

                //add to value table
                ValueTable.VALUE_TABLE.add(identToken.getContent(), alloca);
            }
        }

        return super.buildIR();
    }

    @Override
    public String toString() {
        return identToken +
                Objects.toString(lbrackToken, "") +
                Objects.toString(constExpNode, "") +
                Objects.toString(rbrackToken, "") +
                assignToken + constInitValNode + nodeType;
    }
}
