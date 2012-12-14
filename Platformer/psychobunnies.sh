#!/bin/bash

cd $(dirname $0)
java -cp lib: -Djava.library.path=lib -jar Platformer.jar
