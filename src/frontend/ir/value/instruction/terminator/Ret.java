package frontend.ir.value.instruction.terminator;

import frontend.ir.value.Value;

public class Ret extends TerminatorInstruction {
    public Ret(String name, Value returnValue) {
        super(name);
        if (returnValue != null) {
            addUsed(returnValue);
        }
    }

    @Override
    public String toString() {
        String retString = getUsed(0) == null ? "void" : getUsed(0).getValueType() + " " + getUsed(0).getName();
        return super.toString() + "ret " + retString;
    }
}
