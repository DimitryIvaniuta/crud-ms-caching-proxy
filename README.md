# crud-ms-caching-proxy
Java / Spring Boot microservice with CRUD for Users, Contacts, Listings, Transactions (PostgreSQL + Flyway + JPA). Includes a custom caching proxy (no Spring Cache abstraction) built via Spring AOP proxy + BeanPostProcessor, with TTL cache + explicit evict on writes
