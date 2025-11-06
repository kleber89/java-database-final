# Java Database Final — Example Project

This repository contains a small example application (Spring Boot back-end + static front-end) covering a domain of stores, products, inventory, orders and reviews. It was created as a final project for the "Java Developer" course on Coursera.

## Summary

- Back-end: Spring Boot (Maven) application with JPA entities for `Product`, `Store`, `Customer`, `Inventory`, `OrderDetails`, `OrderItem` and a MongoDB document for `Review`.
- Front-end: static pages (HTML/CSS/JS) under the `front-end` folder for basic interactions (not a full SPA).

The project demonstrates relational modeling with JPA, CRUD operations using Spring Data, REST controllers and a basic order processing flow (decrement inventory, create orders and order items).

## Main structure

- `back-end/` — Spring Boot (Maven) module
  - `src/main/java/com/project/code/Model` — entities and DTOs (JPA + Mongo)
  - `src/main/java/com/project/code/Repo` — Spring Data JPA / Mongo repositories
  - `src/main/java/com/project/code/Controller` — REST controllers
  - `src/main/java/com/project/code/Service` — business logic (e.g. `OrderService`)
  - `src/main/resources/application.properties` — application configuration
- `front-end/` — static pages and assets (HTML, JS, CSS)
- `reviews.json`, `insert_data.sql` — example/sample data

## Technologies

- Java 11+ (many parts are compatible with Java 8+)
- Spring Boot
- Spring Data JPA (Hibernate)
- Spring Data MongoDB (for `Review`)
- Maven
- Jackson (JSON serialization)

## Key endpoints (summary)

Below is a brief list of implemented REST endpoints (base routes):


- `/product`
  - POST `/product` — create a product
  - GET `/product` — list products
  - GET `/product/{id}` — get product by id
  - PUT `/product` — update product
  - DELETE `/product/{id}` — delete product (also removes related inventory)
  - GET `/product/searchProduct/{name}` — search by name (optional `storeId` query param)


- `/inventory`
  - POST `/inventory/save` — create inventory record
  - PUT `/inventory/update` — update inventory (accepts `CombinedRequest`)
  - GET `/inventory/store/{storeId}/products` — products for a store
  - GET `/inventory/filter` — filter products by category/name
  - GET `/inventory/search` — search products by name within a store
  - GET `/inventory/validate` — validate available quantity


- `/store`
  - POST `/store` — create a store
  - GET `/store/validate/{storeId}` — validate store existence
  - POST `/store/placeOrder` — process an order (accepts `PlaceOrderRequestDTO`)


- `/reviews`
  - GET `/reviews/{storeId}/{productId}` — retrieve reviews (comment, rating, customer name)

Controllers are in `back-end/src/main/java/com/project/code/Controller`.

## How to run (back-end)

Important note: the repository attempted to use the Maven wrapper (`mvnw`) but wrapper files may be missing in some cases. You can use either option below:

1) Use system-installed Maven (recommended if available):

```powershell
cd "c:\Users\usuario\OneDrive\Desktop\Java Developer Coursera\java-database-final\back-end"
mvn -DskipTests package
```

2) Regenerate or add the Maven wrapper (if you prefer `mvnw`):

```powershell
# From the back-end module root
mvn -N io.takari:maven:wrapper
# This generates .mvn/wrapper and mvnw/mvnw.cmd
```

Using `-DskipTests` will compile the project quickly without running tests.

## Database configuration

Database settings are in `back-end/src/main/resources/application.properties`. The project can be configured to use H2, PostgreSQL or MySQL by changing `spring.datasource.*` properties. If reviews are used with MongoDB, adjust `spring.data.mongodb.*` accordingly.

## Tests and validation

- Add unit/integration tests under `back-end/src/test/java` and run `mvn test`.
- Example data can be loaded using `insert_data.sql` or `reviews.json` depending on your persistence setup.

## Notes and best practices

- Many controllers include only basic validation; for production code add `@Valid` DTOs and stronger validation.
- Controllers use constructor injection (recommended). Some classes may show linter warnings about unnecessary `@Autowired` on constructors — you can safely remove `@Autowired` if there is a single constructor.

## Suggested next steps

- Add automated tests for the order flow.
- Add authentication/authorization (e.g. Spring Security) for access control.
- Improve exception handling and standardize error responses (consistent error payloads).

## Contact / Help

If you want me to implement additional items (tests, restore the Maven wrapper, add OpenAPI/Swagger documentation, or provide Docker scripts), tell me which item to prioritize and I'll implement it.

---
File updated by the development assistant.