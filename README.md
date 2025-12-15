# korber-pharma-managed-service
A lightweight Spring Boot microservice that maintains pharmaceutical inventory by product and batch, supports batch-level expiry tracking, and exposes REST endpoints to query and update inventory. Built with Spring Data JPA and an H2 in-memory database.


## Features
- **Pharmaceutical Inventory Management**: Track products and their batches with expiry dates.
- **RESTful API**: Expose endpoints for querying and updating inventory.
- **In-Memory Database**: Uses H2 for easy setup and testing.
- **Validation and Error Handling**: Ensures data integrity and provides meaningful error responses.
- **Unit and Integration Tests**: Comprehensive test coverage for reliability.

## Future Enhancements
- **Authentication and Authorization**: Secure endpoints with Spring Security.
- **Persistent Database Support**: Option to switch to a persistent database like PostgreSQL or MySQL.
- **Batch Processing**: Automated tasks for inventory audits and expiry notifications.
- **Caching**: Improve performance with caching strategies.
- **Asynchronous Processing**: Handle long-running tasks without blocking.
- **Monitoring and Metrics**: Integrate with monitoring tools for performance tracking.

## Getting Started

# Inventory Management Service

This microservice manages product inventory and batch reservations. It exposes APIs to check product availability and reserve stock for orders.

---

## 🚀 Project Setup[target](korber-pharma-inventory-service/target)

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/inventory-management-service.git
   cd inventory-management-service
2. **Build the project**
   ```bash
   ./mvn clean install

3. **Run the project**
   ```bash
   ./mvn spring-boot:run
## 📖 API Documentation
## Get Product Inventory
```bash
    GET: {host}/v1/korber-pharma/inventory/{product_id}
    Accept: application/json
    HTTP/1.1 200 OK
    Content-Type: application/json
    {
    "productId": 1003,
    "productName": "Tablet",
    "batches": [
        {
            "batchId": 4,
            "expiryDate": "2026-09-03",
            "quantity": 35
        },
        {
            "batchId": 8,
            "expiryDate": "2026-09-09",
            "quantity": 21
        }
    ]
   }
```

## Product not found in inventory
```bash
      GET: {host}/v1/korber-pharma/inventory/{invalid_product_id}
        Accept: application/json
        HTTP/1.1 404 Not Found
        Content-Type: application/json
      {
        "errorCode": "ERR001",
        "errorDesc": "Product not found",
        "errorMessage": "Product not found for productId: 10003",
        "timestamp": "2025-12-15T21:57:51.698898"
      }
```


## Order Management Service

This microservice accepts and processes product orders. It communicates with the Inventory Management Service to check availability and reserve stock.

---

## 🚀 Project Setup

1. **Clone the repository**
   ```bash
    git clone https://github.com/your-org/order-management-service.git
    cd order-management-service
2. **Build the project**
   ```bash
   ./mvn clean install

3. **Run the project**
   ```bash
   ./mvn spring-boot:run

## 📖 API Documentation
## Place Order
```bash
    POST: {host}/v1/korber-pharma/place-order
    Accept: application/json
    HTTP/1.1 200 OK
    Content-Type: application/json

    Request:
         {
          "productId":1003,
          "quantity": 45
         }
    
     Response:
      {
        "orderId": 1,
        "productId": 1002,
        "productName": "Smartphone",
        "quantity": 46,
        "status": "PLACED",
        "reservedFromBatchIds": [
            9,
            10
        ],
        "message": "Order placed. Inventory reserved."
    }
```
## Product out of stock
```bash
POST: {host}/v1/korber-pharma/place-order
    Accept: application/json
    HTTP/1.1 400 Bad Request
    Content-Type: application/json
    
    Request:
    {
        "productId":1002,
        "quantity": 460
    }
    Response:
    {
        "errorCode": "ERR002",
        "errorDesc": "Product is out of stock",
        "errorMessage": "Insufficient stock for product ID: 1002, Available quantity: 66",
        "timestamp": "2025-12-15T21:54:36.967684800"
    }
```



    



