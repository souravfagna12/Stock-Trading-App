**Stock Trading Platform:**
An event-driven stock trading platform built with a Spring Boot microservices architecture. 
It supports user authentication, fund management, real-time market price streaming, buy/sell order processing, portfolio tracking, notifications, and AI-powered fraud detection using Claude .

**Features**
1. Secure authentication with JWT, validated centrally at the API Gateway
2. User and account management, including adding and withdrawing funds
3. Real-time price updates pushed to clients over WebSocket
4. Buy / Sell order placement with validation of funds and holdings
5. Asynchronous, event-driven processing between services using Kafka
6. Portfolio and holdings view with current valuation
7. Notifications for order and account events
8. AI service powered by Claude for fraud detection and identify suspicious transactions.
9. Redis caching for fast reads of frequently accessed data such as live prices

**Tech Stack**
_Layer_	                |    _Technology_
Language / Framework	|    Java, Spring Boot
Security	            |    Spring Security, JWT
Messaging	            |    Apache Kafka
Caching	                |    Redis
Real-time	            |    WebSocket
Database	            |    PostgreSQL
API Gateway	            |    Spring Cloud Gateway
AI	                    |    Claude (Anthropic API)
Build / Run	            |    Maven, Docker, Docker Compose

**Architecture**
![img_1.png](img_1.png)
Requests enter through the API Gateway, which validates the JWT before routing to the appropriate service. \
Services communicate asynchronously through Kafka for order and account events, while Redis holds hot data such as the latest stock prices.
Live prices reach clients through WebSocket from the Market Data Service.

**Services**
Api gateway:
Single entry point, routing, JWT validation

User service:
login , register , wallet

Market Data Service:
This is where our web socket lives and it broadcast it in every 2 seconds and store the latest price into the Redis.

Order service:
Buy / sell happen using this service and it make 3 calls
1. To the market date service to the latest price.
2. To the AI service to detect the fraud
3. Detect the fund for successful trade.

Portfolio service
This will get the notification from kafka for a success trade and it will calculate the live profit and loss from the market data service price

Notification service:
Consume events and notify users.

AI service: 
Powered by claude API for fraud detection and identify suspicious transactions.

**Order Flow**
- Step 1: Order-service receive the order and request is created with status as AI-check.
- Step 2: Market-data-service is called for get current-stock-price, calculate the total amount required for the order
- Step 3: calls AI service , send orders detail to it and analyze the order.
- Step 4: call user service - to deduct the money
- Step 5: order.status = executed , and published it to kafka.
- Step 6: portfolio consume it and update the holdings.
- Step 7: notify the user. 

