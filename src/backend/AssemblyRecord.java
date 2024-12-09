package backend;

import backend.assembly.Assembly;
import backend.assembly.Comment;
import backend.assembly.directive.SegmentDeclaration;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AssemblyRecord {
    private final ArrayList<Assembly> dataSegment;
    private final ArrayList<Assembly> textSegment;

    public AssemblyRecord() {
        dataSegment = new ArrayList<>();
        textSegment = new ArrayList<>();
        dataSegment.add(SegmentDeclaration.DATA);
        textSegment.add(SegmentDeclaration.TEXT);
    }

    public void addToData(Assembly assembly) {
        dataSegment.add(assembly);
    }

    public void addToText(Assembly assembly) {
        textSegment.add(assembly);
    }

    public void removeFromText(Assembly assembly) {
        textSegment.remove(assembly);
    }

    public void replaceInText(Assembly oldAssembly, Assembly newAssembly) {
        int index = textSegment.indexOf(oldAssembly);
        textSegment.set(index, newAssembly);
    }

    public ArrayList<Assembly> getTextSegment() {
        return textSegment.stream()
                .filter(assembly -> !(assembly instanceof Comment))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        return dataSegment.stream().map(Object::toString).collect(Collectors.joining("\n\n")) + "\n\n" +
                textSegment.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
