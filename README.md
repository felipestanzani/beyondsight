# BeyondSight

A Java code dependency analyzer and impact analysis tool that uses graph database technology to provide comprehensive code relationship insights.

## Overview

BeyondSight analyzes Java projects to extract code structure and relationships, storing them in a Neo4j graph database. It provides REST APIs for performing impact analysis queries, helping developers understand the ripple effects of code changes across their codebase.

## Features

- **Java Project Parsing**: Automatically parses Java source code and extracts classes, methods, and fields
- **Graph Database Storage**: Stores code relationships in Neo4j for efficient querying
- **REST API**: Comprehensive API for impact analysis queries
- **Transitive Dependency Tracking**: Find upstream callers and downstream callees
- **Hierarchical Impact Analysis**: Get complete impact reports with class and method hierarchies
- **Line Number Tracking**: Precise location tracking for all code relationships

## Tech Stack

- **Spring Boot** 4.0.0-RC1
- **Java** 25
- **Neo4j** Graph Database
- **JavaParser** 3.27.1 for Java code parsing
- **Gradle** Build System
- **Docker Compose** for Neo4j setup

## Prerequisites

- Java 25
- Docker & Docker Compose
- Gradle (or use the included Gradle wrapper)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd beyondsight
```

### 2. Start Neo4j Database

```bash
docker-compose up -d
```

This will start Neo4j on:

- Bolt port: `7687`
- HTTP port: `7474`
- Username: `neo4j`
- Password: `notverysecret`

### 3. Build and Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## API Endpoints

### Project Indexing

#### Index a Java Project

```http
POST /api/v1/index/rescan?path=/absolute/path/to/java/project
```

**Description**: Triggers a full re-scan of a Java project. Clears the entire database first and then parses all `.java` files in the specified directory.

**Parameters**:

- `path` (required): Absolute file path to the root of the Java project

### Impact Analysis

#### Field Impact Analysis

##### Find Methods Writing to a Field

```http
GET /api/v1/impact/field-writers?fieldName=fieldName
```

##### Find Methods Reading a Field

```http
GET /api/v1/impact/field-readers?fieldName=fieldName
```

##### Full Field Impact Analysis

```http
GET /api/v1/impact/field/full?fieldName=fieldName&className=ClassName
```

**Description**: Returns hierarchical structure showing all classes and methods affected by changing the field.

#### Method Impact Analysis

##### Find Upstream Callers

```http
GET /api/v1/impact/upstream-callers?methodName=methodName
```

**Description**: Finds all methods that (directly or indirectly) call a target method ("who calls me?" analysis).

##### Find Downstream Callees

```http
GET /api/v1/impact/downstream-callees?methodSignature=methodSignature
```

**Description**: Finds all methods that (directly or indirectly) are called by a target method ("who do I call?" analysis).

##### Full Method Impact Analysis

```http
GET /api/v1/impact/method/full?methodSignature=methodSignature
```

**Description**: Returns hierarchical structure showing all classes and methods affected by changing the method.

#### Class Impact Analysis

##### Full Class Impact Analysis

```http
GET /api/v1/impact/class/full?className=ClassName
```

**Description**: Returns hierarchical structure showing all classes and methods affected by changing the class.

## Usage Examples

### 1. Index a Java Project

```bash
curl -X POST "http://localhost:8080/api/v1/index/rescan?path=/Users/username/my-java-project"
```

### 2. Find Methods Writing to a Field

```bash
curl "http://localhost:8080/api/v1/impact/field-writers?fieldName=userName"
```

### 3. Find Upstream Callers of a Method

```bash
curl "http://localhost:8080/api/v1/impact/upstream-callers?methodName=calculateTotal"
```

### 4. Get Full Method Impact Analysis

```bash
curl "http://localhost:8080/api/v1/impact/method/full?methodSignature=calculateTotal()"
```

### 5. Get Full Class Impact Analysis

```bash
curl "http://localhost:8080/api/v1/impact/class/full?className=UserService"
```

## Architecture

### Graph Database Model

BeyondSight uses Neo4j to model Java code relationships:

**Nodes**:

- `JavaClass`: Represents Java classes with name and file path
- `JavaMethod`: Represents Java methods with name, signature, and file path
- `JavaField`: Represents Java fields with name

**Relationships**:

- `ClassFieldRelationship`: Links classes to their fields
- `ClassMethodRelationship`: Links classes to their methods
- `MethodCallRelationship`: Links methods to methods they call
- `MethodFieldReadRelationship`: Links methods to fields they read
- `MethodFieldWriteRelationship`: Links methods to fields they write

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

**Felipe Cesar Stanzani Fonseca**

---

For more information about Neo4j and graph databases, visit [neo4j.com](https://neo4j.com/).
