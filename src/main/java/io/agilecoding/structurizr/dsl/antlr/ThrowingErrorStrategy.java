package io.agilecoding.structurizr.dsl.antlr;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;

/**
 * We want our parser to throw an exception when it encounters an error, rather than trying to recover from it.
 */
class ThrowingErrorStrategy extends DefaultErrorStrategy {
    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        throw new RuntimeException(e);
    }

    @Override
    public void sync(Parser recognizer) {
        // No-op to avoid unnecessary error recovery attempts
    }
}
