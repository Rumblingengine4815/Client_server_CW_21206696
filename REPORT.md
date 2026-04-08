# Smart Campus API Report (5COSC022W)

## Part 1.1: JAX-RS Resource Lifecycle
By default, JAX-RS creates a new instance of a resource class for each request (request scope) unless explicitly configured as singleton. This behavior reduces accidental shared state in resource classes. However, in-memory data stores are still shared globally, so thread-safe collections are required to prevent race conditions and data corruption under concurrent requests.

## Part 1.2: Hypermedia (HATEOAS)
Hypermedia allows clients to discover available actions through links in responses instead of relying only on static documentation. This improves evolvability and reduces client breakage when endpoints change, because navigation is included dynamically in API responses.

## Part 2.1: Returning IDs vs Full Room Objects
Returning only IDs is bandwidth-efficient and better for large lists, but requires additional calls by clients to get details. Returning full objects is more convenient for clients and reduces round trips, but increases payload size. A practical design is summary data for collection endpoints and full data for single-resource endpoints.

## Part 2.2: DELETE Idempotency
DELETE is idempotent because repeating the same delete operation does not create further state changes after the first successful deletion. The first call deletes the room; subsequent identical calls keep the room absent. The status code may differ (for example 204 then 404), but idempotency is based on server state, not identical responses.

## Part 3.1: @Consumes(MediaType.APPLICATION_JSON)
This annotation restricts accepted request payload type to JSON. If a client sends an unsupported media type such as text/plain or application/xml, JAX-RS returns 415 Unsupported Media Type because no matching message body reader is available for that endpoint.

## Part 3.2: QueryParam vs Path Segment for Filtering
Using query parameters for filtering (for example /sensors?type=CO2) is preferable because it represents optional search criteria over one collection resource. Path segments are better suited for resource identity and hierarchy (for example /rooms/{id}). Query params are more flexible for combining multiple filters.

## Part 4.1: Sub-Resource Locator Benefits
Sub-resource locators separate nested concerns (readings under sensors) into dedicated classes. This improves maintainability, readability, and testability, and avoids oversized controller classes with too many nested routes and responsibilities.

## Part 5.2: Why 422 for Missing Linked Resource
422 Unprocessable Entity is semantically accurate when request syntax is valid JSON but domain validation fails (for example roomId reference does not exist). 404 is more appropriate when the requested URI resource is missing, not when a nested payload reference is invalid.

## Part 5.4: Security Risk of Exposing Stack Traces
Raw stack traces reveal internal implementation details such as package names, class names, file paths, line numbers, framework versions, and logic flow. Attackers can use this information for reconnaissance and targeted exploitation. Returning sanitized generic errors improves security posture.
