package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.model.Element;
import com.structurizr.model.ModelItem;
import com.structurizr.model.Relationship;

import javax.annotation.Nonnull;
import java.util.*;

class IdentifiersRegister {

    private final BidirectionalMap<String, Element> elementsByIdentifier = new BidirectionalMap<>();
    private final BidirectionalMap<String, Relationship> relationshipsByIdentifier = new BidirectionalMap<>();
    private IdentifierScope identifierScope = IdentifierScope.Flat;

    public void setIdentifierScope(IdentifierScope identifierScope) {
        this.identifierScope = identifierScope;
    }

    public void register(@Nonnull String id, @Nonnull Element element) {
        if (identifierScope == IdentifierScope.Hierarchical) {
            id = calculateHierarchicalIdentifier(id, element);
        }
        elementsByIdentifier.put(id, element);
    }

    public void register(@Nonnull String id, @Nonnull Relationship relationship) {
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

    private String findIdentifier(Element element) {
        return elementsByIdentifier.getKeyByValue(element);
    }

    @Nonnull
    public Element getElement(String id) {
        Element modelItem = this.elementsByIdentifier.getValueByKey(id);
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
            Element modelItem = this.elementsByIdentifier.getValueByKey(possibleId);
            if (modelItem != null) {
                return modelItem;
            }
        }
        throw new NoSuchElementException("Element with id " + id + " inside " + contextElement + " not found");
    }

    @Nonnull
    public ModelItem get(String id) {
        Relationship relationship = this.relationshipsByIdentifier.getValueByKey(id);
        return Objects.requireNonNullElseGet(relationship, () -> getElement(id));
    }
}