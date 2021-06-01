package bg.sofia.uni.fmi.mjt.spellchecker;

import bg.sofia.uni.fmi.mjt.spellchecker.dictionary.Dictionary;
import bg.sofia.uni.fmi.mjt.spellchecker.dictionary.StopWordsDictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

public class NaiveSpellChecker implements SpellChecker {

    private final Dictionary dictionary;
    private final StopWordsDictionary stopWordsDictionary;

    /**
     * Creates a new instance of NaiveSpellCheckTool, based on a dictionary of words and stop words
     *
     * @param dictionaryReader a java.io.Reader input stream containing list of words
     *                         which will serve as a dictionary for the tool
     * @param stopwordsReader  a java.io.Reader input stream containing list of stopwords
     */
    public NaiveSpellChecker(Reader dictionaryReader, Reader stopwordsReader) {
        dictionary = new Dictionary();
        dictionary.fillFromStream(dictionaryReader, false);
        stopWordsDictionary = new StopWordsDictionary();
        stopWordsDictionary.fillFromStream(stopwordsReader, true);
    }

    private String processWord(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        String resultString = word.toLowerCase().trim();
        final String EMPTY_STR = "";

        if (isNonAlphaNumericWord(word)) {
            return EMPTY_STR;
        }

        resultString = removeLeadingNonAlphanumericSymbols(resultString);
        resultString = removeTrailingNonAlphanumericSymbols(resultString);
        return resultString;
    }

    private boolean isNonAlphaNumericWord(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        int wordLength = word.length();
        final char FIRST_LETTER = 'a';
        final char LAST_LETTER = 'z';
        final char UPPER_FIRST_LETTER = 'A';
        final char UPPER_LAST_LETTER = 'Z';
        final char FIRST_DIGIT = '0';
        final int LAST_DIGIT = '9';

        for (int i = 0; i < wordLength; ++i) {
            char charAtI = word.charAt(i);
            if ((charAtI > FIRST_LETTER && charAtI < LAST_LETTER)
                    || (charAtI > UPPER_FIRST_LETTER && charAtI < UPPER_LAST_LETTER)
                    || (charAtI > FIRST_DIGIT && charAtI < LAST_DIGIT)) {
                return false;
            }
        }
        return true;
    }

    private String removeLeadingNonAlphanumericSymbols(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        StringBuilder resultString = new StringBuilder(word);
        final char FIRST_LETTER = 'a';
        final char LAST_LETTER = 'z';
        final char FIRST_DIGIT = '0';
        final int LAST_DIGIT = '9';
        while (true) {
            char charAtI = resultString.charAt(0);
            if ((charAtI >= FIRST_LETTER && charAtI <= LAST_LETTER)
                    || (charAtI >= FIRST_DIGIT && charAtI <= LAST_DIGIT)) {
                break;
            } else {
                resultString.deleteCharAt(0);
            }
        }
        return resultString.toString();
    }

    private String removeTrailingNonAlphanumericSymbols(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        StringBuilder resultString = new StringBuilder(word);
        int lastStringIndex = word.length() - 1;
        final char FIRST_LETTER = 'a';
        final char LAST_LETTER = 'z';
        final char FIRST_DIGIT = '0';
        final int LAST_DIGIT = '9';
        while (true) {
            char charAtI = resultString.charAt(lastStringIndex);
            if ((charAtI >= FIRST_LETTER && charAtI <= LAST_LETTER)
                    || (charAtI >= FIRST_DIGIT && charAtI <= LAST_DIGIT)) {
                break;
            } else {
                resultString.deleteCharAt(lastStringIndex--);
            }
        }
        return resultString.toString();
    }

    private boolean isMisspelled(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        return (!stopWordsDictionary.contains(word)
                && !dictionary.contains(word)
                && !word.equals(""));
    }

    private void writeSuggestionsInStream(Writer output, int suggestionsCount, String misspeledWord, int line) {
        try {
            output.write(System.lineSeparator() + "Line #" + line
                    + ", {" + misspeledWord + "} - Possible suggestions are {");

            List<String> suggestions = findClosestWords(misspeledWord, suggestionsCount);
            int commaCntr = suggestionsCount;
            final int LAST_SUGG = 1;
            for (String sugg : suggestions) {
                output.write(sugg);
                if (commaCntr != LAST_SUGG) {
                    output.write(", ");
                }
                commaCntr--;
            }
            output.write("}");
        } catch (IOException e) {
            throw new RuntimeException("IO error/ Writing suggestions failed: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Writing sugestions failed: " + e);
        }
    }

    private void writeCorectionsInStream(Reader input, Writer output, int suggestionsCount) {
        String buffLine = "";
        String findings = "= = = Findings = = =";
        int lineNum = 1;
        try {
            output.write(findings);
            var text = new BufferedReader(input);
            while ((buffLine = text.readLine()) != null) {
                String[] words = buffLine.split(" ");

                for (String currWord : words) {
                    String originalWord = currWord;
                    currWord = processWord(currWord);
                    if (isMisspelled(currWord)) {
                        writeSuggestionsInStream(output, suggestionsCount, originalWord, lineNum);
                    }
                }
                lineNum++;
            }
        } catch (IOException e) {
            throw new RuntimeException("IO error/ Writing corrections failed: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Writing corrections failed: " + e);
        }
    }

    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        final int ZERO_SUGGESTIONS = 0;
        if (textReader == null) {
            throw new IllegalArgumentException("Argument textReader must not be null!");
        }
        if (output == null) {
            throw new IllegalArgumentException("Argument output must not be null!");
        }
        if (suggestionsCount < ZERO_SUGGESTIONS) {
            throw new IllegalArgumentException("Argument suggestionsCount must not be null!");
        }

        try {
            var text = new BufferedReader(textReader);
            var outputBuffer = new BufferedWriter(output);
            textReader.reset();
            String originalText = text.lines().collect(Collectors.joining(System.lineSeparator()));
            outputBuffer.write(originalText + System.lineSeparator());
            textReader.reset();
            outputBuffer.write(metadata(text).metadataToString() + System.lineSeparator());
            outputBuffer.flush();
            textReader.reset();
            writeCorectionsInStream(text, outputBuffer, suggestionsCount);

            outputBuffer.flush();
        } catch (IOException e) {
            throw new RuntimeException("IO error/ Analyzing failed: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Analyzing failed: " + e);
        }
    }

    public Metadata metadata(Reader textReader) {
        if (textReader == null) {
            throw new IllegalArgumentException("Argument textReader must not be null!");
        }
        int chars = 0;
        int words = 0;
        int mistakes = 0;
        final String EMPTY_STR = "";

        String buffLine = "";
        try {
            BufferedReader text = new BufferedReader(textReader);
            while ((buffLine = text.readLine()) != null) {
                String[] currWords = buffLine.split(" ");
                for (String currWord : currWords) {
                    chars += currWord.trim().length();
                    currWord = processWord(currWord);
                    if (currWord != EMPTY_STR) {
                        if (!stopWordsDictionary.contains(currWord)) {
                            words++;
                        }
                        if (isMisspelled(currWord)) {
                            mistakes++;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("IO error/ Generating metadata failed: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Generating metadata failed: " + e);
        }

        Metadata res = new Metadata(chars, words, mistakes);
        return res;
    }

    public List<String> findClosestWords(String word, int n) {
        word = processWord(word);
        return dictionary.findClosestNWordsByCosineSimilarity(word, n);
    }

    public List<String> findClosestWordsByLevenshteinDistance(String word, int n) {
        word = processWord(word);
        return dictionary.findClosestNWordsByLevenshteinDistance(word, n);
    }

}
