# tcp-udp-auction-network

A multithreaded, socket-based auction platform built in Java that simulates a distributed peer-to-peer marketplace via a centralized auctioneer server. Designed as part of the COEN 366 (Communication Networks and Protocols) course at Concordia University.

> Developed with a focus on industry-grade code structure, extensibility, and robust client-server architecture.

---

## 📌 Features

### ✅ Core Features (As per COEN 366 Specifications)

- 📡 **UDP-based Communication**

  - Peer registration/deregistration
  - Item listing
  - Auction subscriptions
  - Bid placement
  - Auction announcements and real-time updates

- 🔒 **TCP-based Communication**

  - Auction closure
  - Finalization of purchase transactions
  - Credit card simulation
  - Shipping information exchange

- 🧵 **Multithreading**

  - Concurrent server request handling
  - Concurrent client listening and user input threads

- 💾 **Persistence**

  - Server crash recovery with serialized state restoration (items, users, auction data)

- 📄 **Protocol Compliance**
  - Strict adherence to the custom message protocol with `RQ#` references and well-defined responses

---

## 🖼️ Optional Enhancements (Bonus)

- 🖥️ JavaFX-based GUI (optional)
- 🗂️ SQLite/PostgreSQL database integration for persistent storage
- 🔐 Basic authentication system (e.g., login with password or token)
- 🌐 REST API bridge for potential web front-end integration (Spring Boot)
- 📊 Admin analytics and auction history viewer

---

## 🚀 Technologies Used

| Category        | Technology                              |
| --------------- | --------------------------------------- |
| Language        | Java 17+                                |
| Networking      | Java Sockets (TCP & UDP)                |
| Concurrency     | Java Threads, Synchronized Collections  |
| Persistence     | Java Object Serialization / Optional DB |
| Optional GUI    | JavaFX                                  |
| Build Tool      | Maven or Gradle                         |
| Version Control | Git + GitHub                            |

---
