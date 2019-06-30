#!/bin/bash
project_dir="/$HOME/repos/proj-aw003/java/src"
cd $project_dir
echo "A compilar o projeto.."
javac -cp "/$HOME/repos/proj-aw003/java/jars/*" SparqlTests.java ScriptHandler.java AnnotationHandler.java WebService.java
echo "A correr o projeto..."
java -cp .:/home/aw003/jars/* SparqlTests