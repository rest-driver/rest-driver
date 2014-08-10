#!/bin/bash

if [ "x`which curl`" = "x" ]; then
  echo "You need to install curl"
  exit
fi

WORKING_DIRECTORY=$1
VERSION=$2

# Wait for our new version to appear in Maven Central
for MODULE in "rest-driver" "rest-client-driver" "rest-server-driver"; do
  STATUS=`curl -sL -w "%{http_code}" -o /dev/null "http://search.maven.org/remotecontent?filepath=com/github/rest-driver/$MODULE/$VERSION/"
  while [ "$STATUS" != "200" ]; do
    echo "Checking for $MODULE at `date`"
    STATUS=`curl -sL -w "%{http_code}" -o /dev/null "http://search.maven.org/remotecontent?filepath=com/github/rest-driver/$MODULE/$VERSION/"`
    echo "Not found yet - waiting for 5 minutes"
    sleep 300
  done
done

perl -pi -e "s/\d+\.\d+\.\d+/$VERSION/g" README.md
git add README.md
git commit -m "Updating README.md to $VERSION"
git push origin master

mkdir -p $WORKING_DIRECTORY
cd $WORKING_DIRECTORY
git clone git@github.com:rest-driver/rest-driver.wiki.git $WORKING_DIRECTORY/rest-driver.wiki
cd $WORKING_DIRECTORY/rest-driver.wiki
find . -name "*.md" -type f -exec perl -pi -e "s/\d+\.\d+\.\d+/$VERSION/g" {} \;
git add .
git commit -m "Updating version number in wiki pages to $VERSION"
git push origin master
cd ../..
mvn clean javadoc:aggregate-jar
git clone git@github.com:rest-driver/rest-driver.github.com.git $WORKING_DIRECTORY/rest-driver.github.com
cd $WORKING_DIRECTORY/rest-driver.github.com
perl -pi -e "s/\d+\.\d+\.\d+/$VERSION/g" index.html
cd docs
git rm -r .
cd $WORKING_DIRECTORY/rest-driver.github.com
cp -R ../apidocs docs
git add .
git commit -m "Updating index page and JavaDoc to $VERSION"
git push origin master
