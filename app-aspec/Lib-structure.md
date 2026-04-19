### App Structure

```yaml
mayo-spring-boot-starters/
  pom.xml                      <-- parent / BOM-ish aggregator
  spa-platform-bom/
  spa-exception-alert-core/
  spa-exception-alert-autoconfigure/
  spa-exception-alert-spring-boot-starter/
  spa-exception-alart-demo/
  spa-sftp-support-autoconfigure/
  spa-sftp-support-spring-boot-starter/
  spa-graph-support-autoconfigure/
  spa-graph-support-spring-boot-starter/
  spa-logging-autoconfigure/
  spa-logging-spring-boot-starter/
  spa-test-support/
  docs/
  sample-apps/
    exception-alert-demo/
    sftp-demo/
```


### lib pattern
- For each reusable feature, use two modules:

#### *-core
Put the plain Java/shared Spring classes here:

1. ExceptionAlertContext
2. ExceptionAlertService
3. DefaultExceptionAlertService
4. ExceptionEmailTemplateService
5. maybe SystemMetadataProvider

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
