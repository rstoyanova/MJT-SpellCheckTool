package bg.sofia.uni.fmi.mjt.spellchecker.dictionary;

import bg.sofia.uni.fmi.mjt.spellchecker.comparisons.CosineSimilarity;
import bg.sofia.uni.fmi.mjt.spellchecker.comparisons.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Dictionary extends AbstractDictionary {

    public Dictionary() {
        super();
    }

    public List<String> findClosestNWordsByCosineSimilarity(String word, int n) {
        final int ZERO_SUGGESTIONS = 0;
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        if (n < ZERO_SUGGESTIONS) {
            throw new IllegalArgumentException("Argument n must not be negative!");
        }
        if (n == ZERO_SUGGESTIONS) {
            return new ArrayList<>();
        }

        List<String> sortedByCosineSimilarity =
                wordCollection.stream().sorted(Comparator.comparing(
                        (String other) -> (CosineSimilarity.cosineSimilarityBetween(word, other))).reversed())
                        .limit(n)
                        .collect(Collectors.toList());

        return sortedByCosineSimilarity;
    }

    public List<String> findClosestNWordsByLevenshteinDistance(String word, int n) {
        final int ZERO_SUGGESTIONS = 0;
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        if (n < ZERO_SUGGESTIONS) {
            throw new IllegalArgumentException("Argument n must not be negative!");
        }
        if (n == ZERO_SUGGESTIONS) {
            return new ArrayList<>();
        }

        List<String> sortedByLevenshteinDistance =
                wordCollection.stream().sorted(Comparator.comparing(
                        (String other) -> (LevenshteinDistance.calculateDistance(word, other))))
                        .limit(n)
                        .collect(Collectors.toList());

        return sortedByLevenshteinDistance;
    }
}
