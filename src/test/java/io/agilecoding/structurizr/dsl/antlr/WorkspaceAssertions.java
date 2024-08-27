package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.Workspace;
import com.structurizr.model.*;
import com.structurizr.view.SystemLandscapeView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WorkspaceAssertions {

    private final Workspace workspace;

    WorkspaceAssertions(Workspace workspace) {
        this.workspace = workspace;
    }

    public void assertHasRelationship(@Nullable Element source, @Nullable Element destination, String... descriptions) {
        assertNotNull(source, "source is null");
        assertNotNull(destination, "destination is null");
        Set<Relationship> relationships = source.getEfferentRelationshipsWith(destination);
        assertEquals(descriptions.length, relationships.size());
        for (String description : descriptions) {
            assertTrue(relationships.stream().anyMatch(r -> r.getDescription().equals(description)), "Relationship with description " + description + " not found");
        }
    }

    Model getModel() {
        return workspace.getModel();
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    @Nonnull
    public Person getPersonWithName(String name) {
        Person person = workspace.getModel().getPersonWithName(name);
        assertNotNull(person, "Person with name " + name + " not found");
        return person;
    }

    public void assertWorkspaceProperty(String key, String expectedValue) {
        assertEquals(expectedValue, workspace.getProperties().get(key), "Property " + key + " has unexpected value");
    }

    public SoftwareSystem getSoftwareSystemWithName(String name) {
        SoftwareSystem softwareSystem = workspace.getModel().getSoftwareSystemWithName(name);
        assertNotNull(softwareSystem, "Software System with name " + name + " not found");
        return softwareSystem;
    }

    public SystemLandscapeView getSystemLandscapeView() {
        return workspace.getViews().getSystemLandscapeViews().iterator().next();

    }
}
