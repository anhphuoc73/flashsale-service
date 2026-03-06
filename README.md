1. Requirements
    Trước khi chạy project, cần cài sẵn:
        Docker 
        Docker Compose
   
    Kiểm tra:
        docker --version
        docker compose version
2. Clone project
      git clone https://github.com/<your-username>/flashsale-service.git
      cd flashsale-service
3. Build và run
   Project sử dụng Docker Compose để chạy:
      Flashsale Service
      MongoDB
      Redis 
   Run:
       docker compose up --build
       
   Docker sẽ tụ động build gồm:
        Build image cho flashsale-service
        Pull image: Mongo:7 và Redis:7
        Tạo container
        start toàn bộ service
4. Kiểm tra docker đang chạy
    Docker ps
    
    Kết quả:
        flashsale-service
        mongodb
        redis

        
5. API 
    5.1 AUTHENTICATION
        5.1.1 REGISTER
            URL: http://localhost:8080/api/auth/register
            METHOD: POST
            Request body:
               {
                    "email":"anhphuoc10@gmail.com",
                    "phone":"0902312210",
                    "password":"123456"
               }
        5.2.2 VERIFY (OTP)
            URL: http://localhost:8080/api/auth/verify
            METHOD: POST
            Request body:
               {
                    "input":"anhphuoc10@gmail.com",
                    "otp":"948005"
               }
        5.1.3 LOGIN
           URL: http://localhost:8080/api/auth/login
           METHOD: POST
           Request body:
               {
                   "input":"admin@gmail.com",
                   "password":"123456"
               }
        5.1.4 LOGOUT
           URL: http://localhost:8080/api/auth/logout
           HEADERS:
                Authorization: Bearer <accessToken>
           METHOD: POST
           Request body:
        5.1.5 REFRESH TOKEN
          URL: http://localhost:8080/api/auth/refresh
          METHOD: POST
          Request body:
          {
               "refreshToken": "eyJhbGciOiJIUzI...."
          }
       5.2 PRODUCT
           5.2.1 CREATE
               URL: http://localhost:8080/api/products
               HEADERS:
                    Authorization: Bearer <accessToken>
               METHOD: POST
               Request body:
               {
                   "name": "Iphone 15 plus",
                   "price": 35000000,
                   "stock": 200
               }
           5.2.2 UPDATE
               URL: http://localhost:8080/api/products/69a54fd6e99c696ef323219b
               HEADERS:
                    Authorization: Bearer <accessToken>
               METHOD: PUT
               Request body:
               {
                   "name": "Iphone123456 15 Pro Max",
                   "description": "Newest version 2026",
                   "price": 45000000,
                   "stock": 50
               }
           5.2.3 DELETE
               URL: http://localhost:8080/api/products/69a54fd6e99c696ef323219b
               HEADERS:
                    Authorization: Bearer <accessToken>
               METHOD: DELETE
               Request body:
           5.2.4 LIST
               URL: http://localhost:8080/api/products?page=0&size=5
               HEADERS:
                    Authorization: Bearer <accessToken>
               METHOD: GET
               Params:
       5.3 SESSION
           5.3.1 CREATE
              URL: http://localhost:8080/api/sessions
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD: POST
              Request body:
              {
                  "name": "Flash Sale Morning 06/03",
                  "startTime": "2026-03-07 00:00:00",
                  "endTime": "2026-03-07 23:59:59",
                   "active": true
              }
           5.3.2 UPDATE
              URL: http://localhost:8080/api/sessions
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD: PUT
              Request body:
              {
                   "name": "Flash Sale Morning 111"
              }
           5.3.3 DELETE
              URL: http://localhost:8080/api/sessions/69a54fd6e99c696ef323219b
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD: DELETE
              Request body:
           5.3.4 LIST
              URL: http://localhost:8080/api/admin/sessions?page=0&size=10
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD: GET
              Params:
       5.4 FLASHSALE ITEM  
           5.4.1 CREATE
              URL: http://localhost:8080/api/admin/flash-sale-items
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD: POST
              Request body:
              {
                  "sessionId": "69aa3c2f11f36d343cf3e73b",
                  "productId": "69a9c332dc93ad1284ea22ef",
                  "quantityLimit": 100,
                  "salePrice": 199000
              }
           5.4.2 UPDATE
              URL: http://localhost:8080/api/admin/flash-sale-items/69a85910eb18d02dc1cbcfa8
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD: PUT
              Request body:
              {
                  "salePrice": 179000,
                  "quantityLimit": 150
              }
          5.4.3 DELETE
              URL: http://localhost:8080/api/admin/flash-sale-items/69a85910eb18d02dc1cbcfa8
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD: DELETE
              Request body:
          5.4.4 LIST ADMIN
              URL: http://localhost:8080/api/admin/flash-sale-items?page=0&size=10
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD:  GET
              Params:
          5.4.5 LIST USER
              URL: http://localhost:8080/api/flash-sale-items/current
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD: GET
              Params:
       5.5 PURCHASES (ORDER)
           5.5.1 CREATE (ORDER)
              URL: http://localhost:8080/api/purchases
              HEADERS:
              Authorization: Bearer <accessToken>
              METHOD: POST
              Request body:
              {
                   "flashSaleItemId": "69aa3c4f11f36d343cf3e73e"
              }
           5.5.2 LIST
              URL: http://localhost:8080/api/purchases
              HEADERS:
                    Authorization: Bearer <accessToken>
              METHOD: GET
              Params:
                
