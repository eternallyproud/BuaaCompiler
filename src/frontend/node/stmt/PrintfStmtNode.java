package frontend.node.stmt;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import frontend.IRBuilder;
import frontend.ir.value.global.StringLiteral;
import frontend.ir.value.Value;
import frontend.ir.value.instruction.io.PutCh;
import frontend.ir.value.instruction.io.PutInt;
import frontend.ir.value.instruction.io.PutStr;
import frontend.ir.value.type.ScalarValueType;
import frontend.node.ExpNode;
import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;
import java.util.List;

//<PrintfStmt> ::= 'printf' '(' <StringConst> { ',' <Exp> } ')' ';'
public class PrintfStmtNode extends StmtNode {
    private final Token printfToken;
    private final Token lparenToken;
    private final Token strconToken;
    private final ArrayList<Token> commaTokens;
    private final ArrayList<ExpNode> expNodes;
    private final Token rparenToken;
    private final Token semicnToken;

    public PrintfStmtNode(Token printfToken, Token lparenToken, Token strconToken, ArrayList<Token> commaTokens, ArrayList<ExpNode> expNodes, Token rparenToken, Token semicnToken) {
        this.printfToken = printfToken;
        this.lparenToken = lparenToken;
        this.strconToken = strconToken;
        this.commaTokens = commaTokens;
        this.expNodes = expNodes;
        this.rparenToken = rparenToken;
        this.semicnToken = semicnToken;
    }

    private int getFormatPlaceholderNum() {
        return Tools.findSubstringOccurrences(strconToken.getContent(), "%d") +
                Tools.findSubstringOccurrences(strconToken.getContent(), "%c");
    }

    @Override
    public void checkSemantic() {
        for (ExpNode expNode : expNodes) {
            expNode.checkSemantic();
        }
        if (getFormatPlaceholderNum() != expNodes.size()) {
            ErrorHandler.ERROR_HANDLER.addError(new Error(strconToken.getLine(), ErrorType.MISMATCHED_PRINTF_FORMAT_ERROR));
        }
    }

    @Override
    public Value buildIR() {
        // values of expNodes
        List<Value> values = expNodes.stream().map(ExpNode::buildIR).toList();

        // format string
        String strcon = strconToken.getContent().substring(1, strconToken.getContent().length() - 1);

        int expCount = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strcon.length(); i++) {
            if (strcon.charAt(i) == '%') {
                if (!sb.isEmpty()) {
                    StringLiteral stringLiteral = new StringLiteral(IRBuilder.IR_BUILDER.getStringLiteralName(), sb.append("\\00").toString());
                    IRBuilder.IR_BUILDER.addStringLiteral(stringLiteral);
                    IRBuilder.IR_BUILDER.addInstruction(new PutStr(IRBuilder.IR_BUILDER.getLocalVarName(), stringLiteral));
                    sb.setLength(0);
                }
                if (strcon.charAt(i + 1) == 'd') {
                    //putint
                    PutInt putInt = new PutInt(IRBuilder.IR_BUILDER.getLocalVarName(), values.get(expCount++).convertTo(ScalarValueType.INT32));
                    IRBuilder.IR_BUILDER.addInstruction(putInt);
                } else if (strcon.charAt(i + 1) == 'c') {
                    //putch
                    PutCh putCh = new PutCh(IRBuilder.IR_BUILDER.getLocalVarName(), values.get(expCount++).convertTo(ScalarValueType.INT32));
                    IRBuilder.IR_BUILDER.addInstruction(putCh);
                }
                i++;
            } else if (strcon.charAt(i) == '\\' && strcon.charAt(i + 1) == 'n') {
                sb.append("\\0A");
                i++;
            } else {
                sb.append(strcon.charAt(i));
            }
        }
        if (!sb.isEmpty()) {
            StringLiteral stringLiteral = new StringLiteral(IRBuilder.IR_BUILDER.getStringLiteralName(), sb.append("\\00").toString());
            IRBuilder.IR_BUILDER.addStringLiteral(stringLiteral);
            IRBuilder.IR_BUILDER.addInstruction(new PutStr(IRBuilder.IR_BUILDER.getLocalVarName(), stringLiteral));
        }

        return super.buildIR();
    }

    @Override
    public String toString() {
        return "" + printfToken + lparenToken + strconToken +
                Tools.twoArrayListToString(commaTokens, expNodes) +
                rparenToken + semicnToken + super.toString();
    }

}
