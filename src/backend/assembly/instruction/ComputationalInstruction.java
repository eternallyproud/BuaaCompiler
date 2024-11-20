package backend.assembly.instruction;

import backend.Register;

public class ComputationalInstruction extends Instruction {
    private enum ComputationalOperator {
        //R-R
        ADD, ADDU, SUB, SUBU, MUL, SEQ, SNE, SGT, SGE, SLT, SLE, SLL,
        //R-R mult/div
        MULT, DIV,
        //R-I
        ADDI, ANDI,
        UNDEFINED;

        public static ComputationalOperator getByString(String str) {
            for (ComputationalOperator operator : values()) {
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

    private final ComputationalOperator operator;
    private final Integer immediate;

    //R-R
    public ComputationalInstruction(String operator, Register rd, Register rs, Register rt) {
        this.operator = ComputationalOperator.getByString(operator);
        this.immediate = null;
        this.rs = rs;
        this.rt = rt;
        this.rd = rd;
    }

    //R-R mult/div
    public ComputationalInstruction(String operator, Register rs, Register rt) {
        this.operator = ComputationalOperator.getByString(operator);
        this.immediate = null;
        this.rs = rs;
        this.rt = rt;
        this.rd = null;
    }

    //R-I
    public ComputationalInstruction(String operator, Register rt, Register rs, int immediate) {
        this.operator = ComputationalOperator.getByString(operator);
        this.immediate = immediate;
        this.rs = rs;
        this.rt = rt;
        this.rd = null;
    }

    @Override
    public String toString() {
        if (immediate == null) {
            if (rd == null) {
                return super.toString() + operator + " " + rs + ", " + rt;
            } else {
                return super.toString() + operator + " " + rd + ", " + rs + ", " + rt;
            }
        } else {
            return super.toString() + operator + " " + rt + ", " + rs + ", " + immediate;
        }
    }
}
