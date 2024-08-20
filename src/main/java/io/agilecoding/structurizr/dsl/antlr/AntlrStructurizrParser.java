package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.Workspace;
import io.agilecoding.structurizr.dsl.antlr.internal.StructurizrDSLLexer;
import io.agilecoding.structurizr.dsl.antlr.internal.StructurizrDSLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.Path;

public class AntlrStructurizrParser {

    public Workspace parseWorkspaceFromDsl(Path path) throws IOException {
        StructurizrDSLLexer lexer = new StructurizrDSLLexer(CharStreams.fromPath(path));
        StructurizrDSLParser parser = new StructurizrDSLParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new ThrowingErrorStrategy());
        ParseTree tree = parser.workspace();
        AntlrStructurizrDSLListener listener = new AntlrStructurizrDSLListener();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
        return listener.getWorkspace();
    }
}
