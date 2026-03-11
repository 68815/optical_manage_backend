---
name: "resource-manager"
description: "Manages optical network resources including creating, updating, deleting, and querying resource points. Invoke when user needs to manage network resources or perform CRUD operations on resource points."
---

# Resource Manager

## Overview

This skill helps manage optical network resources through the existing MapService API. It provides functionality for creating, updating, deleting, and querying resource points in the optical network.

## Features

- **Create Resource**: Add new resource points to the network
- **Update Resource**: Modify existing resource information
- **Delete Resource**: Remove resource points from the network
- **Query Resources**: Search for resources based on location or other criteria

## Usage Examples

### Create a New Resource

**Scenario**: User wants to add a new optical equipment location

```bash
# Example request structure
POST /api/map/resources
Content-Type: application/json

{
  "name": "Optical Node A",
  "type": "NODE",
  "location": {
    "latitude": 39.9042,
    "longitude": 116.4074
  },
  "properties": {
    "capacity": "10G",
    "status": "ACTIVE"
  }
}
```

### Update an Existing Resource

**Scenario**: User needs to update resource status

```bash
# Example request structure
PUT /api/map/resources/{id}
Content-Type: application/json

{
  "name": "Optical Node A",
  "type": "NODE",
  "location": {
    "latitude": 39.9042,
    "longitude": 116.4074
  },
  "properties": {
    "capacity": "10G",
    "status": "MAINTENANCE"
  }
}
```

### Delete a Resource

**Scenario**: User wants to remove an obsolete resource

```bash
# Example request
DELETE /api/map/resources/{id}
```

### Query Resources

**Scenario**: User needs to find resources in a specific area

```bash
# Example request structure
POST /api/map/resources/query
Content-Type: application/json

{
  "bbox": {
    "minLat": 39.9,
    "minLng": 116.4,
    "maxLat": 39.95,
    "maxLng": 116.45
  },
  "filters": {
    "type": "NODE",
    "status": "ACTIVE"
  }
}
```

## API Endpoints

- `POST /api/map/resources` - Create new resource
- `GET /api/map/resources/{id}` - Get resource by ID
- `PUT /api/map/resources/{id}` - Update resource
- `DELETE /api/map/resources/{id}` - Delete resource
- `POST /api/map/resources/query` - Query resources
- `POST /api/map/segments` - Create fiber segment
- `PUT /api/map/segments/{id}` - Update fiber segment
- `POST /api/map/segments/query` - Query fiber segments

## Response Formats

### Success Response

```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Optical Node A",
    "type": "NODE",
    "location": {
      "latitude": 39.9042,
      "longitude": 116.4074
    },
    "properties": {
      "capacity": "10G",
      "status": "ACTIVE"
    },
    "createdAt": "2026-03-07T10:00:00Z",
    "updatedAt": "2026-03-07T10:00:00Z"
  },
  "message": "Resource created successfully"
}
```

### Error Response

```json
{
  "success": false,
  "error": {
    "code": "400",
    "message": "Invalid resource data"
  }
}
```

## Best Practices

1. Always validate location coordinates before creating resources
2. Use descriptive names for resources to improve maintainability
3. Keep resource properties up-to-date for accurate network management
4. Use proper error handling when working with API responses
5. Implement pagination for large query results