package frontend.ir.llvm.value.instruction.other;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.instruction.ComputationalInstruction;
import backend.assembly.instruction.JumpInstruction;
import backend.assembly.instruction.Li;
import backend.assembly.instruction.MemoryInstruction;
import backend.assembly.instruction.MoveInstruction;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Parameter;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.type.ScalarValueType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

//<result> = call <ty> <function>(<function args>)
public class Call extends Operation {
    private HashSet<Register> activeRegisters = null;

    public Call(String name, Function function, ArrayList<Value> parameters) {
        super(function.getValueType(), name);
        addUsed(function);
        for (Value parameter : parameters) {
            addUsed(parameter);
        }
    }

    public void setActiveRegisters(HashSet<Register> activeRegisters) {
        this.activeRegisters = activeRegisters;
    }

    public Function getFunction() {
        return (Function) getUsedValueList().get(0);
    }

    @Override
    public String hash() {
        return getUsedValue(0).getName() + " " + getUsedValueList().subList(1, getUsedValueList().size()).stream().map(value -> getName()).collect(Collectors.joining(" "));
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        MemoryInstruction sw, lw;
        ArrayList<MemoryInstruction> swInstructions = new ArrayList<>(), lwInstructions = new ArrayList<>();
        int stackOffset = AssemblyBuilder.ASSEMBLY_BUILDER.getStackOffset();

        //store mapped registers
        ArrayList<Register> mappedRegisters = AssemblyBuilder.ASSEMBLY_BUILDER.getMappedRegisters();
        if (activeRegisters != null) {
            mappedRegisters = new ArrayList<>(activeRegisters);
            for (Register register : AssemblyBuilder.ASSEMBLY_BUILDER.getMappedRegisters()) {
                if (register == Register.A1 || register == Register.A2 || register == Register.A3) {
                    mappedRegisters.add(register);
                }
            }
        }
        for (int index = 0; index < mappedRegisters.size(); index++) {
            sw = new MemoryInstruction("sw", mappedRegisters.get(index), null, Register.SP, stackOffset - 4 * (index + 1));
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sw);
            swInstructions.add(sw);
        }

        //store ra
        sw = new MemoryInstruction("sw", Register.RA, null, Register.SP, stackOffset - 4 * mappedRegisters.size() - 4);
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sw);

        //store parameters
        List<Value> parameters = getUsedValueList().subList(1, getUsedValueList().size());
        for (int index = 0; index < parameters.size(); index++) {
            //first three parameters
            if (index < 3 && AssemblyBuilder.ASSEMBLY_BUILDER.valueToRegister()) {
                //rs: register to move the parameter from
                Register rs = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(parameters.get(index));

                //rt: register to move the parameter to
                Register rt = Register.getByIndex(Register.A0.ordinal() + index + 1);

                if (parameters.get(index) instanceof Constant) {
                    //li
                    Li li = new Li(rt, Integer.parseInt(parameters.get(index).getName()));
                    AssemblyBuilder.ASSEMBLY_BUILDER.addToText(li);

                    //has corresponding register
                } else if (rs != null) {
                    //parameter has just been stored on stack top
                    if (parameters.get(index) instanceof Parameter) {
                        lw = new MemoryInstruction("lw", rt, null, Register.SP, stackOffset - 4 * mappedRegisters.indexOf(rs) - 4);
                        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
                    }
                    //parameter is still in some register
                    else {
                        MoveInstruction move = new MoveInstruction(rt, rs);
                        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(move);
                    }

                    //no corresponding register
                } else {
                    //lw
                    lw = new MemoryInstruction("lw", rt, null, Register.SP, AssemblyBuilder.ASSEMBLY_BUILDER.getValueStackOffset(parameters.get(index)));
                    AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
                }
            }
            //parameters after the first three
            else {
                //rt: register to sw the parameter from
                Register rt = Register.K0;
                if (parameters.get(index) instanceof Constant) {
                    Li li = new Li(rt, Integer.parseInt(parameters.get(index).getName()));
                    AssemblyBuilder.ASSEMBLY_BUILDER.addToText(li);
                } else if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(parameters.get(index)) != null) {
                    Register rs = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(parameters.get(index));

                    //parameter has just been stored on stack top
                    if (parameters.get(index) instanceof Parameter) {
                        lw = new MemoryInstruction("lw", rt, null, Register.SP, stackOffset - 4 * mappedRegisters.indexOf(rs) - 4);
                        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
                    }
                    //parameter is still in some register
                    else {
                        rt = rs;
                    }
                } else {
                    lw = new MemoryInstruction("lw", rt, null, Register.SP, AssemblyBuilder.ASSEMBLY_BUILDER.getValueStackOffset(parameters.get(index)));
                    AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
                }
                sw = new MemoryInstruction("sw", rt, null, Register.SP, stackOffset - 4 * mappedRegisters.size() - 4 - 4 * index - 4);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sw);
            }
        }

        //renew sp -> sp + stackOffset - 4 * mappedRegisters.size() - 4
        ComputationalInstruction addiu = new ComputationalInstruction("addiu", Register.SP, Register.SP, stackOffset - 4 * mappedRegisters.size() - 4);
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(addiu);

        //jal
        JumpInstruction jal = new JumpInstruction("jal", getUsedValue(0).getName().substring(1), null);
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(jal);

        //restore ra
        lw = new MemoryInstruction("lw", Register.RA, null, Register.SP, 0);
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);

        //renew sp -> sp + -(stackOffset - 4 * mappedRegisters.size() - 4)
        addiu = new ComputationalInstruction("addiu", Register.SP, Register.SP, -(stackOffset - 4 * mappedRegisters.size() - 4));
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(addiu);

        //restore registers
        for (int index = 0; index < mappedRegisters.size(); ++index) {
            lw = new MemoryInstruction("lw", mappedRegisters.get(index), null, Register.SP, stackOffset - 4 * (index + 1));
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
            lwInstructions.add(lw);
        }

        //save swInstructions and lw Instructions
        jal.setSwInstructions(swInstructions);
        jal.setLwInstructions(lwInstructions);

        //store return value
        if (getUsedValue(0).getValueType() != ScalarValueType.VOID) {
            if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this) != null) {
                MoveInstruction move = new MoveInstruction(AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this), Register.V0);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(move);
            } else {
                Assembly.saveValueOnStackFromRegisterIfNotMapped(this, Register.V0);
            }
        }
    }

    @Override
    public String toString() {
        String prefix = getUsedValue(0).getValueType() == ScalarValueType.VOID ? "" : name + " = ";
        ArrayList<String> parameters = getUsedValueList().subList(1, getUsedValueList().size()).stream()
                .map(parameter -> parameter.getValueType() + " " + parameter.getName()).collect(Collectors.toCollection(ArrayList::new));
        return super.toString() + prefix + "call " + getUsedValue(0).getValueType() + " "
                + getUsedValue(0).getName() + "(" + String.join(", ", parameters) + ")";
    }
}
