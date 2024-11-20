package frontend.parser.node.stmt;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.terminator.Ret;
import frontend.ir.llvm.value.type.ValueType;
import frontend.parser.node.ExpNode;
import frontend.semantic.SymbolTable;
import frontend.lexer.token.Token;

import java.util.Objects;

//<ReturnStmt> ::= 'return' [ <Exp> ] ';'
public class ReturnStmtNode extends StmtNode {
    private final Token returnToken;
    private final ExpNode expNode;
    private final Token semicnToken;

    public ReturnStmtNode(Token returnToken, ExpNode expNode, Token semicnToken) {
        this.returnToken = returnToken;
        this.expNode = expNode;
        this.semicnToken = semicnToken;
    }

    public Token getReturnToken() {
        return returnToken;
    }

    @Override
    public void checkReturnVoid() {
        if (expNode != null) {
            SymbolTable.SYMBOL_TABLE.tackle(returnToken);
        }
    }

    @Override
    public Value buildIR() {
        //return value type
        ValueType returnValueType = IRBuilder.IR_BUILDER.getCurrentFunction().getValueType();

        //return value
        Value returnValue = null;
        if (expNode != null) {
            returnValue = expNode.buildIR().convertTo(returnValueType);
        }

        //ret
        Instruction instruction = new Ret(IRBuilder.IR_BUILDER.getLocalVarName(), returnValue);
        IRBuilder.IR_BUILDER.addInstruction(instruction);

        return instruction;
    }

    @Override
    public void checkSemantic() {
        if (expNode != null) {
            expNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return returnToken +
                Objects.toString(expNode, "") +
                semicnToken + super.toString();
    }
}
