# Generic REST Caller

## Overview

The Generic REST Caller is a microservice that dynamically calls third-party APIs based on configurations stored in a MongoDB database. It provides endpoints for managing API keys and configurations, as well as executing API calls.

## Table of Contents

- [Generic REST Caller](#generic-rest-caller)
  - [Overview](#overview)
  - [Table of Contents](#table-of-contents)
  - [Installation](#installation)
  - [Database Population](#database-population)
  - [Usage](#usage)
  - [API Endpoints](#api-endpoints)
    - [API Key Management](#api-key-management)
      - [Create API Key](#create-api-key)
      - [List API Keys](#list-api-keys)
      - [Revoke API Key](#revoke-api-key)
    - [API Configuration Management](#api-configuration-management)
      - [Create API Configuration](#create-api-configuration)
      - [List API Configurations](#list-api-configurations)
      - [Get API Configuration](#get-api-configuration)
      - [Update API Configuration](#update-api-configuration)
      - [Delete API Configuration](#delete-api-configuration)
    - [Execute API Call](#execute-api-call)
    - [Mock API Endpoints](#mock-api-endpoints)
      - [Overview](#overview-1)
      - [Mock PNR Check](#mock-pnr-check)
      - [Mock Flight Status](#mock-flight-status)
      - [Mock Ticket Creation](#mock-ticket-creation)
      - [Mock Ticket Update](#mock-ticket-update)
      - [Mock Ticket Deletion](#mock-ticket-deletion)
    - [Example Usage of `api/call` with Mock APIs](#example-usage-of-apicall-with-mock-apis)
      - [Example: Mock PNR Check](#example-mock-pnr-check)
      - [Example: Mock Flight Status](#example-mock-flight-status)
      - [Example: Mock Ticket Creation](#example-mock-ticket-creation)
      - [Example: Mock Ticket Update](#example-mock-ticket-update)
      - [Example: Mock Ticket Deletion](#example-mock-ticket-deletion)
  - [Logging](#logging)

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/Voice-AI-Project/generic-rest-caller.git
   cd generic-rest-caller
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Set up your environment variables in a `.env` file:

   ```plaintext
   PORT=5000
   MONGODB_URI=mongodb://localhost:27017/yourdbname
   API_KEY=YOUR_API_KEY_STRING
   LOG_LEVEL=info
   NODE_ENV=development
   ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5000,http://localhost:8080,http://localhost:8000
   ```

4. Start the application:
   ```bash
   npm start
   ```

## Database Population

To populate the database with mock data, follow these steps:

1. **Ensure MongoDB is Running**: Make sure your MongoDB server is running and accessible.

2. **Set Up Environment Variables**: Ensure your `.env` file is correctly configured with the `MONGODB_URI` pointing to your MongoDB instance.

3. **Run the Population Script**: Execute the `populateDb.js` script to insert mock data into the database.

   ```bash
   node src/populateDb.js
   ```

   This script will connect to the MongoDB database specified in your `.env` file and populate it with predefined mock data for flights, PNRs, and tickets.

4. **Verify Data Population**: After running the script, you should see a confirmation message indicating that the mock data has been populated successfully.

## Usage

Once the server is running, you can access the API at `http://localhost:5000`.

To access authenticated routes, you need to create your own API key. You can do this by hitting the [`POST /api/keys`](#create-api-key) endpoint. This will generate an API key that you can use in the `x-api-key` header for all authenticated requests.

## API Endpoints

### API Key Management

#### Create API Key

- **Endpoint**: `POST /api/keys`
- **Description**: Creates a new API key.
- **Response**: Returns the newly created API key.
- **Example cURL Command**:
  ```bash
  curl -X POST http://localhost:5000/api/keys \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "your-client-id"
  }'
  ```

#### List API Keys

- **Endpoint**: `GET /api/keys`
- **Description**: Lists all API keys.
- **Response**: Returns a list of API keys.
- **Example cURL Command**:
  ```bash
  curl -X GET http://localhost:5000/api/keys \
  -H "x-api-key: YOUR_API_KEY"
  ```

#### Revoke API Key

- **Endpoint**: `DELETE /api/keys/:id`
- **Path Parameters**:
  - `id` (string, required): The ID of the API key to revoke.
- **Response**: Returns a success message.
- **Example cURL Command**:
  ```bash
  curl -X DELETE http://localhost:5000/api/keys/YOUR_KEY_ID \
  -H "x-api-key: YOUR_API_KEY"
  ```

### API Configuration Management

#### Create API Configuration

- **Endpoint**: `POST /api/configs`
- **Description**: Creates a new API configuration.
- **Response**: Returns the newly created API configuration.
- **Example cURL Command**:
  ```bash
  curl -X POST http://localhost:5000/api/configs \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_API_KEY" \
  -d '{
    "serviceId": "example-service",
    "name": "Example Service",
    "baseUrl": "http://example.com",
    "endpoint": "/api/example",
    "method": "GET"
  }'
  ```

#### List API Configurations

- **Endpoint**: `GET /api/configs`
- **Description**: Lists all API configurations.
- **Response**: Returns a list of API configurations.
- **Example cURL Command**:
  ```bash
  curl -X GET http://localhost:5000/api/configs \
  -H "x-api-key: YOUR_API_KEY"
  ```

#### Get API Configuration

- **Endpoint**: `GET /api/configs/:serviceId`
- **Path Parameters**:
  - `serviceId` (string, required): The ID of the API configuration to retrieve.
- **Response**: Returns the API configuration details.
- **Example cURL Command**:
  ```bash
  curl -X GET http://localhost:5000/api/configs/example-service \
  -H "x-api-key: YOUR_API_KEY"
  ```

#### Update API Configuration

- **Endpoint**: `PUT /api/configs/:serviceId`
- **Path Parameters**:
  - `serviceId` (string, required): The ID of the API configuration to update.
- **Response**: Returns a success message.
- **Example cURL Command**:
  ```bash
  curl -X PUT http://localhost:5000/api/configs/example-service \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_API_KEY" \
  -d '{
    "name": "Updated Example Service",
    "baseUrl": "http://example.com",
    "endpoint": "/api/example",
    "method": "GET"
  }'
  ```

#### Delete API Configuration

- **Endpoint**: `DELETE /api/configs/:serviceId`
- **Path Parameters**:
  - `serviceId` (string, required): The ID of the API configuration to delete.
- **Response**: Returns a success message.
- **Example cURL Command**:
  ```bash
  curl -X DELETE http://localhost:5000/api/configs/example-service \
  -H "x-api-key: YOUR_API_KEY"
  ```

### Execute API Call

- **Endpoint**: `POST /api/call`
- **Method**: `POST`
- **Description**: Executes an external API call based on the configuration specified by the `serviceId`. The request body must include the HTTP method to be used for the external API call, along with any additional parameters required by the specific API.

- **Request Body**:

  - `serviceId` (string, required): The ID of the API configuration to use.
  - `method` (string, required): The HTTP method to use for the external API call (e.g., GET, POST, PUT, DELETE).
  - `reqBody` (object, optional): An object containing any additional parameters required by the external API. This will be sent as the request body to the external API.

- **Query Parameters** (for GET, PUT, DELETE methods):

  - Additional parameters can be included in the query string of the URL. For example, if the external API requires a `location` parameter, it can be included as follows:
  - **Example for GET**:
    ```bash
    curl -X GET "http://localhost:5000/api/call?location=New%20York" \
    -H "x-api-key: YOUR_API_KEY" \
    -d '{
      "serviceId": "weather-api",
      "method": "GET"
    }'
    ```

- **Response**:

  - Returns the response from the external API, which may include data, status codes, and any error messages from the API.

- **Example cURL Command**:
  ```bash
  curl -X POST http://localhost:5000/api/call \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_API_KEY" \
  -d '{
    "serviceId": "weather-api",
    "method": "GET",
    "reqBody": {
      "location": "New York",
      "units": "metric"
    }
  }'
  ```

### Mock API Endpoints

#### Overview

The mock APIs simulate real-world API interactions for testing purposes. Below are the configurations and usage examples for each mock API.

#### Mock PNR Check

- **Description**: Checks the PNR status of a flight.
- **Endpoint**: `/mock/flight/pnr`
- **Method**: `GET`
- **Request Parameters**:
  - `pnr` (string, required): The PNR number to check.
- **Response**:
  - Returns the PNR details, including passenger information, flight details, seat, class, and meal preferences.

#### Mock Flight Status

- **Description**: Retrieves the status of a flight.
- **Endpoint**: `/mock/flight/status`
- **Method**: `GET`
- **Request Parameters**:
  - `flightNumber` (string, required): The flight number to check.
- **Response**:
  - Returns the flight status, including departure and arrival times, aircraft details, and last updated timestamp.

#### Mock Ticket Creation

- **Description**: Creates a new ticket.
- **Endpoint**: `/mock/ticket`
- **Method**: `POST`
- **Request Body**:
  - `passenger` (string, required): The name of the passenger.
  - `flight` (string, required): The flight number for the ticket.
- **Response**:
  - Returns the details of the created ticket, including ticket ID, passenger name, and flight details.

#### Mock Ticket Update

- **Description**: Updates an existing ticket.
- **Endpoint**: `/mock/ticket`
- **Method**: `PUT`
- **Request Body**:
  - `ticketId` (string, required): The ID of the ticket to update.
  - `status` (string, optional): The new status of the ticket.
  - `priority` (string, optional): The priority level of the ticket.
- **Response**:
  - Returns the updated ticket details.

#### Mock Ticket Deletion

- **Description**: Deletes a ticket.
- **Endpoint**: `/mock/ticket`
- **Method**: `DELETE`
- **Request Parameters**:
  - `ticketId` (string, required): The ID of the ticket to delete.
- **Response**:
  - Returns a success message confirming the deletion of the ticket.

### Example Usage of `api/call` with Mock APIs

#### Example: Mock PNR Check

- **Example cURL Command**:
  ```bash
  curl -X POST http://localhost:5000/api/call \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_API_KEY" \
  -d '{
    "serviceId": "mock-pnr-check",
    "method": "GET",
    "reqBody": {
      "pnr": "ABC123"
    }
  }'
  ```

#### Example: Mock Flight Status

- **Example cURL Command**:
  ```bash
  curl -X POST http://localhost:5000/api/call \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_API_KEY" \
  -d '{
    "serviceId": "mock-flight-status",
    "method": "GET",
    "reqBody": {
      "flightNumber": "FL123"
    }
  }'
  ```

#### Example: Mock Ticket Creation

- **Example cURL Command**:
  ```bash
  curl -X POST http://localhost:5000/api/call \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_API_KEY" \
  -d '{
    "serviceId": "mock-ticket-create",
    "method": "POST",
    "reqBody": {
      "passenger": "John Doe",
      "flight": "FL123"
    }
  }'
  ```

#### Example: Mock Ticket Update

- **Example cURL Command**:
  ```bash
  curl -X POST http://localhost:5000/api/call \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_API_KEY" \
  -d '{
    "serviceId": "mock-ticket-update",
    "method": "PUT",
    "reqBody": {
      "ticketId": "TKT-123456789",
      "status": "Closed"
    }
  }'
  ```

#### Example: Mock Ticket Deletion

- **Example cURL Command**:
  ```bash
  curl -X POST http://localhost:5000/api/call \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_API_KEY" \
  -d '{
    "serviceId": "mock-ticket-delete",
    "method": "DELETE",
    "reqBody": {
      "ticketId": "TKT-123456789"
    }
  }'
  ```

## Logging

The application uses Winston for logging. Logs are output to the console and can also be configured to write to files in production.
