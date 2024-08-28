package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
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
        var workspace = parseAndAssertWorkspace("amazon-web-services.dsl");
        assertEquals("Amazon Web Services Example", workspace.getWorkspace().getName());
    }

    @Test
    @Disabled
    void bibBankPlc() throws IOException {
        var workspace = parseAndAssertWorkspace("big-bank-plc.dsl");
        assertEquals("Big Bank plc", workspace.getWorkspace().getName());
    }

    @Test
    void gettingStarted() throws IOException {
        var workspace = parseAndAssertWorkspace("getting-started.dsl");
        assertEquals("", workspace.getWorkspace().getName());

        Person user = workspace.getPersonWithName("User");
        assertEquals("", user.getDescription());

        SoftwareSystem softwareSystem = workspace.getSoftwareSystemWithName("Software System");

        assertTrue(user.hasEfferentRelationshipWith(softwareSystem));

        assertEquals(1, workspace.getWorkspace().getViews().getSystemContextViews().size());
        SystemContextView systemContextView = workspace.getWorkspace().getViews().getSystemContextViews().iterator().next();
        assertEquals(2, systemContextView.getElements().size());
    }

    @Test
    void workspaceProperties() throws IOException {
        var workspace = parseAndAssertWorkspace("workspace-properties.dsl");
        workspace.assertWorkspaceProperty("structurizr.dslEditor", "false");
    }

    @Test
    void dynamic() throws IOException {
        var workspace = parseAndAssertWorkspace("dynamic.dsl");

        assertEquals(2, workspace.getWorkspace().getViews().getDynamicViews().size());
    }

    @Test
    void includeImpliedRelationship() throws Exception {
        var workspace = parseAndAssertWorkspace("include-implied-relationship.dsl");

        // check the system landscape view includes a relationship
        assertEquals(1, workspace.getSystemLandscapeView().getRelationships().size());
    }

    @Test
    void excludeImpliedRelationship() throws IOException {
        var workspace = parseAndAssertWorkspace("exclude-implied-relationship.dsl");
        var landscapeView = workspace.getSystemLandscapeView();
        assertEquals(2, landscapeView.getElements().size());
        assertEquals(0, landscapeView.getRelationships().size());
    }

    @Test
    void relationships() throws IOException {
        var workspace = parseAndAssertWorkspace("relationships.dsl");

        SoftwareSystem softwareSystem = workspace.getModel().getSoftwareSystemWithName("Software System");

        Person user = workspace.getPersonWithName("User");
        workspace.assertHasRelationship(user, softwareSystem, "Uses");

        Person user1 = workspace.getPersonWithName("User1");
        workspace.assertHasRelationship(user1, softwareSystem, "uses");
    }

    @Test
    void namesWithoutQuotes() throws IOException {
        var workspace = parseAndAssertWorkspace("names-without-quotes.dsl");
        assertNotNull(workspace.getModel().getSoftwareSystemWithName("ss"));
        assertNotNull(workspace.getModel().getPersonWithName("user"));
    }

    @Test
    void hierarchicalIdentifiers() throws IOException {
        var workspace = parseAndAssertWorkspace("hierarchical-identifiers.dsl");

        assertEquals(0, requireNonNull(workspace.getModel().getSoftwareSystemWithName("B")).getRelationships().size());
        assertEquals(0, requireNonNull(workspace.getModel().getPersonWithName("A")).getRelationships().size());

        SoftwareSystem b = workspace.getSoftwareSystemWithName("B");
        Container c = b.getContainerWithName("C");
        assertNotNull(c);
        Component d = c.getComponentWithName("D");
        assertNotNull(d, "component D not found");
        Component a = c.getComponentWithName("A");
        assertNotNull(a, "component D not found");

        workspace.assertHasRelationship(d, a, "");
    }

    private WorkspaceAssertions parseAndAssertWorkspace(String fileName) throws IOException {
        File file = new File("src/test/resources/dsl/" + fileName);
        if (!file.exists()) {
            Assertions.fail("File not found: " + file.getAbsolutePath());
        }
        Workspace workspace = parser.parseWorkspaceFromDsl(file.toPath());
        return new WorkspaceAssertions(workspace);
    }
}

