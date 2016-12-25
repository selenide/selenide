#!/usr/bin/env bash

HERE="`dirname \"$0\"`"				# relative
HERE="`( cd \"$HERE\" && pwd )`" 	# absolutized and normalized
if [ -z "$HERE" ] ; then
	# error; for some reason, the path is not accessible
	# to the script (e.g. permissions re-evaled after suid)
	exit 1  # fail
fi

ARTIFACTS_DIR="${HERE}/build/reports/"
ARTIFACTS_FILE=${TRAVIS_JOB_NUMBER}_log.tar.gz

ls $ARTIFACTS_DIR
echo "COMPRESSING build artifacts."
cd $ARTIFACTS_DIR
tar -zcvf $ARTIFACTS_FILE *
# upload to http://transfer.sh
echo "Uploading to transfer.sh"
curl --upload-file $ARTIFACTS_FILE http://transfer.sh