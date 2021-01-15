public interface TextEditor {

    // Removes characters [i..j) from the document and places them in the clipboard.
    // Previous clipboard contents is overwritten.
    void cut(int i, int j);

    // Places characters [i..j) from the document in the clipboard.
    // Previous clipboard contents is overwritten.
    void copy(int i, int j);

    // Inserts the contents of the clipboard into the document starting at position i.
    // Nothing is inserted if the clipboard is empty.
    void paste(int i);

    // Returns the document as a string.
    String getText();

    // Returns the number of misspelled words in the document. A word is considered misspelled
    // if it does not appear in /usr/share/dict/words or any other dictionary (of comparable size)
    // that you choose.
    int misspellings();

    // Returns the number of characters in the document
    int length();

}
