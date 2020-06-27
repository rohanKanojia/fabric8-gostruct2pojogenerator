echo "cleaning up old generate ..."
rm cmd/generate/generate.go
mvn clean install
echo "generating new generate script based in input.json"
mvn exec:java -Dexec.mainClass="io.fabric8.generator.GoScriptCreator"
echo "running go build"
make
echo "SUCCESS. You can find your generated POJOs in target/generated-sources"
tree target/generated-sources
