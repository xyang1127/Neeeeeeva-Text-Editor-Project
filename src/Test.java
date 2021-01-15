import java.util.Collections;
import java.util.Random;

public class Test {

    public static void main(String[] args) {

        System.out.println("--- Test Correctness ---");
        testCorrectness();

        System.out.println("\n--- Test Basic Operation Speed ---");
        String document = String.join("", Collections.nCopies(100, "Hello Friend ")); // create a string made up of n copies of string s
        System.out.println("<case for SMALL document:>");
        System.out.println("\n Text Editor with Gap Buffer Implementation: ");
        testBasicaOperationSpeed(new GapBuffetTextEditor(document), 100);
        System.out.println("\n Text Editor with Brute Force Implementation: ");
        testBasicaOperationSpeed(new SimpleTextEditor(document), 100);

        document = String.join("", Collections.nCopies(100000, "Hello Friend "));
        System.out.println("\n<case for LARGE document:>");
        System.out.println("\n Text Editor with Gap Buffer Implementation: ");
        testBasicaOperationSpeed(new GapBuffetTextEditor(document), 100);
        System.out.println("\n Text Editor with Brute Force Implementation: ");
        testBasicaOperationSpeed(new SimpleTextEditor(document), 100);

        document = String.join("", Collections.nCopies(100000, "Neeva is awesome!"));
        System.out.println("\n--- Test Random Operations (All operation has the same possibility to be chosen) On a Large File ---");
        System.out.println("\n Text Editor with Gap Buffer Implementation: ");
        testRandomOperation1(new GapBuffetTextEditor(document),100);
        testRandomOperation1(new GapBuffetTextEditor(document),10000);
        System.out.println("\n Text Editor with Brute Force Implementation: ");
        testRandomOperation1(new SimpleTextEditor(document),100);
        testRandomOperation1(new SimpleTextEditor(document), 10000);

        System.out.println("\n--- Test Random Operations (Heavy Read and Check) On a Large File ---");
        System.out.println("\n Text Editor with Gap Buffer Implementation: ");
        testRandomOperation2(new GapBuffetTextEditor(document),100);
        testRandomOperation2(new GapBuffetTextEditor(document),10000);
        System.out.println("\n Text Editor with Brute Force Implementation: ");
        testRandomOperation2(new SimpleTextEditor(document),100);
        testRandomOperation2(new SimpleTextEditor(document), 10000);

        System.out.println("\n--- Test Random Operations (Heavy Modify Operations) On a Large File ---");
        System.out.println("\n Text Editor with Gap Buffer Implementation: ");
        testRandomOperation3(new GapBuffetTextEditor(document),100);
        testRandomOperation3(new GapBuffetTextEditor(document),10000);
        System.out.println("\n Text Editor with Brute Force Implementation: ");
        testRandomOperation3(new SimpleTextEditor(document),100);
        testRandomOperation3(new SimpleTextEditor(document), 10000);

        System.out.println("\n--- Test Corner Cases ---");
        cornerCase1();

    }

    private static void cornerCase1() {

        String document = "ABC";
        GapBuffetTextEditor GBTE = new GapBuffetTextEditor(document);
        SimpleTextEditor STE = new SimpleTextEditor(document);

        GBTE.cut(0,2);
        GBTE.paste(0);

        STE.cut(0,2);
        STE.paste(0);


        boolean success = (GBTE.getText().equals(STE.getText())) && GBTE.misspellings()==STE.misspellings();
        System.out.println("\ncornor case 1: " + (success ? "SUCCESS" : "FAIL"));

    }

    private static void testCorrectness() {

        String[] cases = new String[]{"Hello Friend!", "ABCDEDG", " 1 23AAD J", "Neeva is awesome!"};

        for(String document : cases) {
            System.out.println("\ncase for: " + document);
            GapBuffetTextEditor GBTE = new GapBuffetTextEditor(document);
            SimpleTextEditor STE = new SimpleTextEditor(document);

            // test cut and paste
            GBTE.cut(1, 3);
            GBTE.paste(2);

            STE.cut(1, 3);
            STE.paste(2);
            System.out.println("cut paste operation is correct: " +  (GBTE.getText().equals(STE.getText())&&GBTE.misspellings()==STE.misspellings()) );

            // test copy and paste
            GBTE.copy(1, 3);
            GBTE.paste(2);

            STE.copy(1, 3);
            STE.paste(2);
            System.out.println("copy paste operation is correct: " +  (GBTE.getText().equals(STE.getText())&&GBTE.misspellings()==STE.misspellings()) );

            // test getText
            System.out.println("getText operation is correct: " + GBTE.getText().equals(STE.getText()));

            // test misspellings
            System.out.println("misspellings operation is correct: " + (GBTE.misspellings()==STE.misspellings()) );
        }


    }

