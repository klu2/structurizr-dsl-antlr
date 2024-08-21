package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.Workspace;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.SystemContextView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class AntlrStructurizrParserTests {

    private final AntlrStructurizrParser parser = new AntlrStructurizrParser();

    @Test
    @Disabled
    void amazonWebServices() throws IOException {
        Workspace workspace = parseWorkspace("amazon-web-services.dsl");
        assertEquals("Amazon Web Services Example", workspace.getName());
    }

    @Test
    @Disabled
    void bibBankPlc() throws IOException {
        Workspace workspace = parseWorkspace("big-bank-plc.dsl");
        assertEquals("Big Bank plc", workspace.getName());
    }

    @Test
    void gettingStarted() throws IOException {
        Workspace workspace = parseWorkspace("getting-started.dsl");
        assertEquals("", workspace.getName());

        Person user = workspace.getModel().getPersonWithName("User");
        assertNotNull(user);
        assertEquals("", user.getDescription());

        SoftwareSystem softwareSystem = workspace.getModel().getSoftwareSystemWithName("Software System");
        assertNotNull(softwareSystem);

        assertTrue(user.hasEfferentRelationshipWith(softwareSystem));

        assertEquals(1, workspace.getViews().getSystemContextViews().size());
        SystemContextView systemContextView = workspace.getViews().getSystemContextViews().iterator().next();
        assertEquals(2, systemContextView.getElements().size());
    }

    @Test
    void workspaceProperties() throws IOException {
        Workspace workspace = parseWorkspace("workspace-properties.dsl");

        assertEquals("false", workspace.getProperties().get("structurizr.dslEditor"));
    }

    @Test
    void dynamic() throws IOException {
        Workspace workspace = parseWorkspace("dynamic.dsl");

        assertEquals(2, workspace.getViews().getDynamicViews().size());
    }

    @Test
    void excludeImpliedRelationship() throws IOException {
        Workspace workspace = parseWorkspace("exclude-implied-relationship.dsl");

    }

    @Test
    void relationships() throws IOException {
        Workspace workspace = parseWorkspace("relationships.dsl");

    }

    @Test
    void namesWithoutQuotes() throws IOException {
        Workspace workspace = parseWorkspace("names-without-quotes.dsl");
        assertNotNull(workspace.getModel().getSoftwareSystemWithName("ss"));
        assertNotNull(workspace.getModel().getPersonWithName("user"));
    }

    @Test
    @Disabled
    void hierarchicalIdentifiers() throws IOException {
        Workspace workspace = parseWorkspace("hierarchical-identifiers.dsl");

        assertEquals(0, requireNonNull(workspace.getModel().getSoftwareSystemWithName("B")).getRelationships().size());
        assertEquals(0, requireNonNull(workspace.getModel().getPersonWithName("A")).getRelationships().size());
    }

    private Workspace parseWorkspace(String fileName) throws IOException {
        File file = new File("src/test/resources/dsl/" + fileName);
        if (!file.exists()) {
            Assertions.fail("File not found: " + file.getAbsolutePath());
        }
        return parser.parseWorkspaceFromDsl(file.toPath());
    }
}

