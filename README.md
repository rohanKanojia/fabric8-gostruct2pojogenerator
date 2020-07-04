# Go Struct to POJO Generator using Fabric8 Model Generators

[![CircleCI](https://circleci.com/gh/rohanKanojia/fabric8-gostruct2pojogenerator/tree/master.svg?style=svg)](https://app.circleci.com/pipelines/github/rohanKanojia/fabric8-gostruct2pojogenerator)

This project is a demonstration of how you can generate POJOs for your Custom Resources if you already have go structs for your Custom Resource types. You can then easily use there generated POJOs in [Fabric8 Kubernetes Client typed API](https://github.com/fabric8io/kubernetes-client/blob/doc/cheat-sheet/doc/CHEATSHEET.md#customresource-typed-api) for Custom Resources. We at [Fabric8 Kubernetes Client](https://github.com/fabric8io/kubernetes-client) team have been generating POJOs for Kubernetes resources from go structs. We also have extensions for Knative, Tekton and Service Catalog which generate model on the same principle. This project just uses the generators used by Fabric8 Kubernetes Client and tries to provide a generic way to generate your own Custom Resource POJOs by just providing simple configuration file.

## How to Build?
You just need to do a basic `mvn compile` or `mvn clean install` to build this project.

## How to Generate POJOs for Custom Resources from Go Structs?
You just need to provide a basic JSON file with details of where your go packages are 
located, what package shall your package go to? You need to create `input.json` file
inside `src/main/resources/generator/` directory. Let's see an example of structs 
being provided inside official [Kubernetes Sample Controller](https://github.com/kubernetes/sample-controller/blob/master/pkg/apis/samplecontroller/v1alpha1/types.go) repository:

This is how your `src/main/resources/generator/input.json` should look like:
```
{
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
Once you have provided this minimal JSON file, you can issue this command to generate your POJOs. This basically runs `GoScriptCreator` class which modifies `cmd/generate/generate.go` file according to the configuration provided and triggers a `make`:
```
~/go/src/github.com/rohanKanojia/code-generation-using-fabric8 : $ mvn exec:java -Dexec.mainClass="io.fabric8.generator.GoScriptCreator"
[INFO] Scanning for projects...
[INFO] 
[INFO] -------------< io.fabric8:kubernetes-model-code-generator >-------------
[INFO] Building Fabric8 :: Kubernetes Model :: Code Generator 1.0.0-SNAPSHOT
[INFO] -------------------------------[ bundle ]-------------------------------
[INFO] 
[INFO] --- exec-maven-plugin:1.6.0:java (default-cli) @ kubernetes-model-code-generator ---
Jun 13, 2020 3:08:14 PM io.fabric8.generator.GoScriptCreator loadGoGenerateTemplateAndApply
INFO: Writing: /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/cmd/generate/generate.go
GOBUILD: CGO_ENABLED=0 GO15VENDOREXPERIMENT=1 go build -a ./cmd/generate/generate.go
GOBUILD: ./generate > src/main/resources/schema/kube-schema.json
GOBUILD: ./generate validation > src/main/resources/schema/validation-schema.json
GOBUILD: mvn clean install
GOBUILD: [INFO] Scanning for projects...
GOBUILD: [INFO] 
GOBUILD: [INFO] -------------< io.fabric8:kubernetes-model-code-generator >-------------
GOBUILD: [INFO] Building Fabric8 :: Kubernetes Model :: Code Generator 1.0.0-SNAPSHOT
GOBUILD: [INFO] -------------------------------[ bundle ]-------------------------------
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ kubernetes-model-code-generator ---
GOBUILD: [INFO] Deleting /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/target
GOBUILD: [INFO] 
GOBUILD: [INFO] --- jsonschema2pojo-maven-plugin:0.4.23:generate (default) @ kubernetes-model-code-generator ---
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-antrun-plugin:3.0.0:run (default) @ kubernetes-model-code-generator ---
GOBUILD: [INFO] Executing tasks
GOBUILD: [WARNING]      [echo] removing the duplicate generated class
GOBUILD: [INFO] Executed tasks
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-resources-plugin:3.1.0:resources (default-resources) @ kubernetes-model-code-generator ---
GOBUILD: [WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
GOBUILD: [INFO] Copying 8 resources
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ kubernetes-model-code-generator ---
GOBUILD: [INFO] Changes detected - recompiling the module!
GOBUILD: [WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
GOBUILD: [INFO] Compiling 12 source files to /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/target/classes
GOBUILD: [INFO] /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/target/generated-sources/annotations/io/kubernetes/mixedcase/api/model/ClusterTestTypeBuilder.java: Some input files use or override a deprecated API.
GOBUILD: [INFO] /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/target/generated-sources/annotations/io/kubernetes/mixedcase/api/model/ClusterTestTypeBuilder.java: Recompile with -Xlint:deprecation for details.
GOBUILD: [INFO] /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/target/generated-sources/annotations/io/kubernetes/mixedcase/api/model/ClusterTestTypeFluentImpl.java: Some input files use unchecked or unsafe operations.
GOBUILD: [INFO] /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/target/generated-sources/annotations/io/kubernetes/mixedcase/api/model/ClusterTestTypeFluentImpl.java: Recompile with -Xlint:unchecked for details.
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-resources-plugin:3.1.0:testResources (default-testResources) @ kubernetes-model-code-generator ---
GOBUILD: [WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
GOBUILD: [INFO] skip non existing resourceDirectory /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/src/test/resources
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-compiler-plugin:3.8.1:testCompile (default-testCompile) @ kubernetes-model-code-generator ---
GOBUILD: [INFO] No sources to compile
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-surefire-plugin:3.0.0-M4:test (default-test) @ kubernetes-model-code-generator ---
GOBUILD: [INFO] No tests to run.
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-bundle-plugin:4.2.1:bundle (default-bundle) @ kubernetes-model-code-generator ---
GOBUILD: [WARNING] Bundle io.fabric8:kubernetes-model-code-generator:bundle:1.0.0-SNAPSHOT : Split package, multiple jars provide the same package:io/fabric8/kubernetes/api/model
GOBUILD: Use Import/Export Package directive -split-package:=(merge-first|merge-last|error|first) to get rid of this warning
GOBUILD: Package found in   [Jar:., Jar:kubernetes-model-core]
GOBUILD: Class path         [Jar:., Jar:kubernetes-model-core, Jar:jackson-module-jaxb-annotations, Jar:jackson-annotations, Jar:jackson-core, Jar:jackson-databind, Jar:jakarta.xml.bind-api, Jar:jakarta.activation-api, Jar:javax.annotation-api, Jar:jaxb-api, Jar:kubernetes-model-common, Jar:lombok, Jar:builder-annotations, Jar:sundr-core, Jar:sundr-codegen, Jar:resourcecify-annotations, Jar:transform-annotations]
GOBUILD: [WARNING] Bundle io.fabric8:kubernetes-model-code-generator:bundle:1.0.0-SNAPSHOT : Unused Export-Package instructions: [io.fabric8.kubernetes.api.model.storage**] 
GOBUILD: [INFO] 
GOBUILD: [INFO] --- build-helper-maven-plugin:3.1.0:attach-artifact (attach-artifacts) @ kubernetes-model-code-generator ---
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-install-plugin:3.0.0-M1:install (default-install) @ kubernetes-model-code-generator ---
GOBUILD: [INFO] Installing /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/target/kubernetes-model-code-generator-1.0.0-SNAPSHOT.jar to /home/rohaan/.m2/repository/io/fabric8/kubernetes-model-code-generator/1.0.0-SNAPSHOT/kubernetes-model-code-generator-1.0.0-SNAPSHOT.jar
GOBUILD: [INFO] Installing /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/pom.xml to /home/rohaan/.m2/repository/io/fabric8/kubernetes-model-code-generator/1.0.0-SNAPSHOT/kubernetes-model-code-generator-1.0.0-SNAPSHOT.pom
GOBUILD: [INFO] Installing /home/rohaan/go/src/github.com/rohanKanojia/code-generation-using-fabric8/target/classes/schema/kube-schema.json to /home/rohaan/.m2/repository/io/fabric8/kubernetes-model-code-generator/1.0.0-SNAPSHOT/kubernetes-model-code-generator-1.0.0-SNAPSHOT-schema.json
GOBUILD: [INFO] 
GOBUILD: [INFO] --- maven-bundle-plugin:4.2.1:install (default-install) @ kubernetes-model-code-generator ---
GOBUILD: [INFO] Installing io/fabric8/kubernetes-model-code-generator/1.0.0-SNAPSHOT/kubernetes-model-code-generator-1.0.0-SNAPSHOT.jar
GOBUILD: [INFO] Writing OBR metadata
GOBUILD: [INFO] ------------------------------------------------------------------------
GOBUILD: [INFO] BUILD SUCCESS
GOBUILD: [INFO] ------------------------------------------------------------------------
GOBUILD: [INFO] Total time:  13.598 s
GOBUILD: [INFO] Finished at: 2020-06-13T15:09:26+05:30
GOBUILD: [INFO] ------------------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:13 min
[INFO] Finished at: 2020-06-13T15:09:26+05:30
[INFO] ------------------------------------------------------------------------
~/go/src/github.com/rohanKanojia/code-generation-using-fabric8 : $ 

```
Once build is finished you can find your POJOs in `target/generated-sources` directory:
```
~/go/src/github.com/rohanKanojia/code-generation-using-fabric8 : $ ls target/generated-sources/io/kubernetes/samplecontroller/api/model/
Foo.java  FooList.java  FooSpec.java  FooStatus.java
~/go/src/github.com/rohanKanojia/code-generation-using-fabric8 : $ tree target/generated-sources/
target/generated-sources/
├── annotations
│   └── io
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
    │               └── KubeSchema.java
    └── kubernetes
        └── samplecontroller
            └── api
                └── model
                    ├── Foo.java
                    ├── FooList.java
                    ├── FooSpec.java
                    └── FooStatus.java

15 directories, 21 files
```
If your project has some slightly complex structure and you have some internal dependencies too, you can provide them inside `input.json` file so that it adds your dependencies inside `cmd/generate/generate.go` script. Here is an example of generating POJOs for Knative model:
```
{
    "packages": [
    {
        "name":"knative_serving_v1",
        "goPackage": "github.com/knative/serving/pkg/apis/serving/v1",
        "javaPackage": "io.kubernetes.knative.api.model.serving.v1",
        "apiGroup":"knative.dev",
        "apiVersion": "v1",
        "types": [
           {"name":"Service", "namespaced":true},
           {"name":"Revision", "namespaced":true},
           {"name":"Configuration", "namespaced":true},
           {"name":"Route", "namespaced":true}
        ]
    },
    {
        "name":"knative_serving_v1beta1",
        "goPackage": "github.com/knative/serving/pkg/apis/serving/v1beta1",
        "javaPackage": "io.kubernetes.knative.api.model.serving.v1beta1",
        "apiGroup":"knative.dev",
        "apiVersion": "v1",
        "types": [
           {"name":"Service", "namespaced":true},
           {"name":"Revision", "namespaced":true},
           {"name":"Configuration", "namespaced":true},
           {"name":"Route", "namespaced":true}
        ]
    },
    {
        "name":"knative_eventing_v1beta1",
        "goPackage": "github.com/knative/eventing/pkg/apis/eventing/v1alpha1",
        "javaPackage": "io.kubernetes.knative.api.model.serving.v1beta1",
        "apiGroup":"knative.dev",
        "apiVersion": "v1",
        "types": [
           {"name":"Broker", "namespaced":true},
           {"name":"Trigger", "namespaced":true},
           {"name":"EventType", "namespaced":true}
        ]
    },
    {
        "name":"knative_messaging",
        "goPackage": "knative.dev/eventing/pkg/apis/messaging/v1alpha1",
        "javaPackage": "io.kubernetes.knative.api.model.messaging.v1alpha1",
        "apiGroup":"knative.dev",
        "apiVersion": "v1",
        "types": [
           {"name":"Channel", "namespaced":true},
           {"name":"InMemoryChannel", "namespaced":true},
           {"name":"Subscription", "namespaced":true}
        ]
    }
    ],
    "dependencies": 
    [
      {
          "name": "apis",
          "goPackage":"knative.dev/pkg/apis",
          "mapping": {
              "URL": "java.lang.String",
              "VolatileTime": "java.lang.String"
          }

      }
    ],
  "internalPackageMapping" : {
    "knative.dev": "io.fabric8.knative.internal"
  }
}
```
You can notice the additional `dependencies` field in which Knative dependencies are listed. It also allows manually mapping some structs to Java types if you're not satisfied with results. The `internalPackageMapping` field maps all go structs found inside `knative.dev` go path to `io.fabric8.knative.internal` package(i.e. all classes formed from `knative.dev` go path would go inside that package).

You can checkout other examples in `./input-samples` directory.

## Examples:

I have provided some examples of different input samples as different branches, you can check them out by checking out different branches:

- [Kubernetes Code Generator MixedCase](https://github.com/rohanKanojia/fabric8-gostruct2pojogenerator)
- [Knative Model Generator](https://github.com/rohanKanojia/fabric8-gostruct2pojogenerator/tree/knative-model)
- [Kubernetes Sample Controller](https://github.com/rohanKanojia/fabric8-gostruct2pojogenerator/tree/sample-controller)
- [Tekton Model Generator](https://github.com/rohanKanojia/fabric8-gostruct2pojogenerator/tree/Tekton)
