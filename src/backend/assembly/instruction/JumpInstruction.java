package backend.assembly.instruction;

import backend.Register;

import java.util.ArrayList;

public class JumpInstruction extends Instruction {
    private enum JumpOperator {
        J, JR, JAL, UNDEFINED;

        public static JumpOperator getByString(String str) {
            for (JumpOperator operator : values()) {
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

    private final JumpOperator jumpOperator;
    private final String target;

    private ArrayList<MemoryInstruction> swInstructions;
    private ArrayList<MemoryInstruction> lwInstructions;

    public JumpInstruction(String operator, String target, Register rs) {
        this.jumpOperator = JumpOperator.getByString(operator);
        this.target = target;
        this.rs = rs;
    }

    public String getOperator(){
        return jumpOperator.toString();
    }

    public String getTarget(){
        return target;
    }

    public void setSwInstructions(ArrayList<MemoryInstruction> swInstructions){
        this.swInstructions = swInstructions;
    }

    public ArrayList<MemoryInstruction> getSwInstructions() {
        return swInstructions;
    }

    public void setLwInstructions(ArrayList<MemoryInstruction> lwInstructions){
        this.lwInstructions = lwInstructions;
    }

    public ArrayList<MemoryInstruction> getLwInstructions(){
        return lwInstructions;
    }

    @Override
    public String toString() {
        if (target != null) {
            return super.toString() + jumpOperator + " " + target;
        } else {
            return super.toString() + jumpOperator + " " + rs;
        }
    }
}
