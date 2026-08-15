#!/usr/bin/env node
'use strict';

const { spawnSync } = require('node:child_process');
const fs = require('node:fs');
const path = require('node:path');

// The single fat JAR bundled in this package.
const jarDir = path.join(__dirname, '..', 'jar');
const jar = fs.readdirSync(jarDir).find((f) => f.endsWith('.jar'));
if (!jar) {
  console.error('selenide-cli: bundled JAR not found in ' + jarDir);
  process.exit(1);
}

// An explicit path (as opposed to a bare command) isn't resolved against PATHEXT by
// spawnSync/CreateProcess on Windows, so a correctly-set JAVA_HOME would otherwise fail with
// ENOENT - misreported below as "Java is required", which is backwards.
const javaExecutable = process.platform === 'win32' ? 'java.exe' : 'java';
const javaBin = process.env.JAVA_HOME
  ? path.join(process.env.JAVA_HOME, 'bin', javaExecutable)
  : 'java';

const result = spawnSync(javaBin, ['-jar', path.join(jarDir, jar), ...process.argv.slice(2)], {
  stdio: 'inherit',
});

if (result.error) {
  if (result.error.code === 'ENOENT') {
    console.error('selenide-cli: Java 17+ is required. Install a JDK or set JAVA_HOME.');
  } else {
    console.error('selenide-cli: failed to launch java — ' + result.error.message);
  }
  process.exit(1);
}
process.exit(result.status === null ? 1 : result.status);
