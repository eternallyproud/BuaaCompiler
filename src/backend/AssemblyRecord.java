package backend;

import backend.assembly.Assembly;
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

    @Override
    public String toString() {
        return dataSegment.stream().map(Object::toString).collect(Collectors.joining("\n\n")) + "\n\n" +
                textSegment.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
