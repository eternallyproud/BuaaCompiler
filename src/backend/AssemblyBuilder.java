package backend;

import backend.assembly.Assembly;
import error.ErrorHandler;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.Value;
import optimize.OptimizeManager;
import utils.InOut;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AssemblyBuilder {
    public final static AssemblyBuilder ASSEMBLY_BUILDER = new AssemblyBuilder();
    private Module module;
    private final AssemblyRecord assemblyRecord;
    private int currentStackOffset;
    private HashMap<Value, Integer> currentValueMap;
    private HashMap<Value, Register> valueToRegister;

    private AssemblyBuilder() {
        this.assemblyRecord = new AssemblyRecord();
    }

    public void init(Module module) {
        this.module = module;
    }

    public void buildAssembly() {
        if (!ErrorHandler.ERROR_HANDLER.isEmpty()) {
            Tools.printFailMessage("汇编代码生成");
        } else {
            Tools.printStartMessage("汇编代码生成");
            module.buildAssembly();
            Tools.printEndMessage("汇编代码生成");
        }
    }

    public void addToData(Assembly assembly) {
        assemblyRecord.addToData(assembly);
    }

    public void addToText(Assembly assembly) {
        assemblyRecord.addToText(assembly);
    }

    public void enterFunction(HashMap<Value, Register> valueToRegister) {
        currentStackOffset = 0;
        currentValueMap = new HashMap<>();
        this.valueToRegister = valueToRegister;
    }

    //alloc a word on stack top for value, if value is not mapped
    public int assignWordOnStackTopForValueIfNotMapped(Value value) {
        //get
        if (currentValueMap.containsKey(value)) {
            return currentValueMap.get(value);
        }
        //alloc
        return assignWordOnStackTopForValue(value);
    }

    //alloc a word on stack top for value
    public int assignWordOnStackTopForValue(Value value) {
        allocSpaceOnStackTop(1);
        mapValueOnStackTop(value);
        return currentStackOffset;
    }

    //alloc space on stack top for value
    public int allocSpaceOnStackTop(int wordSize) {
        return addStackOffset(-wordSize * 4);
    }

    public int addStackOffset(int delta) {
        currentStackOffset += delta;
        return currentStackOffset;
    }

    public int getStackOffset() {
        return currentStackOffset;
    }

    public void mapValueOnStackTop(Value value) {
        currentValueMap.put(value, currentStackOffset);
    }

    public void mapValueToExistedValueOnStack(Value newValue, Value value) {
        currentValueMap.put(newValue, getValueStackOffset(value));
    }

    public int getValueStackOffset(Value value) {
        return currentValueMap.get(value);
    }

    public boolean valueToRegister() {
        return valueToRegister != null;
    }

    public void allocRegisterForValue(Value value, Register register) {
        if (valueToRegister == null) {
            return;
        }
        valueToRegister.put(value, register);
    }

    public Register getRegisterOfValue(Value value) {
        if (valueToRegister == null) {
            return null;
        }
        return valueToRegister.get(value);
    }

    public ArrayList<Register> getMappedRegisters() {
        if (valueToRegister == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(new HashSet<>(valueToRegister.values()));
    }

    public void writeAssembly() {
        InOut.writeAssemblyResult(assemblyRecord.toString());
    }

    public void optimize(){
        OptimizeManager.OPTIMIZE_MANAGER.optimizeAssembly(assemblyRecord);
    }
}
