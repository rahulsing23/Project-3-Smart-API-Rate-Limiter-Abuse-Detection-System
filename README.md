# 🚀 Smart API Rate Limiter & Abuse Detection System

A production-style **Java Spring Boot** project that protects REST APIs from **excessive traffic, brute-force attacks, and abuse** using **Redis-backed rate limiting** and **behavior-based abuse detection**.

---

## 📌 Problem Statement

Modern APIs are exposed to:
- Excessive requests (DoS / brute force)
- Bot traffic and scraping
- Credential stuffing
- Resource exhaustion

Basic authentication alone is not enough to protect APIs.

---

## ✅ Solution

This project implements:
- **Rate Limiting** using the Token Bucket algorithm
- **Abuse Detection** based on high-frequency request patterns
- **Redis** for distributed state management
- **Automatic blocking with TTL**
- **Concurrency-safe design**

---

## 🧠 Key Features

- Per-IP rate limiting
- Redis-based token bucket
- Abuse detection & temporary blocking
- Auto-unblock after TTL expiry
- Distributed-system ready
- Easy to extend for per-user or per-endpoint limits

---

## 🏗️ Tech Stack

- Java 17
- Spring Boot
- Redis
- Spring Data Redis
- Maven
- REST APIs

---

## 📂 Project Structure
```
SmartRatelimiterAndAbuseDetector
│
├── config
│ ├── RedisConfig.java
│ └── FilterConfig.java
│
├── filter
│ └── RateLimitingFilter.java
│
├── service
│ ├── RateLimiterService.java
│ └── AbuseDetectionService.java
│
├── controller
│ └── TestController.java
│
└── SmartRatelimiterAndAbuseDetectorApplication.java

```




---

## ⚙️ Rate Limiting Logic

- **Algorithm:** Token Bucket
- Each client receives limited tokens
- Every request consumes 1 token
- Tokens refill over time
- Requests are blocked when tokens reach zero

---

## 🚨 Abuse Detection Logic

A client is marked abusive if:
- Too many requests in a short window
- Repeated rate-limit violations
- Suspicious burst traffic patterns

Blocked clients are:
- Stored in Redis
- Automatically unblocked after TTL expiry

---



