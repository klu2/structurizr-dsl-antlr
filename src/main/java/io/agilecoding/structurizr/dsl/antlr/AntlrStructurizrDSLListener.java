package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.Workspace;
import com.structurizr.model.*;
import com.structurizr.view.DynamicView;
import com.structurizr.view.SystemContextView;
import io.agilecoding.structurizr.dsl.antlr.internal.StructurizrDSLBaseListener;
import io.agilecoding.structurizr.dsl.antlr.internal.StructurizrDSLParser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class AntlrStructurizrDSLListener extends StructurizrDSLBaseListener {

    private Workspace workspace;
    private final Map<String, Element> elementsByDslID = new HashMap<>();

    @Override
    public void enterWorkspace(StructurizrDSLParser.WorkspaceContext ctx) {
        if (ctx.name == null) {
            this.workspace = new Workspace("", "");
        } else {
            this.workspace = new Workspace(ctx.name.getText(), ctx.description.getText());
        }
    }

    @Override
    public void exitWorkspace(StructurizrDSLParser.WorkspaceContext ctx) {
        if (ctx.properties() != null) {
            for (var p : ctx.properties().property()) {
                this.workspace.addProperty(p.key.getText(), p.value.getText());
            }
        }
    }

    @Override
    public void exitPerson(StructurizrDSLParser.PersonContext ctx) {
        Person person = this.workspace.getModel().addPerson(ctx.name.getText());
        if (ctx.description != null) {
            person.setDescription(ctx.description.getText());
        }
        elementsByDslID.put(ctx.id.getText(), person);
    }

    @Override
    public void exitSoftwareSystem(StructurizrDSLParser.SoftwareSystemContext ctx) {
        SoftwareSystem softwareSystem = this.workspace.getModel().addSoftwareSystem(ctx.name.getText());
        if (ctx.description != null) {
            softwareSystem.setDescription(ctx.description.getText());
        }
        elementsByDslID.put(ctx.id.getText(), softwareSystem);
    }

    @Override
    public void exitModel(StructurizrDSLParser.ModelContext ctx) {
        ctx.relationship().forEach(relCtx -> {
            Element source = this.elementsByDslID.get(relCtx.source.getText());
            Element destination = this.elementsByDslID.get(relCtx.destination.getText());
            String description = (relCtx.description != null) ? relCtx.description.getText() : "";

            if (source instanceof StaticStructureElement staticStructureElement) {
                if (destination instanceof SoftwareSystem softwareSystem) {
                    staticStructureElement.uses(softwareSystem, description);
                } else if (destination instanceof Container container) {
                    staticStructureElement.uses(container, description);
                } else if (destination instanceof CustomElement customElement) {
                    staticStructureElement.uses(customElement, description);
                }
            }
        });
    }

    @Override
    public void exitSystemContext(StructurizrDSLParser.SystemContextContext ctx) {
        SoftwareSystem softwareSystem = (SoftwareSystem) this.elementsByDslID.get(ctx.softwareSystemId.getText());
        SystemContextView systemContextView = this.workspace.getViews().createSystemContextView(softwareSystem, UUID.randomUUID().toString(), "");
        for (StructurizrDSLParser.IncludeContext includeContext : ctx.include()) {
            if (includeContext.STAR() != null) {
                systemContextView.addAllSoftwareSystems();
                systemContextView.addAllPeople();
            } else {
                includeContext.idList().ID().forEach(id -> {
                    Element element = this.elementsByDslID.get(id.getText());
                    if (element instanceof SoftwareSystem ss) {
                        systemContextView.add(ss);
                    } else if (element instanceof Person p) {
                        systemContextView.add(p);
                    }
                });
            }
        }
    }

    @Override
    public void exitDynamic(StructurizrDSLParser.DynamicContext ctx) {
        final DynamicView dynamicView = this.workspace.getViews().createDynamicView("", "");
        ctx.relationship().forEach(relationshipContext -> {
            Element source = this.elementsByDslID.get(relationshipContext.source.getText());
            Element destination = this.elementsByDslID.get(relationshipContext.destination.getText());

            if (source instanceof StaticStructureElement sourceElement && destination instanceof StaticStructureElement targetElement) {
                if (relationshipContext.description != null) {
                    dynamicView.add(sourceElement, relationshipContext.description.getText(), targetElement);
                } else {
                    dynamicView.add(sourceElement, targetElement);
                }
            }
            // TODO other combinations of source and destination
        });
    }

    public Workspace getWorkspace() {
        return workspace;
    }
}
