package backend.assembly.directive;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GlobalDeclaration extends Directive {
    protected final String directive = "." + getClass().getSimpleName().toLowerCase();

    public static class FixedSizeDeclaration extends GlobalDeclaration {
        private final String name;
        private final int elementNumber;
        private final ArrayList<Integer> values;

        protected FixedSizeDeclaration(String name, int elementNumber, ArrayList<Integer> values) {
            this.name = name;
            this.elementNumber = elementNumber;
            this.values = values;
        }

        @Override
        public String toString() {
            int valueNum = (values == null) ? 0 : values.size();

            //section for explicit values
            String valueSection = (valueNum == 0)
                    ? ""
                    : "\n\t" + directive + " " + values.stream().map(String::valueOf).collect(Collectors.joining(", "));

            //section for zero-filling
            String zeroFillSection = (elementNumber > valueNum)
                    ? "\n\t" + directive + " 0: " + (elementNumber - valueNum)
                    : "";

            return name + ":" + valueSection + zeroFillSection;
        }
    }

    public static class Asciiz extends GlobalDeclaration {
        private final String name;
        private final String content;

        public Asciiz(String name, String content) {
            this.name = name;
            this.content = content.replace("\\00", "").replace("\\0A", "\\n");
        }

        @Override
        public String toString() {
            return name + ":\n\t" + directive + " \"" + content + "\"";
        }
    }

    public static class Word extends FixedSizeDeclaration {
        public Word(String name, int elementNumber, ArrayList<Integer> values) {
            super(name, elementNumber, values);
        }
    }
}
