# BeyondSight

The best friend of the modern developer pocket!

## Why BeyondSight?

**Stop forcing your AI to guess. Start giving it answers.**

When you ask an AI assistant ("What happens if I change this method?"), it kicks off a slow, expensive, and unreliable process. The LLM simulates a brute-force `grep` search, burning thousands of API tokens to read files and *guess* your code's structure. This leads to high API bills and missed dependencies.

BeyondSight is a **precision-guided context provider** for AI-driven development. It acts as the "impact analysis brain" for your AI.

It fundamentally changes the economics and reliability of AI-assisted coding by replacing this blind, expensive search with a single, intelligent query.

**The Old Way (Blind & Expensive)**
>
> 1. AI Assistant makes a **blizzard of `grep` calls**.
> 2. It **burns thousands of tokens** reading entire files.
> 3. It tries to *guess* relationships, often missing transitive dependencies.
> 4. **Result:** Slow, expensive, and inaccurate.

**The BeyondSight Way (Precise & Cheap)**
>
> 1. AI Assistant makes **one single MCP tool call**.
> 2. BeyondSight traverses its graph database and returns a **perfect, pre-analyzed impact report** (all files, exact line numbers, and all transitive dependencies).
> 3. **Result:** Instant, cheap, and 100% accurate.

This approach provides clear, immediate benefits:

* 💰 **Massive Cost Savings:** Slashes your LLM API bills by replacing token-heavy searches with a single, lightweight tool call.
* ⚡ **Blazing Speed:** The AI gets a complete dependency map instantly, eliminating search, read, and parse latency.
* 🎯 **Sniper-Like Accuracy:** Delivers a graph-verified impact report with **zero false positives** and **complete transitive dependencies**, enabling safe, reliable AI-powered refactoring.

### Real-World Use Cases

BeyondSight is designed to be the "code memory" for your AI assistant.

* **AI-Assisted Refactoring:** Ask, "AI, rename this `UserService` class." The AI queries BeyondSight and gets a *complete* list of *every* file and line number to change.
* **Assessing API Evolution:** Ask, "What's the impact of changing this method signature?" The AI instantly provides a full, transitive impact report, warning you of downstream breakages.
* **AI-Powered Code Review:** Ask, "Could this PR change break anything?" The AI uses BeyondSight to perform an instant, comprehensive impact analysis that's impossible to do manually.
* **Understanding Architecture:** Ask, "Show me all services that depend on our `PaymentModule`." The AI queries the graph to reveal hidden dependencies in seconds.

## Overview

BeyondSight is a code analysis server that acts as a high-speed "code intelligence layer" for AI assistants.

Its process is simple:

1. **Parses** your entire Java project into its core components.
2. **Models** all code relationships (classes, methods, fields) in a high-performance **Neo4j graph database**.
3. **Exposes** this complete dependency graph to LLMs via the **Model Context Protocol (MCP)**.

When you ask, "What happens if I change this?", your AI doesn't *search*—it *asks BeyondSight* and gets an instant, perfect, and graph-verified answer.

While the primary focus is serving LLMs via MCP, the powerful graph queries are also accessible via a traditional REST API for custom tooling or manual inspection.

## Features

* **Primary MCP Server for LLMs**: The core of the project. Provides a high-speed Model Context Protocol (MCP) server that allows AI assistants (like Cursor) to ask questions and receive instant impact analysis.
* **Pre-configured AI Tools**: Ships with ready-to-use MCP tools (`getClassImpact`, `getMethodImpact`, `getFieldImpact`) so the AI can immediately perform analysis without complex setup.
* **Deep Graph Database Analysis**: Automatically parses all Java source code and stores the *entire* codebase structure (classes, methods, fields, and their relationships) in a Neo4j graph database.
* **Full Transitive Dependency Tracking**: The graph model allows for instant discovery of *all* affected code paths, including multi-level "hidden" dependencies that primitive, file-based searches would miss.
* **Precise, Hierarchical Impact Reports**: Queries return structured, hierarchical reports of all affected classes and methods—**with exact line numbers**—in a format that is perfect for an AI to parse and act upon.
* **Secondary REST API Access**: Includes a comprehensive REST API for programmatic access to the impact analysis engine, allowing integration with non-MCP tools or custom developer scripts.

## Tech Stack

* **Spring Boot** 4.0.0-RC1
* **Java** 25
* **Neo4j** Graph Database
* **JavaParser** 3.27.1 for Java code parsing
* **Gradle** Build System
* **Docker Compose** for Neo4j setup

## Prerequisites

* Java 25
* Docker & Docker Compose
* Gradle (or use the included Gradle wrapper)

## Getting Started

### 1. Clone the Repository

```bash
git clone git@github.com:felipestanzani/beyondsight.git
cd beyondsight
```

### 2. Start Neo4j Database

```bash
docker-compose up -d
```

This will start Neo4j on:

* Bolt port: `7687`
* HTTP port: `7474`
* Username: `neo4j`
* Password: `notverysecret`

### 3. Build and Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 4. Configure MCP Server in Cursor (Optional)

To use BeyondSight's impact analysis tools directly within Cursor AI, you can configure the MCP server:

#### Step 1: Ensure the Application is Running

Make sure BeyondSight is running on `http://localhost:8080` (see step 3 above).

#### Step 2: Configure Cursor MCP Settings

1. Open Cursor and go to **Settings** → **Features** → **Model Context Protocol**
2. Add the following configuration to your MCP settings file (usually located at `~/.cursor/mcp.json`):

