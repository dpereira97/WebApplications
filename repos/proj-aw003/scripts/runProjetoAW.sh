#!/bin/bash
# project_dir="/$HOME/public_html"
project_dir="/$HOME/repos/proj-aw003/java/src"
cd $project_dir
echo "A compilar o projeto.."
# javac -cp "/home/aw003/jars/*" SparqlTests.java ScriptHandler.java AnnotationHandler.java
javac -cp "/home/aw003/repos/proj-aw003/java/jars/*" SparqlTests.java ScriptHandler.java AnnotationHandler.java
echo "A correr o projeto..."
#java -cp .:/home/aw003/jars/* SparqlTests
java -cp .:/home/aw003/repos/proj-aw003/java/jars/* SparqlTests
