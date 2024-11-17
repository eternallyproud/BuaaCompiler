package frontend.ir.value.global;

import frontend.ir.value.BasicBlock;
import frontend.ir.value.Parameter;
import frontend.ir.value.type.ScalarValueType;
import frontend.ir.value.type.ValueType;
import utils.Tools;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Function extends GlobalValue {
    private final ArrayList<Parameter> parameters;
    private final ArrayList<BasicBlock> basicBlocks;

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

    @Override
    public String toString() {
        return "define dso_local " + valueType + " " + name + "(" +
                Tools.arrayListToString(parameters, ", ") + ") {\n" +
                Tools.arrayListToString(basicBlocks, "\n") + "\n}";
    }
}
