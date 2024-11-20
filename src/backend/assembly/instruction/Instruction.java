package backend.assembly.instruction;

import backend.Register;
import backend.assembly.Assembly;

public class Instruction extends Assembly {
    protected Register rs;
    protected Register rt;
    protected Register rd;

    @Override
    public String toString() {
        return "\t";
    }
}
