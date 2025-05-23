package frontend.parser.node.stmt;

import frontend.ir.llvm.value.Value;
import frontend.parser.node.BlockNode;

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
    public Value buildIR() {
        return blockNode.buildIR();
    }

    @Override
    public String toString() {
        return blockNode + super.toString();
    }
}
