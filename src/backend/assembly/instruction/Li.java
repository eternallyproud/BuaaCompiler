package backend.assembly.instruction;

import backend.Register;

public class Li extends Instruction {
    private final Integer immediate;

    public Li(Register rd, Integer number) {
        this.rd = rd;
        this.immediate = number;
    }

    @Override
    public String toString() {
        return super.toString() + "li " + rd + ", " + immediate;
    }
}
