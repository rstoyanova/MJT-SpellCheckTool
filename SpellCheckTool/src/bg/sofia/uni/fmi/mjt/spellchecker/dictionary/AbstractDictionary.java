package bg.sofia.uni.fmi.mjt.spellchecker.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDictionary {
    protected Set<String> wordCollection;

    public AbstractDictionary() {
        wordCollection = new HashSet<>();
    }

    public String processWord(String word, boolean isAStopWord) {
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        String res = word.toLowerCase();
        res = res.trim();
        if (!isAStopWord) {
            res = res.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
        }
        return res;
    }

    public void fillFromStream(Reader stream, boolean acceptOneCharWords) {
        if (stream == null) {
            throw new IllegalArgumentException("Argument stream must not be null!");
        }
        String buffWord = "";
        try {
            var text = new BufferedReader(stream);
            while ((buffWord = text.readLine()) != null) {
                buffWord = processWord(buffWord, acceptOneCharWords);

                if (acceptOneCharWords || buffWord.length() > 1) {
                    wordCollection.add(buffWord);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("IO stream loading failed: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Filling failed: " + e);
        }
    }

    public boolean contains(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        return wordCollection.contains(word);
    }

}
