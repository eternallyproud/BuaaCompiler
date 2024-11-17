package frontend.node;

import frontend.ir.value.Value;

import java.util.Objects;

//<Decl> ::= <ConstDecl> | <VarDecl>
public class DeclNode extends Node {
    private final ConstDeclNode constDeclNode;
    private final VarDeclNode varDeclNode;

    public DeclNode(ConstDeclNode constDeclNode, VarDeclNode varDeclNode) {
        super(NodeType.DECL);
        this.constDeclNode = constDeclNode;
        this.varDeclNode = varDeclNode;
    }

    @Override
    public void checkSemantic() {
        //<ConstDecl>
        if (constDeclNode != null) {
            constDeclNode.checkSemantic();
        }
        //<VarDecl>
        else {
            varDeclNode.checkSemantic();
        }
    }

    @Override
    public Value buildIR() {
        //<ConstDecl>
        if (constDeclNode != null) {
            constDeclNode.buildIR();
        }
        //<VarDecl>
        else {
            varDeclNode.buildIR();
        }

        return super.buildIR();
    }

    @Override
    public String toString() {
        return Objects.toString(constDeclNode, "") +
                Objects.toString(varDeclNode, "");
    }
}
