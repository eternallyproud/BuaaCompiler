package frontend.ir.llvm.value;

public class LoopInfo {
    private final BasicBlock loopHead;
    private final BasicBlock loopEnd;

    public LoopInfo(BasicBlock loopHead, BasicBlock loopEnd) {
        this.loopHead = loopHead;
        this.loopEnd = loopEnd;
    }

    public BasicBlock getLoopHead() {
        return loopHead;
    }

    public BasicBlock getLoopEnd() {
        return loopEnd;
    }
}
