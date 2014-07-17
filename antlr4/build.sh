#!/usr/bin/env bash

export CLASSPATH=".:/opt/lib/antlr-4.2-complete.jar:$CLASSPATH"

wd=$(cd "`dirname $0`/.."; pwd)
target="$wd/app/cm/antlr4/sql"

rm -fr $target
#java -jar /opt/lib/antlr-4.2-complete.jar -package 'cm.antlr4.sql' -o $target $wd/antlr4/Sql.g4 
java -jar /opt/lib/antlr-4.2-complete.jar -package 'cm.antlr4.sql' -o $target -visitor $wd/antlr4/Sql.g4 
