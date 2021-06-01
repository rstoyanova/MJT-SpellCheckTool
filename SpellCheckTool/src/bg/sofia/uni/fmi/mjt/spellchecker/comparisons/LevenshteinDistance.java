package bg.sofia.uni.fmi.mjt.spellchecker.comparisons;

import java.util.ArrayList;
import java.util.List;

public class LevenshteinDistance {
    public static int calculateDistance(String lhs, String rhs) {
        if (lhs == null || rhs == null) {
            throw new IllegalArgumentException("Argument word must not be null!");
        }
        lhs = lhs.toLowerCase();
        rhs = rhs.toLowerCase();

        List<Integer> cost = new ArrayList<>();

        for (int j = 0; j <= rhs.length(); j++) {
            cost.add(j, j);
        }
        for (int i = 1; i <= lhs.length(); i++) {
            cost.set(0, i);
            int nw = i - 1;

            for (int j = 1; j <= rhs.length(); j++) {

                int cj = Math.min(1 + Math.min(cost.get(j), cost.get(j - 1)),
                        lhs.charAt(i - 1) == rhs.charAt(j - 1) ? nw : nw + 1);

                nw = cost.get(j);
                cost.set(j, cj);
            }
        }
        return cost.get(rhs.length());
    }

}
