#!/bin/bash
while getopts ":b:a:d:e:h" opt; do
  case ${opt} in
    a )
      APP_PATH=$OPTARG
      ;;
    b )
      BUILD_PATH=$OPTARG
      ;;
    d )
      BUILD_ARTIFACTS_DESTINATION=$OPTARG
      ;;
    e )
      ENVIRONMENT=$OPTARG
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

if [[ $APP_PATH == "" ]]; then
  echo "Supply App Path"
  exit 1
fi

if [[ $BUILD_PATH == "" ]]; then
  echo "Supply Build Path"
  exit 1
fi

if [[ $BUILD_ARTIFACTS_DESTINATION == "" ]]; then
  echo "Supply Build Artifacts Destination"
  exit 1
fi

if [[ $ENVIRONMENT == "" ]]; then
  echo "Supply Target Environment (dev, test, prod)"
  exit 1
fi

cd $APP_PATH
mkdir -p $BUILD_ARTIFACTS_DESTINATION

npm install
npm run build:$ENVIRONMENT

ls -la $BUILD_PATH/
cp -a $BUILD_PATH/. $BUILD_ARTIFACTS_DESTINATION/
ls -la $BUILD_ARTIFACTS_DESTINATION/

rm -fr $APP_PATH/node_modules