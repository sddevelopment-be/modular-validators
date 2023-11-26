#!/usr/bin/bash

echo '  (>^v^)> -{ Rebuilding JavaDocs } '
rm -rf ./docs/javadoc/*

mvn javadoc:javadoc
