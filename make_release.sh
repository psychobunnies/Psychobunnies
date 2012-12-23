#!/bin/bash

curdir="`pwd`"
psychodir="`dirname $0`"
cd "$psychodir"

version="`cat VERSION`"
echo "Current version: $version"
echo -n "New version [$version]: "
read -r newversion
if [ "$newversion" == "" ] || [ "$newversion" == "$version" ]
then
  newversion="$version"
  echo "Making a copy of release $version."
else
  echo -n "Are you sure [y/N]? "
  read -r response
  if [ "$response" == "y" ] || [ "$response" == "Y" ] || [ "$response" == "" ]
  then
    echo -n "Did you package a new JAR [y/N]? "
    read -r response
    echo "Bumping version to $newversion."
    if [ "$response" == "y" ] || [ "$response" == "Y" ] || [ "$response" == "" ]
    then
      echo "$newversion" > VERSION
      git commit VERSION -m "Bumped version to $newversion."
      git tag "v$newversion"
    else
      echo "Making a copy of release $version."
    fi
  else
    echo "Making a copy of release $version."
  fi
fi


dir="`mktemp -d /tmp/psycho.XXXXX`"
echo "Building release in $dir..."
cp -a release/* $dir
cd Platformer
echo "Copying libraries..."
cp -a lib $dir
echo "Copying assets..."
cp -a new-assets $dir
ass=$dir/new-assets
echo "Stripping unnecessary assets..."
rm -f $ass/*.psd $ass/*/*.psd $ass/*/*/*.psd $ass/*/*/*/*.psd $ass/*/*/*/*/*.psd
cd "$curdir"

releasedir="Psychobunnies-$newversion"
echo "Deleting existing release dir ($releasedir)..."
rm -rf "$releasedir" 2>&1 >/dev/null
mv $dir "$releasedir"
echo "Creating tar $releasedir.tar.gz..."
tar -cz "$releasedir" > "$releasedir".tar.gz
echo "Creating zip $releasedir.zip..."
rm -rf "$releasedir".zip 2>&1 >/dev/null
zip -q -r --symlinks "$releasedir".zip "$releasedir"

echo "Cleaning up..."
rm -rf "$releasedir" 2>&1 >/dev/null

zip="$releasedir".zip
if [ "$newversion" != "$version" ]
then
  echo "Updating site..."
  cp "$zip" "$psychodir/site/releases/"
  cd "$psychodir/site/releases"
  rm latest.zip
  echo "Fixing latest link..."
  ln -s "$zip" latest.zip
  git add "$zip"
  git commit "$zip" latest.zip -m "Uploaded version $newversion"
  git pull --rebase
  git push
  cd ../..
  git commit site -m "Bumped site version to $newversion"
  git pull --rebase
  git push
fi
