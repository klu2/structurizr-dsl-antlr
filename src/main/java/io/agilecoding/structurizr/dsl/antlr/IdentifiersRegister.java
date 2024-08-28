package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.model.Element;
import com.structurizr.model.ModelItem;
import com.structurizr.model.Relationship;

import javax.annotation.Nonnull;
import java.util.*;

class IdentifiersRegister {

    private final Map<String, Element> elementsByIdentifier = new HashMap<>();
    private final Map<String, Relationship> relationshipsByIdentifier = new HashMap<>();
    private IdentifierScope identifierScope = IdentifierScope.Flat;

    public void setIdentifierScope(IdentifierScope identifierScope) {
        this.identifierScope = identifierScope;
    }

    public void register(@Nonnull String id, @Nonnull Element element) {
        if (identifierScope == IdentifierScope.Hierarchical) {
            id = calculateHierarchicalIdentifier(id, element);
        }
        if (elementsByIdentifier.containsKey(id)) {
            throw new IllegalArgumentException("Element with id " + id + " already exists");
        }
        elementsByIdentifier.put(id, element);
    }

    public void register(@Nonnull String id, @Nonnull Relationship relationship) {
        if (relationshipsByIdentifier.containsKey(id)) {
            throw new IllegalArgumentException("Relationship with id " + id + " already exists");
        }
        relationshipsByIdentifier.put(id, relationship);
    }

    private String calculateHierarchicalIdentifier(String identifier, Element element) {
        if (element.getParent() == null) {
            /*if (element instanceof DeploymentNode) {
                DeploymentNode dn = (DeploymentNode)element;
                return findIdentifier(new DeploymentEnvironment(dn.getEnvironment())) + "." + identifier;
            } else {*/
            return identifier;
            //}
        } else {
            return findIdentifier(element.getParent()) + "." + identifier;
        }
    }

    /**
     * Finds the identifier used when defining an element.
     *
     * @param element an Element instance
     * @return a String identifier (could be null if no identifier was explicitly specified)
     */
    public String findIdentifier(Element element) {
        if (elementsByIdentifier.containsValue(element)) {
            for (String identifier : elementsByIdentifier.keySet()) {
                Element e = elementsByIdentifier.get(identifier);

                if (e.equals(element)) {
                    return identifier;
                }
            }
        }

        return null;
    }

    @Nonnull
    public Element getElement(String id) {
        Element modelItem = this.elementsByIdentifier.get(id);
        if (modelItem == null) {
            throw new NoSuchElementException("Element with id " + id + " not found");
        }
        return modelItem;
    }

    @Nonnull
    public Element getElement(String id, Element contextElement) {
        if (identifierScope == IdentifierScope.Flat) {
            return getElement(id);
        }
        String contextElementId = findIdentifier(contextElement);
        if (contextElementId == null) {
            throw new NoSuchElementException("Element " + contextElement + " is not registered");
        }
        Set<String> possibleIds = IdentifierReducer.reduceString(contextElementId + "." + id);
        for (String possibleId : possibleIds) {
            Element modelItem = this.elementsByIdentifier.get(possibleId);
            if (modelItem != null) {
                return modelItem;
            }
        }
        throw new NoSuchElementException("Element with id " + id + " inside " + contextElement + " not found");
    }

    @Nonnull
    public ModelItem get(String id) {
        Relationship relationship = this.relationshipsByIdentifier.get(id);
        return Objects.requireNonNullElseGet(relationship, () -> getElement(id));
    }
}