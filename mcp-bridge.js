#!/usr/bin/env node

/**
 * MCP Bridge - stdio to HTTP bridge for Cursor integration
 * 
 * This bridge allows Cursor to communicate with the Spring Boot MCP server
 * by translating stdio JSON-RPC messages to HTTP requests.
 */

const http = require('http');
const readline = require('readline');

const BASE_URL = 'http://localhost:8080/api/v1/mcp/tools';

// Create readline interface for stdin only (don't echo to stdout)
const rl = readline.createInterface({
  input: process.stdin,
  terminal: false
});

// Log to stderr (so it doesn't interfere with stdout communication)
function log(message) {
  console.error(`[MCP-Bridge] ${message}`);
}

// Make HTTP request to Spring Boot server
function makeHttpRequest(toolName, params) {
  return new Promise((resolve, reject) => {
    const url = toolName ? `${BASE_URL}/${toolName}` : BASE_URL;
    const method = toolName ? 'POST' : 'GET';
    const postData = toolName ? JSON.stringify(params) : null;

    const options = {
      method: method,
      headers: {
        'Content-Type': 'application/json',
        ...(postData && { 'Content-Length': Buffer.byteLength(postData) })
      }
    };

    const req = http.request(url, options, (res) => {
      let data = '';

      res.on('data', (chunk) => {
        data += chunk;
      });

      res.on('end', () => {
        try {
          const jsonData = JSON.parse(data);
          resolve(jsonData);
        } catch (error) {
          reject(new Error(`Failed to parse response: ${error.message}`));
        }
      });
    });

    req.on('error', (error) => {
      reject(error);
    });

    if (postData) {
      req.write(postData);
    }

    req.end();
  });
}

// Handle MCP protocol messages
async function handleMessage(message) {
  try {
    const request = JSON.parse(message);
    log(`Received request: ${request.method || 'unknown'}`);

    // Handle initialize
    if (request.method === 'initialize') {
      const response = {
        jsonrpc: '2.0',
        id: request.id,
        result: {
          protocolVersion: '2024-11-05',
          serverInfo: {
            name: 'beyondsight-mcp-server',
            version: '1.0.0'
          },
          capabilities: {
            tools: {}
          }
        }
      };
      process.stdout.write(JSON.stringify(response) + '\n');
      return;
    }

    // Handle tools/list
    if (request.method === 'tools/list') {
      const toolsData = await makeHttpRequest(null, null);
      const tools = Object.entries(toolsData.tools || {}).map(([name, tool]) => ({
        name: name,
        description: tool.description || '',
        inputSchema: {
          type: 'object',
          properties: Object.entries(tool.parameters || {}).reduce((acc, [paramName, paramDesc]) => {
            acc[paramName] = {
              type: 'string',
              description: paramDesc
            };
            return acc;
          }, {}),
          required: Object.keys(tool.parameters || {})
        }
      }));

      const response = {
        jsonrpc: '2.0',
        id: request.id,
        result: {
          tools: tools
        }
      };
      process.stdout.write(JSON.stringify(response) + '\n');
      return;
    }

    // Handle tools/call
    if (request.method === 'tools/call') {
      const toolName = request.params.name;
      const toolParams = request.params.arguments || {};

      const result = await makeHttpRequest(toolName, toolParams);

      const response = {
        jsonrpc: '2.0',
        id: request.id,
        result: {
          content: [
            {
              type: 'text',
              text: JSON.stringify(result.result || result, null, 2)
            }
          ]
        }
      };
      process.stdout.write(JSON.stringify(response) + '\n');
      return;
    }

    // Handle notifications/initialized
    if (request.method === 'notifications/initialized') {
      // No response needed for notifications
      log('Received notifications/initialized');
      return;
    }

    // Unknown method
    log(`Unknown method: ${request.method}`);
    const errorResponse = {
      jsonrpc: '2.0',
      id: request.id,
      error: {
        code: -32601,
        message: `Method not found: ${request.method}`
      }
    };
    process.stdout.write(JSON.stringify(errorResponse) + '\n');

  } catch (error) {
    log(`Error handling message: ${error.message}`);
    const errorResponse = {
      jsonrpc: '2.0',
      id: null,
      error: {
        code: -32603,
        message: `Internal error: ${error.message}`
      }
    };
    process.stdout.write(JSON.stringify(errorResponse) + '\n');
  }
}

// Track pending requests
let pendingRequests = 0;
let stdinClosed = false;

// Listen for messages on stdin
log('MCP Bridge started');
log(`Connecting to Spring Boot server at ${BASE_URL}`);

rl.on('line', (line) => {
  if (line.trim()) {
    pendingRequests++;
    handleMessage(line).finally(() => {
      pendingRequests--;
      if (stdinClosed && pendingRequests === 0) {
        log('MCP Bridge stopped');
        process.exit(0);
      }
    });
  }
});

rl.on('close', () => {
  stdinClosed = true;
  if (pendingRequests === 0) {
    log('MCP Bridge stopped');
    process.exit(0);
  }
});

// Handle process termination
process.on('SIGINT', () => {
  log('Received SIGINT, shutting down');
  process.exit(0);
});

process.on('SIGTERM', () => {
  log('Received SIGTERM, shutting down');
  process.exit(0);
});

