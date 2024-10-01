package frontend.node;

import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;

//<ConstDecl> ::= 'const' <BType> <ConstDef> { ',' <ConstDef> } ';'
public class ConstDeclNode extends Node {
    private final Token constToken;
    private final BTypeNode bTypeNode;
    private final ArrayList<ConstDefNode> constDefNodes;
    private final ArrayList<Token> commaTokens;
    private final Token semicnToken;

    public ConstDeclNode(Token constToken, BTypeNode bTypeNode, ArrayList<ConstDefNode> constDefNodes, ArrayList<Token> commaTokens, Token semicnToken) {
        super(NodeType.CONST_DECL);
        this.constToken = constToken;
        this.bTypeNode = bTypeNode;
        this.constDefNodes = constDefNodes;
        this.commaTokens = commaTokens;
        this.semicnToken = semicnToken;
    }

    @Override
    public String toString() {
        return "" + constToken + bTypeNode + Tools.twoArrayListToString(constDefNodes, commaTokens) + semicnToken + nodeType;
    }
}
