#!/bin/bash
while getopts ":p:h" opt; do
  case ${opt} in
    p )
      SEARCH_PATH=$OPTARG
      ;;
    \? )
      echo "Invalid option: $OPTARG" 1>&2
      ;;
    : )
      echo "Invalid option: $OPTARG requires an argument" 1>&2
      ;;
  esac
done
shift $((OPTIND -1))

for file in $(find $SEARCH_PATH -name pom.xml | xargs realpath); do
  path=$(dirname $file)
  echo $path
  cd $path

  mvn -DskipTests=true clean package
  if [[ $? -ne 0 ]]; then
    echo "Failed to rum mvn package for $path"
    exit 1
  fi
done