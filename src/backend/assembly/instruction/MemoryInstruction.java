package backend.assembly.instruction;

import backend.Register;

public class MemoryInstruction extends Instruction {
    private enum MemoryOperator {
        LW, SW, UNDEFINED;

        public static MemoryOperator getByString(String str) {
            for (MemoryOperator operator : values()) {
                if (operator.name().toLowerCase().equals(str)) {
                    return operator;
                }
            }
            return UNDEFINED;
        }

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private final MemoryOperator memoryOperator;
    private final String label;
    private final Register base;
    private final Integer offset;

    public MemoryInstruction(String operator, Register rt, String label, Register base, Integer offset) {
        this.memoryOperator = MemoryOperator.getByString(operator);
        this.rt = rt;
        this.label = label;
        this.base = base;
        this.offset = offset;
    }

    @Override
    public String toString() {
        if (label != null) {
            return super.toString() + memoryOperator + " " + rt + ", " + label + "+" + offset;
        } else {
            return super.toString() + memoryOperator + " " + rt + ", " + offset + "(" + base + ")";
        }
    }
}
