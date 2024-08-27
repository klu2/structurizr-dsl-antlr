package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.model.Relationship;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

class DslContextUtils {

    /**
     * @param relationship the original relationship
     * @return a set with the original relationship and all relationships linked to it (i.e. implied and replicated relationships)
     */
    public static Set<Relationship> getConnectedRelationships(Relationship relationship) {
        Set<Relationship> modelItems = new LinkedHashSet<>();
        modelItems.add(relationship);
        relationship.getModel().getRelationships().stream()
                .filter(r -> relationship.getId().equals(r.getLinkedRelationshipId()))
                .forEach(modelItems::add);
        return unmodifiableSet(modelItems);
    }
}
