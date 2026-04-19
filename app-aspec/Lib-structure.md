### App Structure

```yaml
mayo-spring-boot-starters/
  pom.xml                      <-- parent / BOM-ish aggregator
  mayo-platform-bom/
  mayo-exception-alert-autoconfigure/
  mayo-exception-alert-spring-boot-starter/
  mayo-sftp-support-autoconfigure/
  mayo-sftp-support-spring-boot-starter/
  mayo-graph-support-autoconfigure/
  mayo-graph-support-spring-boot-starter/
  mayo-logging-autoconfigure/
  mayo-logging-spring-boot-starter/
  mayo-test-support/
  docs/
  sample-apps/
    exception-alert-demo/
    sftp-demo/
```


### lib pattern
- For each reusable feature, use two modules:

#### *-autoconfigure

Contains:
1. @AutoConfiguration
2. @ConfigurationProperties
3. default beans
4. conditions like @ConditionalOnClass, @ConditionalOnMissingBean, @ConditionalOnProperty

#### *-spring-boot-starter

Contains:
1. dependencies only
2. depends on the matching *-autoconfigure
3. pulls in third-party libs the feature needs

That is the standard custom starter pattern described in the Spring Boot docs.


### Rules

#### Put in a starter
1. stable patterns used across multiple apps
2. generic defaults
3. opt-in auto-configuration
4. externalized properties
5. interfaces and default implementations

#### Do not put in a starter
1. app-specific business rules
2. app-specific exception classes
3. one-off workflow logic
4. environment-specific secrets
5. hardcoded recipients or paths
