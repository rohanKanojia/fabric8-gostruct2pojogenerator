package io.fabric8.generator.model;

import java.util.List;
import java.util.Map;

public class GoScriptInput {
    private String name;
    private List<GoPackage> packages;
    private List<GoPackageIndirectDependencies> dependencies;
    private Map<String, String> internalPackageMapping;
    private Map<String, String> providedPackageMapping;

    public Map<String, String> getProvidedPackageMapping() {
        return providedPackageMapping;
    }

    public void setProvidedPackageMapping(Map<String, String> providedPackageMapping) {
        this.providedPackageMapping = providedPackageMapping;
    }

    public Map<String, String> getInternalPackageMapping() {
        return internalPackageMapping;
    }

    public void setInternalPackageMapping(Map<String, String> internalPackageMapping) {
        this.internalPackageMapping = internalPackageMapping;
    }


    public List<GoPackageIndirectDependencies> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<GoPackageIndirectDependencies> dependencies) {
        this.dependencies = dependencies;
    }


    public List<GoPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<GoPackage> packages) {
        this.packages = packages;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }
}
