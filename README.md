# RankingService

## Summary
`RankingService` is a Spring Boot-based microservice that acts as an **aggregator** for the user point management system. Its primary role is to provide a unified view of users and their accumulated points. It does not maintain its own database; instead, it orchestrates calls to the `UserService` and `PointService` to fetch user identities and point balances, respectively, and then merges this data into a single response.

## Project Flow
The project follows an aggregator pattern to provide ranking data:

1.  **Request Handling Layer (`RankController`):** Receives incoming REST requests with a `minPoint` threshold.
2.  **Service Layer (`RankingService` & `UserPointService`):**
    *   **User Data Fetching:** Calls the `UserService` to retrieve a complete list of registered users (IDs and Names).
    *   **Point Data Fetching:** Calls the `PointService` to retrieve point data for users who meet the specified `minPoint` criteria.
    *   **Data Aggregation:** Uses internal mapping logic to correlate the user IDs returned by the `PointService` with the user names fetched from the `UserService`.
3.  **Data Models:** Uses **Google Protocol Buffers (Protobuf)** for internal messaging and for the API request/response structures.

## API Endpoints
The API uses `application/json` as the media type and returns **Protobuf**-based structures.

| Endpoint | Method | Description | Input | Output |
| :--- | :--- | :--- | :--- | :--- |
| `/rank/users` | `POST` | Retrieves a list of users and their points, filtered by a minimum threshold. | `GetUserRankRequest` | `GetUserRankResponse` |

### API Details

#### 1. Get User Rank Data
*   **Path:** `/rank/users`
*   **Method:** `POST`
*   **Input (`GetUserRankRequest`):**
    ```json
    {
      "minPoint": integer
    }
    ```
*   **Output (`GetUserRankResponse`):**
    ```json
    {
      "userPointList": [
        {
          "name": "string",
          "point": integer
        }
      ]
    }
    ```
*   **Process:** 
    1. Fetch all users from `UserService`.
    2. Fetch all users meeting the `minPoint` criteria from `PointService`.
    3. Aggregate the results by matching IDs to Names.

## Technology Stack
*   **Framework:** Spring Boot 4.0.3
*   **Serialization:** Google Protocol Buffers (Protobuf)
*   **Inter-service Communication:** OkHttp 5
*   **Build Tool:** Gradle (Kotlin DSL)
*   **Deployment:** Kubernetes (YAML files included in `k8s/` directory)
*   **Containerization:** Docker (Dockerfile included)

## Configuration
The service relies on external service configurations (injectable via environment variables):

*   **UserService:**
    *   `USER_SERVICE_HOST`: Hostname for the `UserService` (default: `userservice.userservicenamespace`)
    *   `userservice.path`: Path for fetching all users (default: `/users`)
*   **PointService:**
    *   `POINT_SERVICE_HOST`: Hostname for the `PointService` (default: `pointservice-svc.psn`)
    *   `pointservice.path`: Path for fetching point data (default: `/point/users`)

The application runs on port `8093` by default.

## Inter-Service Dependencies
*   **UserService:** Required to resolve user IDs into human-readable names.
*   **PointService:** Required to retrieve the current point balances for users.
