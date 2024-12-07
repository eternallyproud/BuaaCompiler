package frontend.ir.llvm.value.instruction.optimize;

import backend.AssemblyBuilder;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Use;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.ValueType;

import java.util.ArrayList;
import java.util.stream.Collectors;

//<result> = phi <ty> [ <val0>, <label0>], ...
public class Phi extends Instruction {

    public Phi(ValueType valueType, String name) {
        super(valueType, name);
    }

    public void addUsedValueSet(Value value, BasicBlock label) {
        addUsed(value);
        addUsed(label);
    }

    public void updateBasicBlock(BasicBlock oldBasicBlock, BasicBlock newBasicBlock) {
        int oldIndex = getIndexOfUsedValue(oldBasicBlock);
        int newIndex = getIndexOfUsedValue(newBasicBlock);
        if (newIndex != -1) {
            removeUsedValue(oldBasicBlock);
            oldBasicBlock.removeUser(this);

            Value oldUsedValue = getUsedValue(oldIndex - 1);
            removeUsedValue(oldUsedValue);
            oldUsedValue.removeUser(this);

            return;
        }
        usedList.set(oldIndex, new Use(this, newBasicBlock));
        oldBasicBlock.removeUser(this);
        newBasicBlock.addUser(this);
    }

    public Value getValueWithBasicBlock(BasicBlock basicBlock) {
        int index = getIndexOfUsedValue(basicBlock);

        return index == -1 ? null : getUsedValue(index - 1);
    }

    @Override
    protected void updateUsed(Value oldUsed, Value newUsed) {
        if (oldUsed instanceof BasicBlock) {
            return;
        }
        super.updateUsed(oldUsed, newUsed);
    }

    @Override
    public String toString() {
        ArrayList<Value> basicBlocks = new ArrayList<>();

        for (int i = 1; i < usedList.size(); i += 2) {
            basicBlocks.add(getUsedValue(i));
        }

        String sb = basicBlocks.stream()
                .map(basicBlock -> "[ " + getUsedValue(basicBlocks.indexOf(basicBlock) * 2).getName() + ", %" + basicBlock.getName() + " ]")
                .collect(Collectors.joining(", "));

        return super.toString() + name + " = phi " + valueType + " " + sb;
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this) == null) {
            AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValueIfNotMapped(this);
        }
    }
}