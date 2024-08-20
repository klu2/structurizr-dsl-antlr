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

    private Workspace parseWorkspace(String fileName) throws IOException {
        File file = new File("src/test/resources/dsl/" + fileName);
        if (!file.exists()) {
            Assertions.fail("File not found: " + file.getAbsolutePath());
        }
        return parser.parseWorkspaceFromDsl(file.toPath());
    }
}

