# Structurizr DSL Parser using ANTLR

This is a POC defining an [ANTLR](https://antlr.org) grammar to parse [Structurizr DSL](https://structurizr.com/dsl) files.

Right now, this repository does not serve as a fully working alternative to `structurizr-dsl` as only minor parts of the
DSL are yet supported.

## Motivation

The package ```structurizr-dsl``` from https://github.com/structurizr/java is a fully working parser maintained by the Structurizr team.
It comes dependency-free and therefore has its own lexer and parser implementation. 

Using ANTLR as a parser generator, we can define a grammar for the DSL and generate the lexer and parser classes. This would have two benefits:

* Much lesser code to maintain as there is no hand-written lexer and parser anymore
* ANTLR is a well-known and widely used tool for parsing
* The ANTLR grammar can be used for other tools as well, i.e. for syntax highlighting and code completion in IDEs

Still, as mentioned above, this is still a POC and by far not feature-complete. I will continue development over time but you are free
to also contribute to this project.

## Build from source

This repository uses [Gradle](https://gradle.org) as build tool. To build the project, run the following command:

```bash
./gradlew build
```

## Usage

To create and user the parser, run the following code:

```java
import java.nio.file.Path;

AntlrStructurizrParser parser = new AntlrStructurizrParser();
Workspace workspace = parser.parse(Path.of("file/to/structurizr.dsl"));

```

That gives you the parsed `Workspace` object from [Structurizr-Java](https://github.com/structurizr/java).