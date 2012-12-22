#!/bin/bash

curdir="`pwd`"
cd "`dirname $0`"
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
echo "Deleting existing release dir (Psychobunnies-1.0)..."
rm -rf Psychobunnies-1.0 2>&1 >/dev/null
mv $dir Psychobunnies-1.0
echo "Creating tar..."
tar -cz Psychobunnies-1.0 > Psychobunnies.tar.gz
echo "Creating zip..."
rm -rf Psychobunnies.zip 2>&1 >/dev/null
zip -q -r --symlinks Psychobunnies Psychobunnies-1.0
