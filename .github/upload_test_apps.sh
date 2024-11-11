#!/bin/bash

# Ensure BS_KEY is set
if [ -z "$BS_KEY" ]; then
  echo "Error: Please set the BS_KEY environment variable."
  exit 1
fi

# BrowserStack username
USERNAME="githubactions_qxmgVeB"

# Define app URLs and custom IDs
declare -A apps
apps=(
  ["ApiDemos"]="https://github.com/appium/appium/raw/master/packages/appium/sample-code/apps/ApiDemos-debug.apk"
  ["Android_SwagLabs"]="https://github.com/saucelabs/my-demo-app-rn/releases/download/v1.3.0/Android-MyDemoAppRN.1.3.0.build-244.apk"
  ["IOS_SwagLabs"]="https://github.com/saucelabs/my-demo-app-rn/releases/download/v1.3.0/iOS-Real-Device-MyRNDemoApp.1.3.0-162.ipa"
)

# Upload apps to BrowserStack
for custom_id in "${!apps[@]}"; do
  app_url=${apps[$custom_id]}

  echo "Uploading $custom_id..."

  response=$(curl -u "$USERNAME:$BS_KEY" \
    -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
    -F "url=$app_url" \
    -F "custom_id=$custom_id")

  echo "Response for $custom_id: $response"
  echo "--------------------------------------------"
done

