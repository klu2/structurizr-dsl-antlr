package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.Workspace;
import com.structurizr.model.*;
import com.structurizr.view.DynamicView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.SystemLandscapeView;
import io.agilecoding.structurizr.dsl.antlr.internal.StructurizrDSLBaseListener;
import io.agilecoding.structurizr.dsl.antlr.internal.StructurizrDSLParser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class AntlrStructurizrDSLListener extends StructurizrDSLBaseListener {

    private Workspace workspace;
    private final Map<String, ModelItem> elementsByDslID = new HashMap<>();

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
        if (ctx.id != null) {
            elementsByDslID.put(ctx.id.getText(), person);
        }
    }

    @Override
    public void exitSoftwareSystem(StructurizrDSLParser.SoftwareSystemContext ctx) {
        SoftwareSystem softwareSystem = this.workspace.getModel().addSoftwareSystem(ctx.name.getText());
        if (ctx.description != null) {
            softwareSystem.setDescription(ctx.description.getText());
        }
        if (ctx.id != null) {
            elementsByDslID.put(ctx.id.getText(), softwareSystem);
        }
    }

    @Override
    public void exitModel(StructurizrDSLParser.ModelContext ctx) {
        for (var relCtx : ctx.relationship()) {
            ModelItem source = this.elementsByDslID.get(relCtx.source.getText());
            ModelItem destination = this.elementsByDslID.get(relCtx.destination.getText());
            String description = (relCtx.description != null) ? relCtx.description.getText() : "";

            Relationship relationship = null;
            if (source instanceof StaticStructureElement staticStructureElement) {
                if (destination instanceof SoftwareSystem softwareSystem) {
                    relationship = staticStructureElement.uses(softwareSystem, description);
                } else if (destination instanceof Container container) {
                    relationship = staticStructureElement.uses(container, description);
                } else if (destination instanceof CustomElement customElement) {
                    relationship = staticStructureElement.uses(customElement, description);
                }
            }
            if (relCtx.id != null) {
                elementsByDslID.put(relCtx.id.getText(), relationship);
            }
        }
    }

    @Override
    public void exitSystemContextView(StructurizrDSLParser.SystemContextViewContext ctx) {
        SoftwareSystem softwareSystem = (SoftwareSystem) this.elementsByDslID.get(ctx.softwareSystemId.getText());
        SystemContextView view = this.workspace.getViews().createSystemContextView(softwareSystem, UUID.randomUUID().toString(), "");
        for (StructurizrDSLParser.IncludeContext includeContext : ctx.include()) {
            if (includeContext.STAR() != null) {
                view.addAllSoftwareSystems();
                view.addAllPeople();
            } else {
                for (var id : includeContext.idList().ID()) {
                    ModelItem element = this.elementsByDslID.get(id.getText());
                    if (element instanceof SoftwareSystem ss) {
                        view.add(ss);
                    } else if (element instanceof Person p) {
                        view.add(p);
                    } else if (element instanceof Relationship r) {
                        view.add(r);
                    }
                }
            }
        }
        for (var excludeContext : ctx.exclude()) {
            for (var id : excludeContext.idList().ID()) {
                ModelItem element = this.elementsByDslID.get(id.getText());
                if (element instanceof SoftwareSystem ss) {
                    view.remove(ss);
                } else if (element instanceof Person p) {
                    view.remove(p);
                } else if (element instanceof Relationship r) {
                    view.remove(r);
                }
            }
        }
    }

    @Override
    public void exitDynamicView(StructurizrDSLParser.DynamicViewContext ctx) {
        final DynamicView dynamicView = this.workspace.getViews().createDynamicView("", "");
        ctx.relationship().forEach(relationshipContext -> {
            ModelItem source = this.elementsByDslID.get(relationshipContext.source.getText());
            ModelItem destination = this.elementsByDslID.get(relationshipContext.destination.getText());

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

    @Override
    public void exitSystemLandscapeView(StructurizrDSLParser.SystemLandscapeViewContext ctx) {
        SystemLandscapeView view = this.workspace.getViews().createSystemLandscapeView(UUID.randomUUID().toString(), "");
        for (StructurizrDSLParser.IncludeContext includeContext : ctx.include()) {
            if (includeContext.STAR() != null) {
                view.addAllSoftwareSystems();
                view.addAllPeople();
            } else {
                for (var id : includeContext.idList().ID()) {
                    ModelItem element = this.elementsByDslID.get(id.getText());
                    if (element instanceof SoftwareSystem ss) {
                        view.add(ss);
                    } else if (element instanceof Person p) {
                        view.add(p);
                    } else if (element instanceof Relationship r) {
                        view.add(r);
                    }
                }
            }
        }
        for (var excludeContext : ctx.exclude()) {
            for (var id : excludeContext.idList().ID()) {
                ModelItem element = this.elementsByDslID.get(id.getText());
                if (element instanceof SoftwareSystem ss) {
                    view.remove(ss);
                } else if (element instanceof Person p) {
                    view.remove(p);
                } else if (element instanceof Relationship r) {
                    view.remove(r);
                }
            }
        }
    }

    public Workspace getWorkspace() {
        return workspace;
    }
}
