# my-spring-lib-starter

Shared Spring Boot libraries for exception handling and alerting.

## Modules

- `my-spring-lib-bom`: BOM for the published modules in this repository.
- `spa-web-error-core`: Shared API error model and base exception handlers.
- `spa-exception-alert-autoconfigure`: Spring Boot auto-configuration for exception alerting.
- `spa-exception-alert-starter`: Starter that brings in the alert auto-configuration.

## Local build

This repository uses the Maven Wrapper. Once Java is installed, run:

```bash
./mvnw clean verify
```

## Publish to Maven Central

The release profile signs artifacts, attaches source and javadoc jars, and publishes through the Sonatype Central Publishing plugin:

```bash
./mvnw -Prelease clean deploy
```

You will need:

- A verified `io.github.fengyanglin09` namespace in the Sonatype Central Portal.
- A Central token stored in your Maven `settings.xml` under server id `central`.
- A GPG key available locally for signing.
- The GitHub Actions secrets described in `.github/workflows/publish.yml` if you want to publish from GitHub.

See `app-aspec/steps-for-package-publish.md` for the release checklist.
