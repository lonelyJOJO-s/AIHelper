# Design Notes

## Goal

Build a small local delay queue that is simple enough to read end-to-end and real enough to practice backend concurrency.

## Current Scope

- In-memory storage.
- Standard library HTTP server.
- Submit, list, get, and cancel API shape.
- Scheduler core intentionally left as exercises.

## Future Slices

1. Implement cancel.
2. Implement heap-backed timer loop.
3. Add executable job handlers.
4. Add retry and terminal state tests.
5. Add persistence or Redis stream variant.
