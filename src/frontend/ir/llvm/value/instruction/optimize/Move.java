package frontend.ir.llvm.value.instruction.optimize;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.ScalarValueType;

public class Move extends Instruction {
    public Move(String name, Value toValue, Value fromValue) {
        super(ScalarValueType.VOID,name);
        addUsed(toValue);
        addUsed(fromValue);
    }

    public void setFromValue(Value fromValue){
        updateUsed(getUsedValue(1), fromValue);
    }

    @Override
    public String toString() {
        return super.toString() + "move " + getUsedValue(0).getName() + " " + getUsedValue(1).getName();
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        Register rt = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(getUsedValue(0));
        Register rs = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(getUsedValue(1));
        if(rs != null && rs == rt){
            return;
        }
        if(rt == null){
            rt = Register.K0;
        }

        //move fromValue to rt
        Assembly.moveScalarValueToRegister(getUsedValue(1), rt);

        //save value on stack from rt, if not mapped to any register
        Assembly.saveValueOnStackFromRegisterIfNotMapped(getUsedValue(0), rt);
    }
}
