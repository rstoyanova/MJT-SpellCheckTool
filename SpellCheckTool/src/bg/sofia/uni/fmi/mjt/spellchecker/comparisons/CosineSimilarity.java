package bg.sofia.uni.fmi.mjt.spellchecker.comparisons;

import bg.sofia.uni.fmi.mjt.spellchecker.comparisons.wordvector.WordVector;

public class CosineSimilarity {
    public static double cosineSimilarityBetween(String lhs, String rhs) {
        if (lhs == null || rhs == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        WordVector lhsVector = new WordVector(lhs);
        WordVector rhsVector = new WordVector(rhs);

        return lhsVector.dotProductWith(rhsVector) / (lhsVector.getLength() * rhsVector.getLength());
    }
}
