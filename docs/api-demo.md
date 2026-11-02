# Catalog Service API Demo

The following commands demonstrate the main Catalog Service REST API responses.

## 1. Create a Book — 201 Created

```bash
curl -i -X POST http://localhost:9001/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "0306406152",
    "title": "Northern Lights",
    "author": "Lyra Silverstar",
    "price": 9.90
  }'
```

Expected response:

```text
HTTP/1.1 201
Content-Type: application/json

{"isbn":"0306406152","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}
```

## 2. Get All Books — 200 OK

```bash
curl -i http://localhost:9001/books
```

Expected response:

```text
HTTP/1.1 200
Content-Type: application/json
```

The response body contains the current list of books.

## 3. Get an Existing Book — 200 OK

```bash
curl -i http://localhost:9001/books/0306406152
```

Expected response:

```text
HTTP/1.1 200
Content-Type: application/json

{"isbn":"0306406152","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}
```

## 4. Get a Missing Book — 404 Not Found

```bash
curl -i http://localhost:9001/books/9780306406157
```

Expected response:

```text
HTTP/1.1 404
Content-Type: text/plain;charset=UTF-8

The book with ISBN 9780306406157 was not found.
```

## 5. Submit an Invalid Book — 400 Bad Request

```bash
curl -i -X POST http://localhost:9001/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "123ABC456Z",
    "title": "",
    "author": "Jon Snow",
    "price": 9.90
  }'
```

Expected response:

```text
HTTP/1.1 400
Content-Type: application/json

{"isbn":"The ISBN format must be valid.","title":"The book title must be defined."}
```

## 6. Create a Duplicate Book — 422 Unprocessable Entity

```bash
curl -i -X POST http://localhost:9001/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "0306406152",
    "title": "Northern Lights",
    "author": "Lyra Silverstar",
    "price": 9.90
  }'
```

Expected response:

```text
HTTP/1.1 422
Content-Type: text/plain;charset=UTF-8

A book with ISBN 0306406152 already exists.
```

## 7. Update an Existing Book — 200 OK

```bash
curl -i -X PUT http://localhost:9001/books/0306406152 \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "0306406152",
    "title": "Northern Lights Updated",
    "author": "Lyra Silverstar",
    "price": 12.99
  }'
```

Expected response:

```text
HTTP/1.1 200
Content-Type: application/json

{"isbn":"0306406152","title":"Northern Lights Updated","author":"Lyra Silverstar","price":12.99}
```

## 8. Delete a Book — 204 No Content

```bash
curl -i -X DELETE http://localhost:9001/books/0306406152
```

Expected response:

```text
HTTP/1.1 204
```

## Additional Exercise Behavior

For E1, a PUT request that creates a new book returns `201 Created`.

For E2, the ISBN in the URL path must match the ISBN in the request body. If they differ, the API returns `400 Bad Request`.