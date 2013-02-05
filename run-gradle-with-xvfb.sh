#!/bin/bash

function start_xvfb_if_needed() {
  if [ -z "$DISPLAY" ]; then
    export DISPLAY=$1
    Xvfb $DISPLAY &
    SELENIDE_XVFB_PID=$!
  fi
}

function kill_xvfb_if_needed() {
  if [ -n "$SELENIDE_XVFB_PID" ]; then
    kill $SELENIDE_XVFB_PID
  fi
}

start_xvfb_if_needed $1
./gradle
RESULT=$?
kill_xvfb_if_needed
exit $RESULT

