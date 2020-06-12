package io.fabric8.generator.model;

import java.util.List;

public class GoPackage {
    private String name;
    private String goPackage;
    private String javaPackage;
    private String apiGroup;
    private String apiVersion;
    private List<GoStructType> types;

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

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public String getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(String apiGroup) {
        this.apiGroup = apiGroup;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public List<GoStructType> getTypes() {
        return types;
    }

    public void setTypes(List<GoStructType> types) {
        this.types = types;
    }


}
