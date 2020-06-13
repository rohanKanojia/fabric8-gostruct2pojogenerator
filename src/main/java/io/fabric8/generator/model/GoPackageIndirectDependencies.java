package io.fabric8.generator.model;

import java.util.Map;

public class GoPackageIndirectDependencies {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoPackage() {
        return goPackage;
    }

    public void setGoPackage(String goPackage) {
        this.goPackage = goPackage;
    }

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    private String name;
    private String goPackage;
    private String javaPackage;
    private Map<String, String> mapping;


}
