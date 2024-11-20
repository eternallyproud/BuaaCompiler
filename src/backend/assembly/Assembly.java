package backend.assembly;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.instruction.La;
import backend.assembly.instruction.Li;
import backend.assembly.instruction.MemoryInstruction;
import backend.assembly.instruction.MoveInstruction;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.GlobalVariable;

public class Assembly {
    //move value to register (value can only be int32 or int8)
    public static void moveScalarValueToRegister(Value value, Register register) {
        //constant
        if (value instanceof Constant) {
            //li
            Li li = new Li(register, Integer.parseInt(value.getName()));
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(li);
        }
        //has corresponding register
        else if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(value) != null) {
            //move
            MoveInstruction move = new MoveInstruction(register, AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(value));
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(move);
        }
        //no corresponding register
        else {
            Integer offset = AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValueIfNotMapped(value);

            //lw
            MemoryInstruction lw = new MemoryInstruction("lw", register, null, Register.SP, offset);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
        }
    }

    //if value is not mapped to any register, move it to register (value can only be int32 or int8)
    public static Register moveScalarValueToRegisterIfNotMapped(Value value, Register register) {
        if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(value) == null) {
            moveScalarValueToRegister(value, register);
            return register;
        } else {
            return AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(value);
        }
    }

    //if value is not mapped to any register, save if on stack from register
    public static void saveValueOnStackFromRegisterIfNotMapped(Value value, Register register) {
        if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(value) == null) {
            //sw
            int offset = AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValue(value);
            MemoryInstruction sw = new MemoryInstruction("sw", register, null, Register.SP, offset);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sw);
        }
    }

    public static Register movePointerValueToRegisterIfNotMapped(Value value, Register register) {
        if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(value) == null) {
            //global variable
            if (value instanceof GlobalVariable) {
                //la
                La la = new La(register, value.getName().substring(1));
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(la);
            } else {
                //lw
                int offset = AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValueIfNotMapped(value);
                MemoryInstruction lw = new MemoryInstruction("lw", register, null, Register.SP, offset);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
            }
            return register;
        } else {
            return AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(value);
        }
    }
}