Phase 1

Create only:

mayo-exception-alert-autoconfigure
mayo-exception-alert-spring-boot-starter
Phase 2

Add:

mayo-platform-bom
sample-app
Phase 3

Extract other cross-app patterns into new starters only when you see repeated usage in 2–3 apps.

That last part matters a lot. Do not create starters too early for things only one app uses.
