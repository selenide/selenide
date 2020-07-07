#!/usr/bin/env bash

HERE="`dirname \"$0\"`"				# relative
HERE="`( cd \"$HERE\" && pwd )`" 	# absolutized and normalized
if [ -z "$HERE" ] ; then
	# error; for some reason, the path is not accessible
	# to the script (e.g. permissions re-evaled after suid)
	exit 1  # fail
fi

ARTIFACTS_DIR="${HERE}/build/reports/"
ARTIFACTS_FILE=${TRAVIS_BUILD_NUMBER}_tests_report.tar.gz
FAILURE_MESSAGE="\nBUILD FAILED. PLEASE READ CHECKSTYLE AND TEST RESULT REPORTS ON THE LINK ABOVE OR IN A LOG ABOVE\n"
SUCCESS_MESSAGE="\nYOU CAN FIND TEST RESULTS REPORTS ON THE LINK ABOVE\n"

echo "COMPRESSING build artifacts."
cd "${ARTIFACTS_DIR}" || exit 2
tar -zcf "${ARTIFACTS_FILE}" *

echo "Uploading build artifacts"
curl -F "file=@${ARTIFACTS_FILE}" -s -w "\n"  https://file.io
if [ $TRAVIS_TEST_RESULT -eq 0 ];
then
	echo -e "${SUCCESS_MESSAGE}"
else
	echo -e "${FAILURE_MESSAGE}"
fi
