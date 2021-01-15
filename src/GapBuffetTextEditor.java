// this class is an implementation of TextEditor interface using gap buffer.
// Gap buffer is implemented as an array in this class
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class GapBuffetTextEditor implements TextEditor{

    private String pastedString;
    private String cache;
    private boolean modified;
    private char[] gapBuffer;
    private int leftPointer, rightPointer;
    private int misspellingWords;
    private HashSet<String> dic;

    public GapBuffetTextEditor(String document) {
        pastedString = "";
        cache = document;
        modified = false;

        // initialize the gap buffer
        gapBuffer = Arrays.copyOf(document.toCharArray(), Math.max(document.length()*2, 100));
        leftPointer = document.length();
        rightPointer = gapBuffer.length-1;

        // load the dictionary
        dic = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File("/usr/share/dict/words"));
            while(scanner.hasNextLine())
                dic.add(scanner.nextLine());
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // check the misspellingWords
        misspellingWords = 0;
        String[] words = document.split(" ");
        for(String s : words)
            if(!dic.contains(s))
                misspellingWords++;

//        System.out.println("initial misspelling is: " + misspellingWords);
//        System.out.println("initial document is: " + document);

    }

    // move the last element of the left side the index i
    private void moveCursor(int index) {
        // int length = leftPointer + gapBuffer.length-rightPointer-1;
        int toMove = (leftPointer-1) - index;
        for(int i=0; i<Math.abs(toMove); i++) {
            if(toMove > 0)
                gapBuffer[rightPointer--] = gapBuffer[--leftPointer];
            else
                gapBuffer[leftPointer++] = gapBuffer[++rightPointer];
        }
    }

    private void resize(int size) {
        String document = getText();
        gapBuffer = Arrays.copyOf(document.toCharArray(), size);
        leftPointer = document.length();
        rightPointer = gapBuffer.length-1;
    }

    @Override
    public int length() {
        return leftPointer + gapBuffer.length-rightPointer-1;
    }

    @Override
    public void cut(int i, int j) {
        // maybe some sanity check later
        // I assume the input is valid now
        copy(i, j);
        if(pastedString.length() == 0)
            return;
        // remove the section [i...j)
        modified = true;
        moveCursor(i-1);
        rightPointer += (j-i);

        // update the misspelling words
        String[] words = pastedString.split(" ");
        if(words.length == 0)
            return;

        int p1 = leftPointer-1;
        while(p1>=0 && gapBuffer[p1]!=' ')
            p1--;
        String prefix = new String(gapBuffer, p1+1, leftPointer-p1-1);

        int p2 = rightPointer+1;
        while(p2<gapBuffer.length && gapBuffer[p2]!=' ')
            p2++;
        String suffix = new String(gapBuffer, rightPointer+1, p2-1-rightPointer);

        if(pastedString.charAt(0) != ' ')
            words[0] = prefix + words[0];
        if(pastedString.charAt(pastedString.length()-1) != ' ')
            words[words.length-1] = words[words.length-1] + suffix;
        for(String s : words)
            if(!dic.contains(s))
                misspellingWords--;

        String newCombination = prefix + suffix;
        if(!dic.contains(newCombination))
            misspellingWords++;

        // probably resize the gap buffer
//        int used = leftPointer + gapBuffer.length-rightPointer-1;
//        int avail = gapBuffer.length - used;
//        if(avail > used*2)
//            resize(used*2);
    }

    @Override
    public void copy(int i, int j) {
        // maybe some sanity check later
        // I assume the input is valid now
        StringBuilder sb = new StringBuilder();

        if(j <= leftPointer)
            sb.append(gapBuffer, i, j-i);
        else if(i < leftPointer) {
            sb.append(gapBuffer, i, leftPointer - i);
            sb.append(gapBuffer, rightPointer+1, j-leftPointer);
        } else
            sb.append(gapBuffer, rightPointer-leftPointer+i+1, j-i);


        pastedString = sb.toString();
    }

    @Override
    public void paste(int i) {
        if(pastedString.length() == 0)
            return;
        else {
            modified = true;
            int length = leftPointer + gapBuffer.length-rightPointer-1;
            if(length + pastedString.length() > gapBuffer.length)
                resize((length + pastedString.length())*2);
            moveCursor(i-1);
            int oldLeft = leftPointer;
            for(char c : pastedString.toCharArray())
                gapBuffer[leftPointer++] = c;

            // update misspelling words
            String[] words = pastedString.split(" ");
            if(words.length == 0)
                return;

            int p1 = oldLeft-1;
            while(p1>=0 && gapBuffer[p1]!=' ')
                p1--;
            String prefix = new String(gapBuffer, p1+1, oldLeft-p1-1);

            int p2 = rightPointer+1;
            while(p2<gapBuffer.length && gapBuffer[p2]!=' ')
                p2++;
            String suffix = new String(gapBuffer, rightPointer+1, p2-1-rightPointer);

            if(pastedString.charAt(0) != ' ')
                words[0] = prefix + words[0];
            if(pastedString.charAt(pastedString.length()-1) != ' ')
                words[words.length-1] = words[words.length-1] + suffix;
            for(String s : words)
                if(!dic.contains(s))
                    misspellingWords++;

            String oldCombination = prefix + suffix;
            if(!dic.contains(oldCombination))
                misspellingWords--;
        }
    }

    @Override
    public String getText() {
        if(modified) {
            StringBuilder sb = new StringBuilder();
            int leftHalf = leftPointer;
            if(leftPointer > 0)
                sb.append(gapBuffer, 0, leftHalf);
            int rightHalf = gapBuffer.length-rightPointer-1;
            if(rightHalf > 0)
                sb.append(gapBuffer, rightPointer+1, rightHalf);
            cache = sb.toString();
            modified = false;
        }
        return cache;
    }

    @Override
    public int misspellings() {
        return misspellingWords;
    }

}
