# Go Struct to POJO Generator using Fabric8 Model Generators

[![CircleCI](https://circleci.com/gh/rohanKanojia/fabric8-gostruct2pojogenerator/tree/master.svg?style=svg)](https://app.circleci.com/pipelines/github/rohanKanojia/fabric8-gostruct2pojogenerator)
![License](https://img.shields.io/github/license/rohanKanojia/fabric8-gostruct2pojogenerator)

This project is a demonstration of how you can generate POJOs for your Custom Resources if you already have go structs for your Custom Resource types. You can then easily use there generated POJOs in [Fabric8 Kubernetes Client typed API](https://github.com/fabric8io/kubernetes-client/blob/doc/cheat-sheet/doc/CHEATSHEET.md#customresource-typed-api) for Custom Resources. We at [Fabric8 Kubernetes Client](https://github.com/fabric8io/kubernetes-client) team have been generating POJOs for Kubernetes resources from go structs. We also have extensions for Knative, Tekton and Service Catalog which generate model on the same principle. This project just uses the generators used by Fabric8 Kubernetes Client and tries to provide a generic way to generate your own Custom Resource POJOs by just providing simple configuration file.

## How to Build?
You just need to do a basic `mvn compile` or `mvn clean install` to build this project.

## How to Generate POJOs for Custom Resources from Go Structs?
You just need to provide a basic JSON file with details of where your go packages are 
located, what package shall your package go to? You need to create `input.json` file
inside `src/main/resources/generator/` directory. Let's see an example of structs 
being provided inside official [Kubernetes Sample Controller](https://github.com/kubernetes/sample-controller/blob/master/pkg/apis/samplecontroller/v1alpha1/types.go) repository:

This is how your `input.json` should look like:
```json
{
    "name": "SampleController",
    "packages": [
    {
        "name":"samplecontroller",
        "goPackage": "k8s.io/sample-controller/pkg/apis/samplecontroller/v1alpha1",
        "javaPackage": "io.kubernetes.samplecontroller.api.model",
        "apiGroup":"samplecontroller.k8s.io",
        "apiVersion": "v1alpha1",
        "types": [
           {"name":"Foo", "namespaced":true}
        ]
    }
    ]
}
```
You need to provide this minimal JSON file while running the jar-with-deps generated in this project:
```bash
java -jar -DinputFile=input.json kubernetes-model-code-generator-1.0.0-SNAPSHOT-jar-with-dependencies.jar
```
Once build is finished. You'll notice that it created a folder with model contents in the directory your executed jar:
```
fabric8-gostruct2pojogenerator-test : $ tree SampleController/ -L 1
SampleController/
├── generator
├── model
└── pom.xml
```

You can find your POJOs in `model/target/generated-sources` directory:
```
fabric8-gostruct2pojogenerator-test : $ tree SampleController/model/target/generated-sources/
SampleController/model/target/generated-sources/
├── annotations
│   └── io
│       ├── fabric8
│       │   └── kubernetes
│       │       └── api
│       │           └── model
│       │               ├── DoneableModelSchema.java
│       │               ├── ModelSchemaBuilder.java
│       │               ├── ModelSchemaFluentImpl.java
│       │               └── ModelSchemaFluent.java
│       └── kubernetes
│           └── samplecontroller
│               └── api
│                   └── model
│                       ├── DoneableFoo.java
│                       ├── DoneableFooList.java
│                       ├── DoneableFooSpec.java
│                       ├── DoneableFooStatus.java
│                       ├── FooBuilder.java
│                       ├── FooFluentImpl.java
│                       ├── FooFluent.java
│                       ├── FooListBuilder.java
│                       ├── FooListFluentImpl.java
│                       ├── FooListFluent.java
│                       ├── FooSpecBuilder.java
│                       ├── FooSpecFluentImpl.java
│                       ├── FooSpecFluent.java
│                       ├── FooStatusBuilder.java
│                       ├── FooStatusFluentImpl.java
│                       └── FooStatusFluent.java
└── io
    ├── fabric8
    │   └── kubernetes
    │       └── api
    │           └── model
    │               └── ModelSchema.java
    └── kubernetes
        └── samplecontroller
            └── api
                └── model
                    ├── Foo.java
                    ├── FooList.java
                    ├── FooSpec.java
                    └── FooStatus.java

19 directories, 25 files
```
