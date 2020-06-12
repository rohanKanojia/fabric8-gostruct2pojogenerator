package io.fabric8.generator.model;

import java.util.List;

public class GoScriptInput {
    private List<GoPackage> packages;


    public List<GoPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<GoPackage> packages) {
        this.packages = packages;
    }
}