```json
{
  "mcpServers": {
    "beyondsight-mcp-server": {
      "transport": "sse",
      "url": "http://localhost:8080/sse",
      "capabilities": {
        "tool": true
      },
      "name": "beyondsight-mcp-server",
      "version": "1.0.0",
      "type": "SYNC",
      "instructions": "This server provides code impact change information",
      "sse-message-endpoint": "http://localhost:8080/mcp/message",
      "heartbeatIntervalMs": 15000,
      "reconnectAttempts": 5
    }
  }
}
```

**Note**: This configuration uses Server-Sent Events (SSE) to communicate with the BeyondSight server.

#### Step 3: Restart Cursor

After adding the configuration, restart Cursor for the changes to take effect.

#### Step 4: Verify MCP Integration

Once configured, you can use the MCP tools directly in Cursor's AI assistant.

### Available MCP Tools

BeyondSight provides the following MCP tools for impact analysis:

#### 1. getClassImpact

**Description**: Gets full transitive impact analysis for a class. Returns hierarchical structure showing all classes and methods affected by changing the class.

**Parameters**:

* `className` (string, required): Name of the class to analyze

**Returns**: Hierarchical structure showing all affected classes and methods

**Example**:

```java
getClassImpact(className: "UserService")
```

#### 2. getMethodImpact

**Description**: Gets full transitive impact analysis for a method. Returns hierarchical structure showing all classes and methods affected by changing the method.

**Parameters**:

* `methodSignature` (string, required): Signature of the method to analyze (e.g., `calculateTotal()`)

**Returns**: Hierarchical structure showing all affected classes and methods

**Example**:

```java
getMethodImpact(methodSignature: "calculateTotal()")
```

#### 3. getFieldImpact

**Description**: Gets full transitive impact analysis for a field. Returns hierarchical structure showing all classes and methods affected by changing the field.

**Parameters**:

* `className` (string, required): Name of the class that contains the field
* `fieldName` (string, required): Name of the field to analyze

**Returns**: Hierarchical structure showing all affected classes and methods

**Example**:

```java
getFieldImpact(className: "UserService", fieldName: "userName")
```

### Using MCP Tools in Cursor

After configuration, you can ask Cursor questions like:

* "What would be impacted if I change the `UserService` class?"
* "Show me the impact analysis for the `calculateTotal()` method"
* "Which methods would be affected if I modify the `userName` field?"

These tools will automatically be available in Cursor's AI assistant and can analyze your Java codebase for impact analysis.

## API Endpoints

### Project Indexing

#### Index a Java Project

```http
POST /api/v1/index/rescan?path=/absolute/path/to/java/project
```

**Description**: Triggers a full re-scan of a Java project. Clears the entire database first and then parses all `.java` files in the specified directory.

**Parameters**:

* `path` (required): Absolute file path to the root of the Java project

#### Get Parse Status

```http
GET /api/v1/index/status
```

**Description**: Gets the current status of the parsing operation.

**Returns**: JSON object with parse status information including `status`, `message`, and `timestamp`.

### Impact Analysis

#### Field Impact Analysis

```http
GET /api/v1/impact/field/full?fieldName=fieldName&className=ClassName
```

**Description**: Returns hierarchical structure showing all classes and methods affected by changing the field.

**Parameters**:

* `fieldName` (required): The name of the field to analyze
* `className` (required): The name of the class containing the field

#### Method Impact Analysis

```http
GET /api/v1/impact/method/full?methodSignature=methodSignature
```

**Description**: Returns hierarchical structure showing all classes and methods affected by changing the method.

**Parameters**:

* `methodSignature` (required): The full method signature (e.g., `calculateTotal()`)

#### Class Impact Analysis

```http
GET /api/v1/impact/class/full?className=ClassName
```

**Description**: Returns hierarchical structure showing all classes and methods affected by changing the class.

**Parameters**:

* `className` (required): The name of the class to analyze

## Usage Examples

### 1. Index a Java Project

```bash
curl -X POST "http://localhost:8080/api/v1/index/rescan?path=/Users/username/my-java-project"
```

### 2. Check Parse Status

```bash
curl "http://localhost:8080/api/v1/index/status"
```

### 3. Impact Analysis Examples

#### Get Full Field Impact Analysis

```bash
curl "http://localhost:8080/api/v1/impact/field/full?fieldName=userName&className=UserService"
```

#### Get Full Method Impact Analysis

```bash
curl "http://localhost:8080/api/v1/impact/method/full?methodSignature=calculateTotal()"
```

#### Get Full Class Impact Analysis

```bash
curl "http://localhost:8080/api/v1/impact/class/full?className=UserService"
```

## Architecture

### Graph Database Model

BeyondSight uses Neo4j to model Java code relationships:

**Nodes**:

* `JavaClass`: Represents Java classes with name and file path
* `JavaMethod`: Represents Java methods with name, signature, and file path
* `JavaField`: Represents Java fields with name

**Relationships**:

* `ClassFieldRelationship`: Links classes to their fields
* `ClassMethodRelationship`: Links classes to their methods
* `MethodCallRelationship`: Links methods to methods they call
* `MethodFieldReadRelationship`: Links methods to fields they read
* `MethodFieldWriteRelationship`: Links methods to fields they write

### Data Flow

1. **Parsing**: JavaParser analyzes `.java` files and extracts AST nodes
2. **Storage**: Code elements and relationships are stored in Neo4j
3. **Querying**: Cypher queries traverse the graph to find impact relationships
4. **Response**: Results are formatted into hierarchical JSON responses

## Development

### Building the Project

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### Configuration

The application configuration is in `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: beyondsight
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: notverysecret
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Author

**[Felipe Cesar Stanzani Fonseca](https://felipestanzani.com)**

---

For more information about Neo4j and graph databases, visit [neo4j.com](https://neo4j.com/).
