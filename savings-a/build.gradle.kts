plugins {
    id("workspace-projects.spring-conventions")
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

//val springfoxVersion by extra("3.0.0")
val testContainersVersion by extra("1.16.3")

testSets {
    "integrationTest"()
}

tasks.withType<Test> {
    outputs.upToDateWhen {false}
    testLogging.showStandardStreams = true
}

configurations {
    create("runtimeAgent")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework.security:spring-security-config")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.6")
//    implementation("io.springfox:springfox-boot-starter:$springfoxVersion")
//    implementation("io.springfox:springfox-swagger-ui:$springfoxVersion")
    implementation("org.liquibase:liquibase-core")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test:5.6.2")

    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")

    // https://mvnrepository.com/artifact/it.unimi.dsi/dsiutils
    testImplementation("it.unimi.dsi:dsiutils:2.7.0")

    "runtimeAgent"("org.springframework:spring-instrument")
    "runtimeAgent"("org.aspectj:aspectjweaver")
}

tasks.withType<Test> {
    doFirst {
        // Ensure that all of the agents we need to load at run-time happen for tests
        configurations.getByName("runtimeAgent").onEach {
            val jvmMutableList = jvmArgs?.toMutableList()
            jvmMutableList?.add("-javaagent:${it.absolutePath}")
            jvmArgs = jvmMutableList?.toList()
        }
    }
}
