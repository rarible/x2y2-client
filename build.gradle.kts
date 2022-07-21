plugins {
    kotlin("jvm") version "1.7.10"
    `java-library`
    `maven-publish`
}

group = "com.rarible.x2y2"
version = "1.0"

repositories {
    mavenCentral()

    maven {
        url = uri("https://repo.rarible.org/repository/maven-public")
        metadataSources {
            mavenPom()
            artifact()
        }
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("org.springframework.boot:spring-boot:2.7.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.6.2")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-test:2.7.0")
    testImplementation("org.springframework:spring-test:5.3.20")
    testImplementation("com.squareup.okhttp3:okhttp:4.10.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.3.1")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.8.2")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        reports {
            junitXml.required.set(true)
            junitXml.mergeReruns.set(true)
            junitXml.outputLocation.set(
                project.buildDir.resolve("surefire-reports")
            )
        }

    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    val sourceJar by creating(Jar::class) {
        dependsOn(classes)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    artifacts {
        add("archives", sourceJar)
    }

    publishing {
        repositories {
            maven {
                url = uri("http://nexus-ext.rarible.int/repository/maven-releases/")
                isAllowInsecureProtocol = true
                credentials.username = System.getenv("GRADLE_NEXUS_USER")
                credentials.password = System.getenv("GRADLE_NEXUS_PASS")
            }
            mavenLocal()
        }
        publications {
            create<MavenPublication>("mavenJava") {
                from(project.components["java"])
                artifact(sourceJar)

                pom {
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://github.com/rarible/x2y2-client/blob/main/LICENSE.md")
                        }
                    }
                    name.set(project.name)
                    url.set("https://rarible.org")
                    description.set("Web client to get orders/offers and events from x2y2 API")
                    scm {
                        url.set("https://github.com/rarible/x2y2-client")
                        connection.set("scm:git:git@github.com:rarible/x2y2-client.git")
                        developerConnection.set("scm:git:git@github.com:rarible/x2y2-client.git")
                    }
                    developers {
                        developer {
                            name.set("Rarible protocol")
                            url.set("https://rarible.org")
                        }
                    }
                }
            }
        }
    }
}
