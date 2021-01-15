import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class SimpleTextEditor implements TextEditor{

    private String pastedString;
    private String document;
    private HashSet<String> dic;

    public SimpleTextEditor(String document) {
        this.document = document;
        pastedString = "";
        dic = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File("/usr/share/dict/words"));
            while(scanner.hasNextLine())
                dic.add(scanner.nextLine());
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cut(int i, int j) {
        copy(i, j);
        document = document.substring(0, i) + document.substring(j);
    }

    @Override
    public void copy(int i, int j) {
        pastedString = document.substring(i, j);
    }

    @Override
    public void paste(int i) {
        document = document.substring(0, i) + pastedString + document.substring(i);
    }

    @Override
    public String getText() {
        return document;
    }

    @Override
    public int misspellings() {
        int ans = 0;
        for(String s : document.split(" ")) {
            if (!dic.contains(s))
                ans++;
        }
        return ans;
    }

    @Override
    public int length() {
        return document.length();
    }

}
