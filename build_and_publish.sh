#!/bin/bash
export GPG_TTY=$(tty)
mvn license:format 
mvn release:prepare
git commit -a
git push -f -u origin master
 mvn clean deploy release:perform  -Possrh -Dgpg.ossrh.passphrase=$1
