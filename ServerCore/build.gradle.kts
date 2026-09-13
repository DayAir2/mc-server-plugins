plugins {
    java
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "com.servercore"
version = "0.1.0"

// ---------------------------------------------------------------------------
// Target platform. These are verified-real coordinates, not guesses:
//   paper-api 26.2.build.123-stable  (Paper's 2026 calendar versioning)
//   Java 25                          (Paper 26.1+ minimum)
// ---------------------------------------------------------------------------
val paperApiVersion = "26.2.build.+"
val minecraftVersion = "26.2"

// Runtime libraries. These are NOT shaded into the jar -- they are declared in
// plugin.yml under `libraries:` and downloaded by Paper's library loader at
// startup. compileOnly here so we can compile against them.
val sqliteVersion = "3.53.4.0"
val hikariVersion = "7.1.0"
val caffeineVersion = "3.2.4"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperApiVersion")

    compileOnly("org.xerial:sqlite-jdbc:$sqliteVersion")
    compileOnly("com.zaxxer:HikariCP:$hikariVersion")
    compileOnly("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")

    testImplementation("io.papermc.paper:paper-api:$paperApiVersion")
    testImplementation("org.xerial:sqlite-jdbc:$sqliteVersion")
    testImplementation("com.zaxxer:HikariCP:$hikariVersion")
    testImplementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all,-serial,-processing", "-parameters"))
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
    }
}

// Inject the Gradle version + library versions into plugin.yml so they can
// never drift out of sync with the build script.
tasks.processResources {
    val props = mapOf(
        "version" to project.version.toString(),
        "apiVersion" to minecraftVersion,
        "sqliteVersion" to sqliteVersion,
        "hikariVersion" to hikariVersion,
        "caffeineVersion" to caffeineVersion,
    )
    inputs.properties(props)
    filesMatching("plugin.yml") {
        expand(props)
    }
}

// Downloads a real Paper server into run/ and starts it with this plugin.
// Note that Gradle does not forward stdin, so you cannot type `stop` into the
// console -- enable RCON in run/server.properties to drive it from a script.
tasks.runServer {
    minecraftVersion(minecraftVersion)
}
