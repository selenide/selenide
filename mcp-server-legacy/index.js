#!/usr/bin/env node

process.stderr.write('[selenide-mcp] This package has moved to "@selenide/mcp". ' +
  'Please update your MCP config to use "@selenide/mcp" instead.\n');

require('@selenide/mcp');
