package frontend.node;

import frontend.IRBuilder;
import frontend.ir.ValueTable;
import frontend.ir.value.Constant;
import frontend.ir.value.global.GlobalVariable;
import frontend.ir.value.Value;
import frontend.ir.value.initializer.Initializer;
import frontend.ir.value.instruction.memory.*;
import frontend.ir.value.type.ScalarValueType;
import frontend.ir.value.type.ValueType;
import frontend.symbol.DataType;
import frontend.symbol.SymbolTable;
import frontend.token.Token;

import java.util.ArrayList;
import java.util.Objects;

//<VarDef> ::= <Ident> [ '[' <ConstExp> ']' ] [ '=' <InitVal> ]
public class VarDefNode extends Node {
    private final Token identToken;
    private final Token lbrackToken;
    private final ConstExpNode constExpNode;
    private final Token rbrackToken;
    private final Token assignToken;
    private final InitValNode initValNode;
    private DataType bType;

    public VarDefNode(Token identToken, Token lbrackToken, ConstExpNode constExpNode, Token rbrackToken, Token assignToken, InitValNode initValNode) {
        super(NodeType.VAR_DEF);
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.constExpNode = constExpNode;
        this.rbrackToken = rbrackToken;
        this.assignToken = assignToken;
        this.initValNode = initValNode;
    }

    public void setBType(DataType bType) {
        this.bType = lbrackToken == null ? bType : bType.getRaisedDataType();
    }

    @Override
    public void checkSemantic() {
        if (constExpNode != null) {
            constExpNode.checkSemantic();
        }
        if (initValNode != null) {
            initValNode.checkSemantic();
        }
        SymbolTable.SYMBOL_TABLE.tackle(identToken, bType);
    }

    @Override
    public Value buildIR() {
        //variable type
        int elementNumber = constExpNode == null ? 1 : constExpNode.calculateValue();
        ValueType valueType = bType.getValueType(elementNumber);

        //expected value type
        DataType expectedType = lbrackToken == null ? bType : bType.getReducedDataType();

        //global variable
        if (ValueTable.VALUE_TABLE.isGlobal()) {
            //initializer
            ArrayList<Integer> values = initValNode == null ? null : initValNode.calculateValue(expectedType);
            Initializer initializer = new Initializer(elementNumber, valueType, values);

            //global variable
            String name = IRBuilder.IR_BUILDER.getGlobalVarName();
            GlobalVariable globalVariable = new GlobalVariable(name, valueType, initializer);

            //add to ir builder
            IRBuilder.IR_BUILDER.addGlobalVariable(globalVariable);

            //add to value table
            ValueTable.VALUE_TABLE.add(identToken.getContent(), globalVariable);
        }
        //local variable
        else {
            //values
            ArrayList<Value> values = initValNode == null ? null : initValNode.buildValue(expectedType);

            //element value type
            ValueType elementValueType = bType.getElementType();

            //array
            if (constExpNode != null) {
                //alloca
                Alloca alloca = new Alloca(IRBuilder.IR_BUILDER.getLocalVarName(), valueType);
                IRBuilder.IR_BUILDER.addInstruction(alloca);

                int valueNum = values == null ? 0 : values.size();

                for (int i = 0; i < valueNum; i++) {
                    //getelementptr
                    GetElementPtr getElementPtr = new GetElementPtr(elementValueType, IRBuilder.IR_BUILDER.getLocalVarName(), alloca, new Constant.Int(i));
                    IRBuilder.IR_BUILDER.addInstruction(getElementPtr);

                    //store
                    Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(), values.get(i), getElementPtr);
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
            //non-array
            else {
                //alloca
                Alloca alloca = new Alloca(IRBuilder.IR_BUILDER.getLocalVarName(), valueType);
                IRBuilder.IR_BUILDER.addInstruction(alloca);

                //store
                if (initValNode != null) {
                    Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(), values.get(0), alloca);
                    IRBuilder.IR_BUILDER.addInstruction(store);
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
                Objects.toString(assignToken, "") +
                Objects.toString(initValNode, "") +
                nodeType;
    }
}
