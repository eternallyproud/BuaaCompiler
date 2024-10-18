package frontend.node.stmt;

import frontend.node.BlockNode;

//<BlockItem> ::= <Block>
public class BlockStmtNode extends StmtNode {
    private final BlockNode blockNode;

    public BlockStmtNode(BlockNode blockNode) {
        this.blockNode = blockNode;
    }

    @Override
    public void checkReturnVoid() {
        blockNode.checkReturnVoid();
    }

    @Override
    public void checkSemantic() {
        blockNode.checkSemantic();
    }

    @Override
    public String toString() {
        return blockNode + super.toString();
    }
}
