#!/bin/bash

mvn clean package -DskipTests


rm -rf migrated
cp -r blank-repo migrated
for f in $@; do 
java -jar target/xpdl-to-bpmn2-0.15-SNAPSHOT-jar-with-dependencies.jar $f migrated/src/main/resources/com/rhc/migration/
done

DIR=$(pwd)
cd migrated/
git init .
git add .
git commit -m'initial migration'
cd ..

echo "import project at $DIR/migrated"
