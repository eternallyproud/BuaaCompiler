package frontend.node;

import frontend.symbol.SymbolTable;
import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;

//<Block> ::= '{' { <BlockItem> } '}'
public class BlockNode extends Node {
    private final Token lbraceToken;
    private final ArrayList<BlockItemNode> blockItemNodes;
    private final Token rbraceToken;
    private boolean isFuncBlock = false;

    public BlockNode(Token lbraceToken, ArrayList<BlockItemNode> blockItemNodes, Token rbraceToken) {
        super(NodeType.BLOCK);
        this.lbraceToken = lbraceToken;
        this.blockItemNodes = blockItemNodes;
        this.rbraceToken = rbraceToken;
    }

    public void isFuncBlock() {
        isFuncBlock = true;
    }

    public void checkReturnVoid() {
        for (BlockItemNode blockItemNode : blockItemNodes) {
            blockItemNode.checkReturnVoid();
        }
    }

    public Token getReturnToken() {
        if (!blockItemNodes.isEmpty()) {
            if (blockItemNodes.get(blockItemNodes.size() - 1).isReturnStmtNode()) {
                return blockItemNodes.get(blockItemNodes.size() - 1).getReturnToken();
            }
        }
        return rbraceToken;
    }

    @Override
    public void checkSemantic() {
        if (!isFuncBlock) {
            SymbolTable.SYMBOL_TABLE.addScope();
        }
        for (BlockItemNode blockItemNode : blockItemNodes) {
            blockItemNode.checkSemantic();
        }
        if (!isFuncBlock) {
            SymbolTable.SYMBOL_TABLE.removeScope();
        }
    }

    @Override
    public String toString() {
        return lbraceToken + Tools.arrayListToString(blockItemNodes) + rbraceToken + nodeType;
    }
}
