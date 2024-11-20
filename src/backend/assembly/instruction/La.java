package backend.assembly.instruction;

import backend.Register;

public class La extends Instruction {
    private final String address;

    public La(Register rd, String address) {
        this.rd = rd;
        this.address = address;
    }

    @Override
    public String toString() {
        return super.toString() + "la " + rd + ", " + address;
    }
}
