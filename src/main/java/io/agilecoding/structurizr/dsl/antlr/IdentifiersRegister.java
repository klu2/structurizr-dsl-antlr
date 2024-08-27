package io.agilecoding.structurizr.dsl.antlr;

import com.structurizr.model.ModelItem;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

class IdentifiersRegister {

    private final Map<String, ModelItem> elementsByDslID = new HashMap<>();
    private IdentifierScope identifierScope;

    public IdentifierScope getIdentifierScope() {
        return identifierScope;
    }

    public void setIdentifierScope(IdentifierScope identifierScope) {
        this.identifierScope = identifierScope;
    }

    public void register(@Nonnull String id, @Nonnull ModelItem modelItem) {
        if (elementsByDslID.containsKey(id)) {
            throw new IllegalArgumentException("Element with id " + id + " already exists");
        }
        elementsByDslID.put(id, modelItem);
    }

    @Nonnull
    public ModelItem get(String id) {
        ModelItem modelItem = this.elementsByDslID.get(id);
        if (modelItem == null) {
            throw new RuntimeException("Element with id " + id + " not found");
        }
        return modelItem;
    }
}