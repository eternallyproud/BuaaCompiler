package frontend.ir.llvm.value.global;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Label;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Parameter;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.ir.llvm.value.type.ValueType;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Function extends GlobalValue {
    private final ArrayList<Parameter> parameters;
    private final ArrayList<BasicBlock> basicBlocks;
    private HashMap<Value, Register> valueToRegister;

    public Function(String name, String cType) {
        super(ScalarValueType.getByCType(cType), name);
        parameters = new ArrayList<>();
        basicBlocks = new ArrayList<>();
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    public ArrayList<ValueType> getParametersValueType() {
        return parameters.stream()
                .map(Parameter::getValueType)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public void removeBasicBlock(BasicBlock basicBlock) {
        basicBlocks.remove(basicBlock);
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void setValueToRegister(HashMap<Value, Register> valueToRegister) {
        this.valueToRegister = valueToRegister;
    }

    public HashMap<Value, Register> getValueToRegister() {
        return valueToRegister;
    }

    public <C> HashMap<BasicBlock, C> getMap(Supplier<C> collectionSupplier) {
        HashMap<BasicBlock, C> map = new HashMap<>();

        for (BasicBlock basicBlock : basicBlocks) {
            map.put(basicBlock, collectionSupplier.get());
        }

        return map;
    }

    @Override
    public void buildAssembly() {
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(new Label(name.substring(1)));

        AssemblyBuilder.ASSEMBLY_BUILDER.enterFunction(valueToRegister);

        for (int i = 0; i < parameters.size(); i++) {
            if (i < 3) {
                //alloc a1-a3
                AssemblyBuilder.ASSEMBLY_BUILDER.allocRegisterForValue(parameters.get(i), Register.A0.getByOffset(i + 1));
            }
            AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValueIfNotMapped(parameters.get(i));
        }

        for (BasicBlock block : basicBlocks) {
            block.buildAssembly();
        }
    }

    @Override
    public String toString() {
        return "define dso_local " + valueType + " " + name + "(" +
                Tools.arrayListToString(parameters, ", ") + ") {\n" +
                Tools.arrayListToString(basicBlocks, "\n") + "\n}";
    }
}
