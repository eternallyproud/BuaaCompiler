package frontend.node;

import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;

//<VarDecl> ::= <BType> <VarDef> { ',' <VarDef> } ';'
public class VarDeclNode extends Node {
    private final BTypeNode bTypeNode;
    private final ArrayList<VarDefNode> varDefNodes;
    private final ArrayList<Token> commaTokens;
    private final Token semicnToken;

    public VarDeclNode(BTypeNode bTypeNode, ArrayList<VarDefNode> varDefNodes, ArrayList<Token> commaTokens, Token semicnToken) {
        super(NodeType.VAR_DECL);
        this.bTypeNode = bTypeNode;
        this.varDefNodes = varDefNodes;
        this.commaTokens = commaTokens;
        this.semicnToken = semicnToken;
    }

    @Override
    public String toString() {
        return bTypeNode + Tools.twoArrayListToString(varDefNodes, commaTokens) + semicnToken + nodeType;
    }

}
