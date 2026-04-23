1. finish the POM metadata for every publishable module (define in the parent and override in the child)
- use following command to check the effective POM for each module and make sure the metadata is correct:
```shell
./mvnw help:effective-pom -pl spa-exception-alert-starter
```
2. make sure sources, javadocs, and signing work locally
3. set up Central namespace and token
4. add the Central publishing plugin
5. run a local dry run with mvn -Prelease clean deploy using a local settings.xml
6. add the GitHub Action
7. create a GitHub release to trigger publishing



- note to keep a release profile with the Central publishing plugins, and keep the regular build free of those plugins so that it doesn't require the Central credentials to build locally:

```yaml
Keep these plugins in normal builds:

compiler
surefire
failsafe
jar
flatten

Move these into a release profile:

central-publishing-maven-plugin
maven-source-plugin
maven-javadoc-plugin
maven-gpg-plugin
```

the command to run release profile locally:

```shell
export CENTRAL_TOKEN_USERNAME=your-central-username
export CENTRAL_TOKEN_PASSWORD=your-central-token
export GPG_PASSPHRASE=your-gpg-passphrase
./mvnw -s app-aspec/settings-central.xml.example -Prelease clean deploy
```

recommended order for the very first release:

```text
1. Verify your namespace in https://central.sonatype.com/
2. Generate a Central token in the portal
3. Generate and publish a GPG public key
4. Run ./mvnw -Prelease clean deploy locally
5. Push the repo and configure GitHub secrets
6. Create a GitHub release to trigger .github/workflows/publish.yml
```
