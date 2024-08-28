package io.agilecoding.structurizr.dsl.antlr;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

class IdentifierReducer {

    /**
     * Given a string like "a.b.c.d", this method returns a set of strings like (in exactly that order:
     * "a.b.c.d", "a.b.d", "a.d", "d"
     *
     * @param input and dot-ssparated string
     * @return a set of strings as deseribed above
     */
    @Nonnull
    public static Set<String> reduceString(@Nonnull String input) {
        Set<String> resultSet = new LinkedHashSet<>();
        String[] parts = input.split("\\.");
        int n = parts.length;

        // Add the original string
        resultSet.add(input);

        // Generate reduced strings
        for (int i = n - 2; i >= 0; i--) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j <= i; j++) {
                sb.append(parts[j]).append(".");
            }
            sb.append(parts[n - 1]);
            resultSet.add(sb.toString());
        }

        // Add the last part
        resultSet.add(parts[n - 1]);

        return resultSet;
    }
}
