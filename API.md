# BeyondSight REST API

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

