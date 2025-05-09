package frontend.parser.node;

import frontend.ir.llvm.value.Value;
import frontend.parser.node.stmt.ReturnStmtNode;
import frontend.parser.node.stmt.StmtNode;
import frontend.lexer.token.Token;

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

    public void checkReturnVoid() {
        if (stmtNode != null) {
            stmtNode.checkReturnVoid();
        }
    }

    public boolean isReturnStmtNode() {
        return stmtNode != null && stmtNode instanceof ReturnStmtNode;
    }

    public Token getReturnToken() {
        return ((ReturnStmtNode) stmtNode).getReturnToken();
    }

    @Override
    public void checkSemantic() {
        //<Decl>
        if (declNode != null) {
            declNode.checkSemantic();
        }
        //<Stmt>
        else {
            stmtNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        //<Decl>
        if (declNode != null) {
            declNode.buildIR();
        }
        //<Stmt>
        else {
            stmtNode.buildIR();
        }

        return super.buildIR();
    }

    @Override
    public String toString() {
        return Objects.toString(declNode, "") + Objects.toString(stmtNode, "");
    }
}
