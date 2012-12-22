#!/bin/bash

curdir="`pwd`"
cd "`dirname $0`"

version="`cat VERSION`"
echo "Current version: $version"
echo -n "New version [$version]: "
read -r newversion
if [ "$newversion" == "" ] || [ "$newversion" == "$version" ]
then
  echo "Making a copy of release $version."
else
  echo -n "Are you sure [y/N]? "
  read -r response
  if [ "$response" == "y" ] || [ "$response" == "Y" ] || [ "$response" == "" ]
  then
    echo "Bumping version to $newversion."
    echo "$newversion" > VERSION
    git commit VERSION -m "Bumped version to $newversion."
    version=$newversion
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
cd $curdir

releasedir="Psychobunnies-$version"
echo "Deleting existing release dir (Psychobunnies-1.0)..."
rm -rf "$releasedir" 2>&1 >/dev/null
mv $dir "$releasedir"
echo "Creating tar $releasedir.tar.gz..."
tar -cz "$releasedir" > "$releasedir".tar.gz
echo "Creating zip $releasedir.zip..."
rm -rf "$releasedir".zip 2>&1 >/dev/null
zip -q -r --symlinks "$releasedir".zip "$releasedir"
