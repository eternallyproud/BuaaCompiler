package frontend.node.stmt;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import frontend.node.ExpNode;
import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;

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

    @Override
    public void checkSemantic() {
        for (ExpNode expNode : expNodes) {
            expNode.checkSemantic();
        }
        if (getFormatPlaceholderNum() != expNodes.size()) {
            ErrorHandler.ERROR_HANDLER.addError(new Error(strconToken.getLine(), ErrorType.MISMATCHED_PRINTF_FORMAT_ERROR));
        }
    }

    private int getFormatPlaceholderNum() {
        return Tools.findSubstringOccurrences(strconToken.getContent(), "%d") +
                Tools.findSubstringOccurrences(strconToken.getContent(), "%c");
    }

    @Override
    public String toString() {
        return "" + printfToken + lparenToken + strconToken +
                Tools.twoArrayListToString(commaTokens, expNodes) +
                rparenToken + semicnToken + super.toString();
    }

}
