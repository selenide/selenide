#!/usr/bin/env node

process.stderr.write('[selenide-cli] This package has moved to "@selenide/cli". ' +
  'Please run "npm install -g @selenide/cli" instead.\n');

require('@selenide/cli/bin/selenide.js');
