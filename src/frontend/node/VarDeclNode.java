package frontend.node;

import frontend.ir.value.Value;
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
    public void checkSemantic(){
        for (VarDefNode varDefNode : varDefNodes) {
            varDefNode.setBType(bTypeNode.getBType());
            varDefNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        for (VarDefNode varDefNode : varDefNodes) {
            varDefNode.buildIR();
        }

        return super.buildIR();
    }

    @Override
    public String toString() {
        return bTypeNode + Tools.twoArrayListToString(varDefNodes, commaTokens) + semicnToken + nodeType;
    }

}
