package frontend.node;

import frontend.node.stmt.StmtNode;

import java.util.Objects;

//<BlockItem> ::= <Decl> | <Stmt>
public class BlockItemNode extends Node {
    private final DeclNode declNode;
    private final StmtNode stmtNode;

    public BlockItemNode(DeclNode declNode, StmtNode stmtNode) {
        super(NodeType.BLOCK_ITEM);
        this.declNode = declNode;
        this.stmtNode = stmtNode;
    }

    @Override
    public String toString() {
        return Objects.toString(declNode, "") + Objects.toString(stmtNode, "");
    }
}
