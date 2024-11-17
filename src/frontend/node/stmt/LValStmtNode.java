package frontend.node.stmt;

import frontend.IRBuilder;
import frontend.ir.value.Value;
import frontend.ir.value.instruction.io.GetChar;
import frontend.ir.value.instruction.io.GetInt;
import frontend.ir.value.instruction.memory.Store;
import frontend.ir.value.type.ValueType;
import frontend.node.ExpNode;
import frontend.node.LValNode;
import frontend.token.Token;

import java.util.Objects;

//<Stmt> ::= <LVal> '=' <Exp> ';' | <LVal> '=' 'getint' '(' ')' ';' | <LVal> '=' 'getchar' '(' ')' ';'
public class LValStmtNode extends StmtNode {
    private final LValNode lValNode;
    private final Token assignToken;
    private final Token getintToken;
    private final Token getcharToken;
    private final Token lparenToken;
    private final Token rparenToken;
    private final ExpNode expNode;
    private final Token semicnToken;

    public LValStmtNode(LValNode lValNode, Token assignToken, Token getintToken, Token getcharToken, Token lparenToken, Token rparenToken, ExpNode expNode, Token semicnToken) {
        this.lValNode = lValNode;
        this.assignToken = assignToken;
        this.getintToken = getintToken;
        this.getcharToken = getcharToken;
        this.lparenToken = lparenToken;
        this.rparenToken = rparenToken;
        this.expNode = expNode;
        this.semicnToken = semicnToken;
    }

    @Override
    public void checkSemantic() {
        lValNode.checkSemantic();
        lValNode.tryAssignTo();
        if (expNode != null) {
            expNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        //lVal value
        Value lValValue = lValNode.buildIRForAssign();

        //expected value type
        ValueType expectedValueType = lValValue.getValueType().getPointerReferenceValueType();

        //<LVal> '=' <Exp> ';'
        if (expNode != null) {
            //convert
            Value assignValue = expNode.buildIR().convertTo(expectedValueType);

            //store
            Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(), assignValue, lValValue);
            IRBuilder.IR_BUILDER.addInstruction(store);
            return store;
        }
        //<LVal> '=' 'getint' '(' ')' ';'
        else if (getintToken != null) {
            //getint
            GetInt getInt = new GetInt(IRBuilder.IR_BUILDER.getLocalVarName());
            IRBuilder.IR_BUILDER.addInstruction(getInt);

            //convert
            Value assignor = getInt.convertTo(expectedValueType);

            //store
            Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(), assignor, lValValue);
            IRBuilder.IR_BUILDER.addInstruction(store);
            return store;
        }
        //<LVal> '=' 'getchar' '(' ')' ';'
        else {
            //getchar
            GetChar getChar = new GetChar(IRBuilder.IR_BUILDER.getLocalVarName());
            IRBuilder.IR_BUILDER.addInstruction(getChar);

            //convert
            Value assignor = getChar.convertTo(expectedValueType);

            //store
            Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(), assignor, lValValue);
            IRBuilder.IR_BUILDER.addInstruction(store);
            return store;
        }
    }

    @Override
    public String toString() {
        return "" + lValNode + assignToken +
                Objects.toString(getintToken, "") +
                Objects.toString(getcharToken, "") +
                Objects.toString(lparenToken, "") +
                Objects.toString(rparenToken, "") +
                Objects.toString(expNode, "") + semicnToken + super.toString();
    }
}
