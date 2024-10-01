package frontend.node.stmt;

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
    public String toString() {
        return "" + printfToken + lparenToken + strconToken +
                Tools.twoArrayListToString(commaTokens, expNodes) +
                rparenToken + semicnToken + super.toString();
    }

}
