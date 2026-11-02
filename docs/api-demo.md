# Catalog Service API Demo

## Create a Book — 201 Created

Command:

```bash
(base) pratikpatel@Mac catalog-service % curl -i -X POST http://localhost:9001/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "1234567891",
    "title": "Northern Lights",
    "author": "Lyra Silverstar",
    "price": 9.90
  }'
HTTP/1.1 201 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 02 Nov 2026 20:33:20 GMT

{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}%                                                                         (base) pratikpatel@Mac catalog-service % curl -i http://localhost:9001/books/1234567891
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 02 Nov 2026 20:33:38 GMT

{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}%                                                                         (base) pratikpatel@Mac catalog-service % curl -i -X POST http://localhost:9001/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "123ABC456Z",
    "title": "",
    "author": "Jon Snow",
    "price": 9.90
  }'
HTTP/1.1 400 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 02 Nov 2026 20:33:46 GMT
Connection: close

{"isbn":"The ISBN format must be valid.","title":"The book title must be defined."}%                                                                            (base) pratikpatel@Mac catalog-service % curl -i -X POST http://localhost:9001/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "1234567891",
    "title": "Northern Lights",
    "author": "Lyra Silverstar",
    "price": 9.90
  }'
HTTP/1.1 422 
Content-Type: text/plain;charset=UTF-8
Content-Length: 43
Date: Mon, 02 Nov 2026 20:33:55 GMT

A book with ISBN 1234567891 already exists.%                                    (base) pratikpatel@Mac catalog-service % 
