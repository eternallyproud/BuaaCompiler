package backend.assembly.instruction;

import backend.Register;
import backend.assembly.Assembly;

public class Instruction extends Assembly {
    protected Register rs;
    protected Register rt;
    protected Register rd;

    public Register getRs(){
        return rs;
    }

    public Register getRt(){
        return rt;
    }

    public Register getRd(){
        return rd;
    }

    @Override
    public String toString() {
        return "\t";
    }
}
