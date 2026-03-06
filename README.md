# Flashsale Service

Backend service cho hệ thống **Flash Sale** sử dụng:

-   Spring Boot
-   MongoDB
-   Redis
-   Docker
-   JWT Authentication

------------------------------------------------------------------------

# 1. Requirements

Trước khi chạy project cần cài đặt:

-   Docker
-   Docker Compose

Kiểm tra:

``` bash
docker --version
docker compose version
```

------------------------------------------------------------------------

# 2. Clone Project

``` bash
git clone https://github.com/anhphuoc73/flashsale-service.git
cd flashsale-service
```

------------------------------------------------------------------------

# 3. Build và Run

Project sử dụng **Docker Compose** để chạy các service:

-   Flashsale Service
-   MongoDB
-   Redis

Chạy project:

``` bash
docker compose up --build
```

Docker sẽ tự động:

-   Build image cho `flashsale-service`
-   Pull image `Mongo:7`
-   Pull image `Redis:7`
-   Tạo container
-   Start toàn bộ service

------------------------------------------------------------------------

# 4. Kiểm tra Docker đang chạy

``` bash
docker ps
```

Kết quả sẽ có:

    flashsale-service
    mongodb
    redis

------------------------------------------------------------------------

# 5. API

## 5.1 Authentication

### Register

POST http://localhost:8080/api/auth/register

``` json
{
  "email": "anhphuoc10@gmail.com",
  "phone": "0902312210",
  "password": "123456"
}
```

### Verify OTP

POST http://localhost:8080/api/auth/verify

``` json
{
  "input": "anhphuoc10@gmail.com",
  "otp": "948005"
}
```

### Login

POST http://localhost:8080/api/auth/login

``` json
{
  "input": "admin@gmail.com",
  "password": "123456"
}
```

### Logout

POST http://localhost:8080/api/auth/logout

Headers

    Authorization: Bearer <accessToken>

### Refresh Token

POST http://localhost:8080/api/auth/refresh

``` json
{
  "refreshToken": "eyJhbGciOiJIUzI..."
}
```

------------------------------------------------------------------------

## 5.2 Product

### Create Product

POST http://localhost:8080/api/products

Headers

    Authorization: Bearer <accessToken>

``` json
{
  "name": "Iphone 15 plus",
  "price": 35000000,
  "stock": 200
}
```

### Update Product

PUT http://localhost:8080/api/products/{productId}

Headers

    Authorization: Bearer <accessToken>

``` json
{
  "name": "Iphone 15 Pro Max",
  "description": "Newest version 2026",
  "price": 45000000",
  "stock": 50
}
```

### Delete Product

DELETE http://localhost:8080/api/products/{productId}

Headers

    Authorization: Bearer <accessToken>

### Product List

GET http://localhost:8080/api/products?page=0&size=5

Headers

    Authorization: Bearer <accessToken>

------------------------------------------------------------------------

## 5.3 Flash Sale Session

### Create Session

POST http://localhost:8080/api/sessions

Headers

    Authorization: Bearer <accessToken>

``` json
{
  "name": "Flash Sale Morning",
  "startTime": "2026-03-07 00:00:00",
  "endTime": "2026-03-07 23:59:59",
  "active": true
}
```

### Update Session

PUT http://localhost:8080/api/sessions

Headers

    Authorization: Bearer <accessToken>

``` json
{
  "name": "Flash Sale Morning Updated"
}
```

### Delete Session

DELETE http://localhost:8080/api/sessions/{id}

Headers

    Authorization: Bearer <accessToken>

### Session List

GET http://localhost:8080/api/admin/sessions?page=0&size=10

Headers

    Authorization: Bearer <accessToken>

------------------------------------------------------------------------

## 5.4 Flash Sale Item

### Create Flash Sale Item

POST http://localhost:8080/api/admin/flash-sale-items

Headers

    Authorization: Bearer <accessToken>

``` json
{
  "sessionId": "69aa3c2f11f36d343cf3e73b",
  "productId": "69a9c332dc93ad1284ea22ef",
  "quantityLimit": 100,
  "salePrice": 199000
}
```

### Update Flash Sale Item

PUT http://localhost:8080/api/admin/flash-sale-items/{id}

Headers

    Authorization: Bearer <accessToken>

``` json
{
  "salePrice": 179000,
  "quantityLimit": 150
}
```

### Delete Flash Sale Item

DELETE http://localhost:8080/api/admin/flash-sale-items/{id}

Headers

    Authorization: Bearer <accessToken>

### Admin Item List

GET http://localhost:8080/api/admin/flash-sale-items?page=0&size=10

Headers

    Authorization: Bearer <accessToken>

### User Flash Sale List

GET http://localhost:8080/api/flash-sale-items/current

Headers

    Authorization: Bearer <accessToken>

------------------------------------------------------------------------

## 5.5 Purchases (Order)

### Create Order

POST http://localhost:8080/api/purchases

Headers

    Authorization: Bearer <accessToken>

``` json
{
  "flashSaleItemId": "69aa3c4f11f36d343cf3e73e"
}
```

### Purchase List

GET http://localhost:8080/api/purchases

Headers

    Authorization: Bearer <accessToken>
