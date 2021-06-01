package bg.sofia.uni.fmi.mjt.spellchecker.comparisons.wordvector;

import java.util.LinkedHashMap;
import java.util.Map;

public class WordVector {
    private Map<String, Integer> vector;

    public WordVector(String word) {
        final int SIZE_OF_GAMA = 2;
        final int STARTING_COUNT = 1;
        vector = new LinkedHashMap<>();

        for (int i = SIZE_OF_GAMA; i <= word.length(); i++) {
            String grama = word.substring(i - SIZE_OF_GAMA, i);

            if (vector.containsKey(grama)) {
                int count = vector.get(grama);
                vector.put(grama, (++count));
            } else {
                vector.put(grama, STARTING_COUNT);
            }
        }
    }

    public double getLength() {
        int length = 0;
        for (Map.Entry<String, Integer> pair : vector.entrySet()) {
            length += pair.getValue() * pair.getValue();
        }
        return Math.sqrt(length);
    }

    public int dotProductWith(WordVector other) {
        if (other == null) {
            throw new IllegalArgumentException("Argument WordVector must not be null!");
        }

        int result = 0;
        for (Map.Entry<String, Integer> pair : vector.entrySet()) {
            if (other.vector.containsKey(pair.getKey())) {
                result += pair.getValue() * other.vector.get(pair.getKey());
            }
        }
        return result;
    }
}
