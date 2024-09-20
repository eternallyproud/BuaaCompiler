import utils.InOut;


public class Compiler {
    public static void main(String[] args) {
        // 测试读写文件
        String fileContent = InOut.readFile();
        InOut.writeFile(fileContent);
    }
}
