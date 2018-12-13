package org.vedas.storage.model;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

@NodeEntity
public class NamedEntity {
    @Id
    private String name;
    private String type;
    private String sentence;

    @Relationship("is")
    private List<NamedEntity> entities = new ArrayList<>();

    public NamedEntity(String name, String type, String sentence) {
        this.name = name;
        this.type = type;
        this.sentence = sentence;
    }

    public NamedEntity(String name, String type, String sentence, List<NamedEntity> entities) {
        this.name = name;
        this.type = type;
        this.sentence = sentence;
        this.entities = entities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public List<NamedEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<NamedEntity> entities) {
        this.entities = entities;
    }
}
