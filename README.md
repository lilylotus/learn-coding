# Diversity
各种各样、随性的编码

# Gradle `api` And `implementation`

[Removal of compile and runtime configurations](https://docs.gradle.org/current/userguide/upgrading_version_6.html#sec:configuration_removal)

Since its inception, Gradle provided the `compile` and `runtime` configurations to declare dependencies. These however did not support a fine grained scoping of dependencies. Hence, better replacements were introduced in Gradle 3.4:

- The `implementation` configuration should be used to declare dependencies which are *implementation details* of a library: they are not visible to consumers of the library during compilation time.
- The `api` configuration, available only if you apply the `java-library` plugin, should be used to declare dependencies which are part of the API of a library, that need to be exposed to consumers at compilation time.

In Gradle 7, both the `compile` and `runtime` configurations are removed. Therefore, you have to migrate to the `implementation` and `api` configurations above. If you are still using the `java` plugin for a Java library, you will need to apply the `java-library` plugin instead.

**注意：** `api` 需要插件 `java-library` 的支持

| Removed Configuration | New Configuration         |
| :-------------------- | :------------------------ |
| `compile`             | `api` or `implementation` |
| `runtime`             | `runtimeOnly`             |
| `testRuntime`         | `testRuntimeOnly`         |
| `testCompile`         | `testImplementation`      |
| `Runtime`             | `RuntimeOnly`             |
| `Compile`             | `Implementation`          |

*Table 4. Mapping between Java module directives and Gradle configurations to declare dependencies*

| Java Module Directive        | Gradle Configuration | Purpose                                 |
| :--------------------------- | :------------------- | :-------------------------------------- |
| `requires`                   | `implementation`     | Declaring implementation dependencies   |
| `requires transitive`        | `api`                | Declaring API dependencies              |
| `requires static`            | `compileOnly`        | Declaring compile only dependencies     |
| `requires static transitive` | `compileOnlyApi`     | Declaring compile only API dependencies |
