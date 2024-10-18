package frontend.node.stmt;

import frontend.node.Node;
import frontend.node.NodeType;

public class StmtNode extends Node {
    public StmtNode() {
        super(NodeType.STMT);
    }

    public void checkReturnVoid(){
    }

    @Override
    public String toString() {
        return nodeType.toString();
    }
}
