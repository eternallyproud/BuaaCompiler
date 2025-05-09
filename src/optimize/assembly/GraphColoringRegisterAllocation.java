package optimize.assembly;

import backend.Register;
import config.Configuration;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.ConversionOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.optimize.Phi;
import frontend.ir.llvm.value.instruction.other.Call;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GraphColoringRegisterAllocation {
    public final static GraphColoringRegisterAllocation GRAPH_COLORING_REGISTER_ALLOCATION = new GraphColoringRegisterAllocation();

    private HashMap<Value, Register> valueToRegister;
    private HashMap<Register, Value> registerToValue;
    private final ArrayList<Register> registerPool;

    private GraphColoringRegisterAllocation() {
        registerPool = new ArrayList<>();
        for (Register register : Register.values()) {
            if (register.ordinal() >= Register.T0.ordinal() && register.ordinal() <= Register.T9.ordinal()) {
                registerPool.add(register);
            }
        }
        registerPool.add(Register.GP);
        registerPool.add(Register.FP);
    }

    public void optimize(Module module) {
        if (Configuration.GRAPH_COLORING_REGISTER_ALLOCATION_OPTIMIZATION) {
            Tools.printOpenInfo("图着色寄存器分配优化");

            for (Function function : module.getFunctions()) {
                optimize(function);
            }
        } else {
            Tools.printCloseInfo("图着色寄存器分配优化");
        }
    }

    private void optimize(Function function) {
        valueToRegister = new HashMap<>();
        registerToValue = new HashMap<>();

        allocRegister(function.getBasicBlocks().get(0));

        saveInfo(function);
    }

    private void allocRegister(BasicBlock basicBlock) {
        HashMap<Value, Instruction> lastUsed = new HashMap<>();

        //find out the last instruction that uses each value
        for (Instruction instruction : basicBlock.getInstructions()) {
            if (instruction instanceof Phi) {
                continue;
            }
            for (Value usedValue : instruction.getUsedValueList()) {
                lastUsed.put(usedValue, instruction);
            }
        }

        ArrayList<Value> neverUsedAfter = new ArrayList<>();
        ArrayList<Value> allocated = new ArrayList<>();

        for (Instruction instruction : basicBlock.getInstructions()) {
            //free register
            for (Value usedValue : instruction.getUsedValueList()) {
                if (lastUsed.get(usedValue) == instruction && !basicBlock.getOut().contains(usedValue) && valueToRegister.containsKey(usedValue)) {
                    registerToValue.remove(valueToRegister.get(usedValue));
                    neverUsedAfter.add(usedValue);
                }
            }

            //alloc register for instruction
            if (instruction.usable() && !(instruction instanceof ConversionOperation conversionOperation && conversionOperation.isZext())) {
                allocated.add(instruction);
                Register reg = allocRegister(instruction);
                if (reg != null) {
                    if (registerToValue.containsKey(reg)) {
                        valueToRegister.remove(registerToValue.get(reg));
                    }
                    registerToValue.put(reg, instruction);
                    valueToRegister.put(instruction, reg);
                }
            }
        }

        for (BasicBlock sonBasicBlock : basicBlock.getSon()) {
            HashMap<Register, Value> temp = new HashMap<>();

            for (Register register : registerToValue.keySet()) {
                if (!sonBasicBlock.getIn().contains(registerToValue.get(register))) {
                    temp.put(register, registerToValue.get(register));
                }
            }

            for (Register register : temp.keySet()) {
                registerToValue.remove(register);
            }

            allocRegister(sonBasicBlock);

            for (Register register : temp.keySet()) {
                registerToValue.put(register, temp.get(register));
            }
        }


        //free the registers which are allocated in the current basic block
        for (Value value : allocated) {
            if (valueToRegister.containsKey(value)) {
                registerToValue.remove(valueToRegister.get(value));
            }
        }

        //recover the registers in never used after
        for (Value value : neverUsedAfter) {
            if (valueToRegister.containsKey(value) && !allocated.contains(value)) {
                registerToValue.put(valueToRegister.get(value), value);
            }
        }
    }

    public Register allocRegister(Instruction instruction) {
        //if there exit a register that is not used, return it
        for (Register reg : registerPool) {
            if (!registerToValue.containsKey(reg)) {
                return reg;
            }
        }

        //else return the register containing the value with the lowest score
        int lowestScore = Integer.MAX_VALUE;
        Register allocatedRegister = null;
        for (Register reg : registerPool) {
            if (((Instruction) registerToValue.get(reg)).getScore() < lowestScore) {
                lowestScore = ((Instruction) registerToValue.get(reg)).getScore();
                allocatedRegister = reg;
            }
        }
        if (instruction.getScore() < lowestScore) {
            return null;
        }
        return allocatedRegister;
    }

    private void saveInfo(Function function) {
        function.setValueToRegister(valueToRegister);

        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            for (Instruction instruction : basicBlock.getInstructions()) {
                if (instruction instanceof Call call) {
                    HashSet<Register> activeRegisters = new HashSet<>();
                    //values that are used in future basic blocks
                    for (Value value : basicBlock.getOut()) {
                        if (valueToRegister.containsKey(value)) {
                            activeRegisters.add(valueToRegister.get(value));
                        }
                    }
                    //values that are used in the future instructions of the current basic block
                    ArrayList<Instruction> instructions = basicBlock.getInstructions();
                    for (int i = instructions.indexOf(call) + 1; i < instructions.size(); i++) {
                        for (Value value : instructions.get(i).getUsedValueList()) {
                            if (valueToRegister.containsKey(value)) {
                                activeRegisters.add(valueToRegister.get(value));
                            }
                        }
                    }
                    call.setActiveRegisters(activeRegisters);
                }
            }
        }
    }
}
