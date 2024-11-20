package frontend.ir.llvm;

import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.LoopInfo;
import frontend.ir.llvm.value.Value;

import java.util.HashMap;
import java.util.LinkedList;

public class ValueTable {
    public static final ValueTable VALUE_TABLE = new ValueTable();
    private final LinkedList<HashMap<String, Value>> valueTable;
    private final LinkedList<LoopInfo> loopInfoTable;

    private ValueTable() {
        valueTable = new LinkedList<>();
        loopInfoTable = new LinkedList<>();
    }

    public void push() {
        valueTable.push(new HashMap<>());
    }

    public void pushLoopInfo(BasicBlock loopHead, BasicBlock loopEnd) {
        loopInfoTable.push(new LoopInfo(loopHead, loopEnd));
    }

    public void pop() {
        valueTable.pop();
    }

    public void popLoopInfo() {
        loopInfoTable.pop();
    }

    public void add(String identifier, Value value) {
        assert valueTable.peek() != null;
        valueTable.peek().put(identifier, value);
    }

    public void addToGlobalScope(String identifier, Value value) {
        valueTable.get(valueTable.size() - 1).put(identifier, value);
    }

    public Value get(String identifier) {
        for (HashMap<String, Value> map : valueTable) {
            if (map.containsKey(identifier)) {
                return map.get(identifier);
            }
        }
        return null;
    }

    public Value getFromGlobalScope(String identifier) {
        return valueTable.get(valueTable.size() - 1).get(identifier);
    }

    public LoopInfo getLoopInfo() {
        return loopInfoTable.peek();
    }

    public boolean isGlobal() {
        return valueTable.size() == 1;
    }
}
