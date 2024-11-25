package frontend.ir.llvm.value.instruction.other;

import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.instruction.optimize.Phi;
import frontend.ir.llvm.value.type.ScalarValueType;

import java.util.ArrayList;

public class ParallelCopy extends Operation {
    private final ArrayList<Phi> phiList;
    private final ArrayList<Value> sourceValueList;
    public ParallelCopy(String name){
        super(ScalarValueType.VOID, name);
        phiList = new ArrayList<>();
        sourceValueList = new ArrayList<>();
    }

    public void addCopy(Phi phi, Value source){
        phiList.add(phi);
        sourceValueList.add(source);
    }

    @Override
    public String toString() {
        return super.toString() + "parallel copy is still in module!";
    }

    public ArrayList<Phi> getPhiList() {
        return phiList;
    }

    public ArrayList<Value> getSourceValueList() {
        return sourceValueList;
    }
}
