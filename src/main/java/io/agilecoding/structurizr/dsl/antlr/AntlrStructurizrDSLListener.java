package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.Workspace;
import com.structurizr.model.*;
import com.structurizr.view.*;
import io.agilecoding.structurizr.dsl.antlr.internal.StructurizrDSLBaseListener;
import io.agilecoding.structurizr.dsl.antlr.internal.StructurizrDSLParser;

import java.util.List;
import java.util.Set;
import java.util.UUID;

class AntlrStructurizrDSLListener extends StructurizrDSLBaseListener {

    private Workspace workspace;
    private final IdentifiersRegister identifiersRegister = new IdentifiersRegister();

    @Override
    public void enterWorkspace(StructurizrDSLParser.WorkspaceContext ctx) {
        if (ctx.name == null) {
            this.workspace = new Workspace("", "");
        } else {
            this.workspace = new Workspace(ctx.name.getText(), ctx.description.getText());
        }
        workspace.getModel().setImpliedRelationshipsStrategy(new CreateImpliedRelationshipsUnlessAnyRelationshipExistsStrategy());
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
            identifiersRegister.register(ctx.id.getText(), person);
        }
        for (StructurizrDSLParser.RelationshipContext relationshipContext : ctx.relationship()) {
            // TODO: check source
            ModelItem destination = this.identifiersRegister.get(relationshipContext.destination.getText());
            if (destination instanceof SoftwareSystem softwareSystem) {
                person.uses(softwareSystem, relationshipContext.description.getText());
            } else if (destination instanceof Container container) {
                person.uses(container, relationshipContext.description.getText());
            } else if (destination instanceof CustomElement customElement) {
                person.uses(customElement, relationshipContext.description.getText());
            }
        }
    }

    @Override
    public void exitSoftwareSystem(StructurizrDSLParser.SoftwareSystemContext ctx) {
        SoftwareSystem softwareSystem = this.workspace.getModel().addSoftwareSystem(ctx.name.getText());
        if (ctx.description != null) {
            softwareSystem.setDescription(ctx.description.getText());
        }
        if (ctx.id != null) {
            identifiersRegister.register(ctx.id.getText(), softwareSystem);
        }
        if (ctx.container() != null) {
            for (var containerCtx : ctx.container()) {
                Container container = softwareSystem.addContainer(ctx.name.getText());
                if (containerCtx.description != null) {
                    container.setDescription(containerCtx.description.getText());
                }
                if (containerCtx.id != null) {
                    identifiersRegister.register(containerCtx.id.getText(), container);
                }
            }
        }
    }

    @Override
    public void exitModel(StructurizrDSLParser.ModelContext ctx) {
        for (var relCtx : ctx.relationship()) {
            ModelItem source = this.identifiersRegister.get(relCtx.source.getText());

            ModelItem destination = this.identifiersRegister.get(relCtx.destination.getText());
            String description = (relCtx.description != null) ? relCtx.description.getText() : "";

            Relationship relationship = null;
            if (source instanceof StaticStructureElement staticStructureElement) {
                if (destination instanceof SoftwareSystem softwareSystem) {
                    relationship = staticStructureElement.uses(softwareSystem, description);
                } else if (destination instanceof Container container) {
                    relationship = staticStructureElement.uses(container, description);
                } else if (destination instanceof CustomElement customElement) {
                    relationship = staticStructureElement.uses(customElement, description);
                } else {
                    throw new UnsupportedOperationException("cannot handle " + destination.getClass() + " as destination type");
                }
            } else {
                throw new UnsupportedOperationException("cannot handle " + source.getClass() + " as source type");
            }
            if (relationship == null) {
                throw new RuntimeException("relationship has not been created");
            }
            if (relCtx.id != null) {
                identifiersRegister.register(relCtx.id.getText(), relationship);
            }
        }
    }

    @Override
    public void exitSystemLandscapeView(StructurizrDSLParser.SystemLandscapeViewContext ctx) {
        SystemLandscapeView view = this.workspace.getViews().createSystemLandscapeView(UUID.randomUUID().toString(), "");
        configureView(ctx.viewConfiguration(), view);
    }

    @Override
    public void exitSystemContextView(StructurizrDSLParser.SystemContextViewContext ctx) {
        SoftwareSystem softwareSystem = (SoftwareSystem) this.identifiersRegister.get(ctx.softwareSystemId.getText());
        SystemContextView view = this.workspace.getViews().createSystemContextView(softwareSystem, UUID.randomUUID().toString(), "");
        configureView(ctx.viewConfiguration(), view);
    }

    @Override
    public void exitDynamicView(StructurizrDSLParser.DynamicViewContext ctx) {
        final DynamicView dynamicView = this.workspace.getViews().createDynamicView("", "");
        ctx.relationship().forEach(relationshipContext -> {
            ModelItem source = this.identifiersRegister.get(relationshipContext.source.getText());
            ModelItem destination = this.identifiersRegister.get(relationshipContext.destination.getText());

            if (source instanceof StaticStructureElement sourceElement && destination instanceof StaticStructureElement targetElement) {
                if (relationshipContext.description != null) {
                    dynamicView.add(sourceElement, relationshipContext.description.getText(), targetElement);
                } else {
                    dynamicView.add(sourceElement, targetElement);
                }
            } else {
                throw new UnsupportedOperationException("cannot yet handle " + source.getClass() + " and " + destination.getClass());
            }
        });
    }

    private void configureView(List<StructurizrDSLParser.ViewConfigurationContext> viewConfigurationItems, StaticView view) {
        for (var config : viewConfigurationItems) {
            if (config.include() != null) {
                var includeContext = config.include();
                if (includeContext.elementIdentifier().star() != null) {
                    view.addDefaultElements();
                } else if (includeContext.elementIdentifier().idList() != null) {
                    for (var id : includeContext.elementIdentifier().idList().ID()) {
                        ModelItem element = this.identifiersRegister.get(id.getText());
                        if (element instanceof SoftwareSystem ss) {
                            view.add(ss);
                        } else if (element instanceof Person p) {
                            view.add(p);
                        } else if (element instanceof Relationship r) {
                            DslContextUtils.getConnectedRelationships(r).forEach(view::add);
                        }
                    }
                } else if (includeContext.elementIdentifier().starRelationship() != null) {
                    throw new RuntimeException("cannot include all relationships");
                } else {
                    throw new UnsupportedOperationException("unexpected path");
                }
            } else if (config.exclude() != null) {
                var excludeContext = config.exclude();
                if (excludeContext.elementIdentifier().star() != null) {
                    Set<ElementView> elements = view.getElements();
                    for (ElementView element : elements) {
                        if (element.getElement() instanceof Person person) {
                            view.remove(person);
                        } else if (element.getElement() instanceof SoftwareSystem softwareSystem) {
                            view.remove(softwareSystem);
                        } else {
                            throw new UnsupportedOperationException("cannot remove " + element.getElement());
                        }
                    }
                } else if (excludeContext.elementIdentifier().idList() != null) {
                    for (var id : excludeContext.elementIdentifier().idList().ID()) {
                        ModelItem element = this.identifiersRegister.get(id.getText());
                        if (element instanceof SoftwareSystem ss) {
                            view.remove(ss);
                        } else if (element instanceof Person p) {
                            view.remove(p);
                        } else if (element instanceof Relationship r) {
                            DslContextUtils.getConnectedRelationships(r).forEach(view::remove);
                        }
                    }
                } else if (excludeContext.elementIdentifier().starRelationship() != null) {
                    Set<RelationshipView> relationships = view.getRelationships();
                    for (RelationshipView relationship : relationships) {
                        view.remove(relationship.getRelationship());
                    }
                } else {
                    throw new UnsupportedOperationException("unexpected path");
                }
            } else if (config.autolayout() != null) {
                view.enableAutomaticLayout();
            }
        }
    }

    public Workspace getWorkspace() {
        return workspace;
    }
}
