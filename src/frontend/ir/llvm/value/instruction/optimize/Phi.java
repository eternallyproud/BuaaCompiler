package frontend.ir.llvm.value.instruction.optimize;

import backend.AssemblyBuilder;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.type.ValueType;

import java.util.ArrayList;
import java.util.stream.Collectors;

//<result> = phi <ty> [ <val0>, <label0>], ...
public class Phi extends Instruction {
    private final ArrayList<BasicBlock> basicBlocks;

    public Phi(ValueType valueType, String name) {
        super(valueType, name);
        basicBlocks = new ArrayList<>();
    }

    public void addUsedValueSet(Value value, BasicBlock label){
        addUsed(value);
        basicBlocks.add(label);
        value.addUser(this);
    }

    public void updateBasicBlock(BasicBlock oldBasicBlock, BasicBlock newBasicBlock){
        basicBlocks.set(basicBlocks.indexOf(oldBasicBlock), newBasicBlock);
    }

    public Value getValueWithBasicBlock(BasicBlock basicBlock){
        return !basicBlocks.contains(basicBlock) ? null : getUsedValue(basicBlocks.indexOf(basicBlock));
    }

    @Override
    public String toString() {
        String sb = basicBlocks.stream()
                .map(basicBlock -> "[ " + getUsedValue(basicBlocks.indexOf(basicBlock)).getName() + ", %" + basicBlock.getName() + " ]")
                .collect(Collectors.joining(", "));

        return super.toString() + name + " = phi " + valueType + " " + sb;
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        if(AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this) == null){
            AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValueIfNotMapped(this);
        }
    }
}
