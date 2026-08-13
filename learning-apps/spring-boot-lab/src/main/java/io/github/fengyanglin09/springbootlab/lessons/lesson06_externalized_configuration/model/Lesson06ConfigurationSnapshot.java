package io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration.model;

import java.time.Duration;
import java.util.List;

/**
 * A small report about the configuration values observed by Lesson 06.
 *
 * <p>The snapshot intentionally stores both raw Environment values and typed
 * @ConfigurationProperties values. That lets the test prove that the same
 * external configuration can be read as strings from the Environment and as a
 * structured Java object through binding.</p>
 */
public record Lesson06ConfigurationSnapshot(
        List<String> activeProfiles,
        String environmentRegion,
        String environmentRefreshInterval,
        String environmentRequireHttps,
        String environmentTokenAudience,
        String boundRegion,
        Duration boundRefreshInterval,
        boolean boundRequireHttps,
        String boundTokenAudience
) {

    /**
     * Command-line arguments should override file-based config data.
     *
     * <p>The test passes {@code --lesson06.lab.region=command-line-region} and
     * {@code --lesson06.lab.security.require-https=false}. Those values should
     * win over both the base YAML file and the profile-specific YAML file.</p>
     */
    public boolean commandLineOverridesWon() {
        return "command-line-region".equals(boundRegion)
                && !boundRequireHttps;
    }

    /**
     * The active {@code dev} profile should make the dev YAML file participate.
     *
     * <p>The base file provides defaults. The dev profile file overrides some of
     * those defaults. The test expects the refresh interval and token audience to
     * come from {@code lesson06-application-dev.yml}.</p>
     */
    public boolean profileSpecificYamlWasUsed() {
        return activeProfiles.contains("dev")
                && Duration.ofSeconds(5).equals(boundRefreshInterval)
                && "dev-audience".equals(boundTokenAudience);
    }

    /**
     * Environment and @ConfigurationProperties should describe the same final
     * resolved values.
     *
     * <p>The Environment exposes string-like property values. The bound object
     * exposes typed values such as Duration and boolean.</p>
     */
    public boolean environmentAndTypedBindingAgree() {
        return environmentRegion.equals(boundRegion)
                && "5s".equals(environmentRefreshInterval)
                && "false".equals(environmentRequireHttps)
                && environmentTokenAudience.equals(boundTokenAudience);
    }
}
