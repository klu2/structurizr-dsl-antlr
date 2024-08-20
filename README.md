# Structurizr DSL Parser using ANTLR

This is a POC defining an [ANTLR](https://antlr.org) grammar to parse [Structurizr DSL](https://structurizr.com/dsl) files.

Right now, this repository does not serve as a fully working alternative to `structurizr-dsl` as only minor parts of the
DSL are yet supported.

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