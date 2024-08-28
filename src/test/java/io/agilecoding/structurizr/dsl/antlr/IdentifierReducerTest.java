package io.agilecoding.structurizr.dsl.antlr;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentifierReducerTest {

    @Test
    void reduceString() {
        Set<String> result = IdentifierReducer.reduceString("a.b.c.d");

        Set<String> expected = new LinkedHashSet<>();
        expected.add("a.b.c.d");
        expected.add("a.b.d");
        expected.add("a.d");
        expected.add("d");

        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void reduceStringSorted() {
        Set<String> result = IdentifierReducer.reduceString("b.c.a");

        Set<String> expected = new LinkedHashSet<>();
        expected.add("b.c.a");
        expected.add("b.a");
        expected.add("a");

        assertThat(result).containsExactlyElementsOf(expected);
    }
}
