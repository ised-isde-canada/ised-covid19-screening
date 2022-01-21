#!/bin/bash
while getopts ":p:h" opt; do
  case ${opt} in
    p )
      SEARCH_PATH=$OPTARG
      ;;
    b )
      BUILD_DIR=$OPTARG
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

if [[ $BUILD_DIR == "" ]]; then
  BUILD_DIR='build'
fi

for file in $(find $SEARCH_PATH -name build.toml | xargs realpath); do
  path=$(dirname $file)
  echo $path
  cd $path
  sam build -b ./$BUILD_DIR/ -u
  if [[ $? -ne 0 ]]; then
    echo "Failed to Build SAM for $path"
    exit 1
  fi
  ## Below needed as sam does not copy *.so libs into builds https://github.com/aws/aws-sam-cli/issues/1360
  LIBRARY_FILES=(./src/lib/*.so)
  if [[ -f $LIBRARY_FILES ]]; then
    cp ./src/lib/*.so ./$BUILD_DIR/*/lib
  fi
done