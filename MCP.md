# BeyondSight MCP Tools

BeyondSight provides the following MCP tools for impact analysis:

## 1. getClassImpact

**Description**: Gets full transitive impact analysis for a class. Returns hierarchical structure showing all classes and methods affected by changing the class.

**Parameters**:

* `className` (string, required): Name of the class to analyze

**Returns**: Hierarchical structure showing all affected classes and methods

**Example**:

```java
getClassImpact(className: "UserService")
```

## 2. getMethodImpact

**Description**: Gets full transitive impact analysis for a method. Returns hierarchical structure showing all classes and methods affected by changing the method.

**Parameters**:

* `methodSignature` (string, required): Signature of the method to analyze (e.g., `calculateTotal()`)

**Returns**: Hierarchical structure showing all affected classes and methods

**Example**:

```java
getMethodImpact(methodSignature: "calculateTotal()")
```

## 3. getFieldImpact

**Description**: Gets full transitive impact analysis for a field. Returns hierarchical structure showing all classes and methods affected by changing the field.

**Parameters**:

* `className` (string, required): Name of the class that contains the field
* `fieldName` (string, required): Name of the field to analyze

**Returns**: Hierarchical structure showing all affected classes and methods

**Example**:

```java
getFieldImpact(className: "UserService", fieldName: "userName")
```

