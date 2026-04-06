#!/usr/bin/env node

const { execSync, spawn } = require('child_process');
const { existsSync, mkdirSync, createWriteStream } = require('fs');
const { join } = require('path');
const { get } = require('https');
const { homedir } = require('os');

const VERSION = require('./package.json').version;
const CACHE_DIR = join(homedir(), '.selenide-mcp');
const JAR_NAME = `selenide-mcp-${VERSION}.jar`;
const JAR_PATH = join(CACHE_DIR, JAR_NAME);
const MAVEN_BASE = 'https://repo1.maven.org/maven2/com/codeborne/selenide-mcp';
const JAR_URL = `${MAVEN_BASE}/${VERSION}/${JAR_NAME}`;

function checkJava() {
  try {
    const output = execSync('java -version 2>&1', { encoding: 'utf8' });
    const match = output.match(/version "(\d+)/);
    if (match && parseInt(match[1]) >= 17) return;
    process.stderr.write('Error: Java 17+ is required. Found: ' + output.split('\n')[0] + '\n');
    process.exit(1);
  } catch {
    process.stderr.write('Error: Java not found in PATH.\n');
    process.stderr.write('Install Java 17+: https://adoptium.net/\n');
    process.exit(1);
  }
}

function downloadJar() {
  return new Promise((resolve, reject) => {
    if (existsSync(JAR_PATH)) return resolve();
    mkdirSync(CACHE_DIR, { recursive: true });
    process.stderr.write(`Downloading selenide-mcp ${VERSION}...\n`);

    function download(url) {
      get(url, (res) => {
        if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
          return download(res.headers.location);
        }
        if (res.statusCode !== 200) {
          return reject(new Error(`Download failed: HTTP ${res.statusCode} from ${url}`));
        }
        const file = createWriteStream(JAR_PATH);
        res.pipe(file);
        file.on('finish', () => {
          file.close();
          process.stderr.write('Download complete.\n');
          resolve();
        });
      }).on('error', reject);
    }
    download(JAR_URL);
  });
}

async function main() {
  checkJava();
  await downloadJar();

  const args = process.argv.slice(2);
  const child = spawn('java', ['-jar', JAR_PATH, ...args], {
    stdio: ['pipe', 'pipe', 'inherit']
  });

  process.stdin.pipe(child.stdin);
  child.stdout.pipe(process.stdout);

  child.on('exit', (code) => process.exit(code || 0));
  process.on('SIGTERM', () => child.kill('SIGTERM'));
  process.on('SIGINT', () => child.kill('SIGINT'));
}

main().catch((err) => {
  process.stderr.write('Error: ' + err.message + '\n');
  process.exit(1);
});
