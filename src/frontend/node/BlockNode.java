package frontend.node;

import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;

//<Block> ::= '{' { <BlockItem> } '}'
public class BlockNode extends Node {
    private final Token lbraceToken;
    private final ArrayList<BlockItemNode> blockItemNodes;
    private final Token rbraceToken;

    public BlockNode(Token lbraceToken, ArrayList<BlockItemNode> blockItemNodes, Token rbraceToken) {
        super(NodeType.BLOCK);
        this.lbraceToken = lbraceToken;
        this.blockItemNodes = blockItemNodes;
        this.rbraceToken = rbraceToken;
    }

    @Override
    public String toString() {
        return lbraceToken + Tools.arrayListToString(blockItemNodes) + rbraceToken + nodeType;
    }
}
