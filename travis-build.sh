#!/bin/bash
set -ev
./gradlew build asciidoc
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
  ./gradlew publishGhPages
fi
