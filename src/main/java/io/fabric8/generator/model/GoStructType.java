package io.fabric8.generator.model;

public class GoStructType {
    private String name;
    private Boolean namespaced;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNamespaced() {
        return namespaced;
    }

    public void setNamespaced(Boolean namespaced) {
        this.namespaced = namespaced;
    }
}
