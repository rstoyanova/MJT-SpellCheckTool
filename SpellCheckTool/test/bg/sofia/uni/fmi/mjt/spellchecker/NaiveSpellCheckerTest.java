package bg.sofia.uni.fmi.mjt.spellchecker;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class NaiveSpellCheckerTest {

    private static final String FINDINGS_TITLE = "= = = Findings = = =";
    private static final String NL = System.lineSeparator();

    private static final String dictionary =
                    "world" + NL
                    + "word" + NL
                    + "cookie" + NL
                    + "cook" + NL
                    + "dinner" + NL
                    + "hello" + NL
                    + "hallow" + NL
                    + "hey" + NL
                    + "table" + NL
                    + "tab" + NL
                    + "best";

    private static final String stopwordsDictionary =
                    "I'm" + NL
                    + "what" + NL
                    + "is" + NL
                    + "the" + NL
                    + "under" + NL
                    + "in";

    private static Reader dictionaryReader;
    private static Reader stopwordsReader;
    private static NaiveSpellChecker spellChecker;
    private static String text1;
    private static Reader textReader1;
    private static Reader textReader2;
    private static Reader textReader3;
    private static Reader textReader4;
    private static Reader textReader5;

    @BeforeClass
    public static void startSet() {
        dictionaryReader = new StringReader(dictionary);
        stopwordsReader = new StringReader(stopwordsDictionary);
        spellChecker = new NaiveSpellChecker(dictionaryReader, stopwordsReader);

        text1 = "The cookieS is " + NL
                + "under tHe ttable";
        final String text2 = "";
        final String text3 = "Hallo!";
        final String text4 = "chocolate<3";
        final String text5 = "{(-words-)}";

        textReader1 = new StringReader(text1);
        textReader2 = new StringReader(text2);
        textReader3 = new StringReader(text3);
        textReader4 = new StringReader(text4);
        textReader5 = new StringReader(text5);
    }

    @AfterClass
    public static void free() {
        try {
            dictionaryReader.close();
            stopwordsReader.close();

            textReader1.close();
            textReader2.close();
            textReader3.close();
            textReader4.close();
            textReader5.close();
        } catch (IOException e) {
            throw new RuntimeException("IO operation failed: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Closing streams failed" + e);
        }

    }

    @Test
    public void testMetadataWordCount() {
        try {
            textReader1.reset();
            textReader2.reset();
            textReader3.reset();
            textReader4.reset();
            textReader5.reset();
        } catch (IOException e) {
            throw new RuntimeException("IO operation failed: " + e);
        }
        final int ZERO_WORDS_EXPECTED = 0;
        final int ONE_WORDS_EXPECTED = 1;
        final int TWO_WORDS_EXPECTED = 2;

        assertEquals("Words in [The cookieS is \nunder tHe ttable]",
                TWO_WORDS_EXPECTED, spellChecker.metadata(textReader1).words());
        assertEquals("Words in []",
                ZERO_WORDS_EXPECTED, spellChecker.metadata(textReader2).words());
        assertEquals("Words in [Hallo!]",
                ONE_WORDS_EXPECTED, spellChecker.metadata(textReader3).words());
        assertEquals("Words in [chocolate<3]",
                ONE_WORDS_EXPECTED, spellChecker.metadata(textReader4).words());
        assertEquals("Words in [{(-words-)}]",
                ONE_WORDS_EXPECTED, spellChecker.metadata(textReader5).words());

    }

    @Test
    public void testMetadataCharCount() {
        try {
            textReader1.reset();
            textReader2.reset();
            textReader3.reset();
            textReader4.reset();
            textReader5.reset();
        } catch (IOException e) {
            throw new RuntimeException("IO operation failed: " + e);
        }
        final int ZERO_CHARS_EXPECTED = 0;
        final int SIX_CHARS_EXPECTED = 6;
        final int ELEVEN_CHARS_EXPECTED = 11;
        final int TWENTY_SIX_CHARS_EXPECTED = 26;

        assertEquals("Chars in [The cookieS is \nunder tHe ttable]",
                TWENTY_SIX_CHARS_EXPECTED, spellChecker.metadata(textReader1).characters());
        assertEquals("Chars in []",
                ZERO_CHARS_EXPECTED, spellChecker.metadata(textReader2).characters());
        assertEquals("Chars in [Hallo!]",
                SIX_CHARS_EXPECTED, spellChecker.metadata(textReader3).characters());
        assertEquals("Chars in [chocolate<3]",
                ELEVEN_CHARS_EXPECTED, spellChecker.metadata(textReader4).characters());
        assertEquals("Chars in [{(-words-)}]",
                ELEVEN_CHARS_EXPECTED, spellChecker.metadata(textReader5).characters());
    }

    @Test
    public void testMetadataMistakesCount() {
        try {
            textReader1.reset();
            textReader2.reset();
            textReader3.reset();
            textReader4.reset();
            textReader5.reset();
        } catch (IOException e) {
            throw new RuntimeException("IO operation failed: " + e);
        }
        final int ZERO_MIST_EXPECTED = 0;
        final int ONE_MIST_EXPECTED = 1;
        final int TWO_MIST_EXPECTED = 2;

        assertEquals("Mistakes in [The cookieS is \nunder tHe ttable]",
                TWO_MIST_EXPECTED, spellChecker.metadata(textReader1).mistakes());
        assertEquals("Mistakes in []",
                ZERO_MIST_EXPECTED, spellChecker.metadata(textReader2).mistakes());
        assertEquals("Mistakes in [Hallo!]",
                ONE_MIST_EXPECTED, spellChecker.metadata(textReader3).mistakes());
        assertEquals("Mistakes in [chocolate<3]",
                ONE_MIST_EXPECTED, spellChecker.metadata(textReader4).mistakes());
        assertEquals("Mistakes in [{(-words-)}]",
                ONE_MIST_EXPECTED, spellChecker.metadata(textReader5).mistakes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMetadataNullArgument() {
        spellChecker.metadata(null);
    }

    @Test
    public void testAnalyzeCorrectOutputWithMistakes() {
        try {
            textReader1.reset();
        } catch (IOException e) {
            throw new RuntimeException("IO operation failed: " + e);
        }

        final String message = "Analyze does not return expected value";
        final int SUGGESTIONS_COUNT = 2;
        String actual;
        final String expected = text1 + NL
                + spellChecker.metadata(textReader1).metadataToString() + NL
                + FINDINGS_TITLE + NL
                + "Line #1, {cookieS} - Possible suggestions are {cookie, cook}" + NL
                + "Line #2, {ttable} - Possible suggestions are {table, tab}";

        try (Writer output = new StringWriter()) {
            spellChecker.analyze(textReader1, output, SUGGESTIONS_COUNT);
            actual = output.toString();
        } catch (IOException e) {
            throw new RuntimeException("IO error/ testAnalyze failed: " + e);
        }

        assertEquals(message, expected, actual);
    }

    @Test
    public void testAnalyzeCorrectOutputWithoutMistakes() {
        final String message = "Analyze does not return expected value";
        String actual;
        final String text = "The word cOOkie" + NL
                + "is the" + NL
                + "BEST iN the" + NL
                + "world";

        String expected;
        final int SUGGESTIONS_COUNT = 2;

        try (Reader input = new StringReader(text);
             Writer output = new StringWriter()) {

            expected = text + NL
                    + spellChecker.metadata(input).metadataToString() + NL
                    + FINDINGS_TITLE;
            spellChecker.analyze(input, output, SUGGESTIONS_COUNT);
            actual = output.toString();
        } catch (IOException e) {
            throw new RuntimeException("IO error/ testAnalyze failed: " + e);
        }

        assertEquals(message, expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeWithNullReaderArgument() {
        final int SUGGESTIONS_COUNT = 5;
        spellChecker.analyze(null, new StringWriter(), SUGGESTIONS_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeWithNullWriterArgument() {
        final int SUGGESTIONS_COUNT = 5;
        spellChecker.analyze(new StringReader(""), null, SUGGESTIONS_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeWithNegativeNumberOfSuggestions() {
        final int SUGGESTIONS_COUNT = -10;
        spellChecker.analyze(new StringReader(""), new StringWriter(), SUGGESTIONS_COUNT);
    }

    @Test
    public void testFindClosestWordsCorrectOutput() {
        final String message = "findClosestWords does not return expected value";
        List<String> expected = new ArrayList<>();
        final String word = "hellou";
        final int SUGGESTIONS_COUNT = 3;

        expected.add("hello");
        expected.add("hallow");
        expected.add("hey");

        List<String> actual = spellChecker.findClosestWords(word, SUGGESTIONS_COUNT);

        assertEquals(message, expected, actual);
    }

    @Test
    public void testFindClosestWordsWithZeroSuggestionsCount() {
        final String message = "findClosestWords does not return expected value";
        List<String> expected = new ArrayList<>();
        final int ZERO_SUGGESTIONS_COUNT = 0;
        assertEquals(message,
                expected, spellChecker.findClosestWords("", ZERO_SUGGESTIONS_COUNT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsWithNullWordArgument() {
        final int SUGGESTIONS_COUNT = 10;
        spellChecker.findClosestWords(null, SUGGESTIONS_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsWithNegativeSuggestionsCount() {
        final int SUGGESTIONS_COUNT = -10;
        spellChecker.findClosestWords("", SUGGESTIONS_COUNT);
    }

    @Test
    public void testFindClosestWordsByLevenshteinDistCorrectOutput() {
        final String message = "findClosestWords does not return expected value";
        List<String> expected = new ArrayList<>();
        final String word = "hellou";
        final int SUGGESTIONS_COUNT = 3;

        expected.add("hello");
        expected.add("hallow");
        expected.add("hey");

        List<String> actual = spellChecker.findClosestWordsByLevenshteinDistance(word, SUGGESTIONS_COUNT);

        assertEquals(message, expected, actual);
    }

    @Test
    public void testFindClosestWordsByLevenshteinDistWithZeroSuggestionsCount() {
        final String message = "findClosestWords does not return expected value";
        final int ZERO_SUGGESTIONS_COUNT = 0;
        List<String> expected = new ArrayList<>();
        assertEquals(message,
                expected, spellChecker.findClosestWordsByLevenshteinDistance("", ZERO_SUGGESTIONS_COUNT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsByLevenshteinDistWithNullWordArgument() {
        final int SUGGESTIONS_COUNT = 10;
        spellChecker.findClosestWordsByLevenshteinDistance(null, SUGGESTIONS_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsByLevenshteinDistWithNegativeSuggestionsCount() {
        final int SUGGESTIONS_COUNT = -10;
        spellChecker.findClosestWordsByLevenshteinDistance("", SUGGESTIONS_COUNT);
    }

}
