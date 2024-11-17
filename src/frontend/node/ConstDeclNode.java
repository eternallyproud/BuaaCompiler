package frontend.node;

import frontend.ir.value.Value;
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
    public void checkSemantic() {
        for (ConstDefNode constDefNode : constDefNodes) {
            constDefNode.setBType(bTypeNode.getBType().getConstantDataType());
            constDefNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        for (ConstDefNode constDefNode : constDefNodes) {
            constDefNode.buildIR();
        }

        return super.buildIR();
    }

    @Override
    public String toString() {
        return "" + constToken + bTypeNode + Tools.twoArrayListToString(constDefNodes, commaTokens) + semicnToken + nodeType;
    }
}