    // generate random operations
    private static void testBasicaOperationSpeed(TextEditor TE, int N) {

        long startTime, endTime, timeElapsed;
        startTime = System.nanoTime();

        // test copy() and paste()
        for(int i=0; i<N; i++)
            if(i % 2 == 0) {
                TE.cut(1, 3);
            } else {
                TE.paste(2);
            }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println(N + " cut paste operations takes: " + (double)timeElapsed/1000000 + " ms");

        // test copy() and paste()
        for(int i=0; i<N; i++)
            if(i % 2 == 0) {
                TE.copy(1, 3);
            } else {
                TE.paste(2);
            }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println(N + " copy paste operations takes: " + (double)timeElapsed/1000000 + " ms");

        // test getText()
        for(int i=0; i<N; i++) {
            TE.getText();
        }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println(N + " getText operations takes: " + (double)timeElapsed/1000000 + " ms");

        for(int i=0; i<N; i++) {
            TE.misspellings();
        }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println(N + " misspellings operations takes: " + (double)timeElapsed/1000000 + " ms");

    }

    private static void testRandomOperation1(TextEditor TE, int N) {

        // 0: cut(i, j);
        // 1: copy(i, j);
        // 2: paste(i);
        // 3: getText();
        // 4: misspellings();
        long startTime, endTime, timeElapsed;
        startTime = System.nanoTime();
        Random random = new Random();
        for(int i=0; i<N; i++) {
            int op = random.nextInt(5);
            int l, r;
            switch (op) {
                case 0:
                    int len = TE.length();
                    if(len > 1) {
                        l = random.nextInt(len - 1);
                        r = random.nextInt(len - 1 - l) + l + 1;
                        TE.cut(l, r);
                    }
                    break;
                case 1:
                    len = TE.length();
                    if(len > 1) {
                        l = random.nextInt(len - 1);
                        r = random.nextInt(len - 1 - l) + l + 1;
                        TE.copy(l, r);
                    }
                    break;
                case 2:
                    len = TE.length();
                    int index = random.nextInt(len);
                    TE.paste(index);
                    break;
                case 3:
                    TE.getText();
                    break;
                case 4:
                    TE.misspellings();
                    break;
            }
        }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println(N + " random operations take " + (double)timeElapsed/1000000 + " ms");

    }

    // operations with heavy read and check misspelling words
    private static void testRandomOperation2(TextEditor TE, int N) {

        // [0]: cut(i, j);
        // [1]: copy(i, j);
        // [2]: paste(i);
        // [3,6]: getText();
        // [7,10]: misspellings();
        long startTime, endTime, timeElapsed;
        startTime = System.nanoTime();
        Random random = new Random();
        for(int i=0; i<N; i++) {
            int op = random.nextInt(11);
            int l, r, len;
            if(op == 0) {
                len = TE.length();
                if(len > 1) {
                    l = random.nextInt(len - 1);
                    r = random.nextInt(len - 1 - l) + l + 1;
                    TE.cut(l, r);
                }
            } else if(op == 1) {
                len = TE.length();
                if(len > 1) {
                    l = random.nextInt(len - 1);
                    r = random.nextInt(len - 1 - l) + l + 1;
                    TE.copy(l, r);
                }
            } else if(op == 2) {
                len = TE.length();
                int index = random.nextInt(len);
                TE.paste(index);
            } else if(op>=3 && op<=6) {
                TE.getText();
            } else {
                TE.misspellings();
            }
        }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println(N + " random operations take " + (double)timeElapsed/1000000 + " ms");

    }

    // operations with heavy modifications
    private static void testRandomOperation3(TextEditor TE, int N) {

        // [0,3]: cut(i, j);
        // [4,7]: copy(i, j);
        // [8,11]: paste(i);
        // [12]: getText();
        // [13]: misspellings();
        long startTime, endTime, timeElapsed;
        startTime = System.nanoTime();
        Random random = new Random();
        for(int i=0; i<N; i++) {

            int op = random.nextInt(14);
            int l, r, len;
            if(op>=0 && op<=3) {
                len = TE.length();
                if(len > 1) {
                    l = random.nextInt(len - 1);
                    r = random.nextInt(len - 1 - l) + l + 1;
                    TE.cut(l, r);
                }
            } else if(op>=4 && op<=7) {
                len = TE.length();
                if(len > 1) {
                    l = random.nextInt(len - 1);
                    r = random.nextInt(len - 1 - l) + l + 1;
                    TE.copy(l, r);
                }
            } else if(op>=8 && op<=11) {
                len = TE.length();
                int index = random.nextInt(len);
                TE.paste(index);
            } else if(op==12) {
                TE.getText();
            } else {
                TE.misspellings();
            }
        }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println(N + " random operations take " + (double)timeElapsed/1000000 + " ms");

    }

}
