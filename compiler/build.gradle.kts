plugins {
    `java-library`
}

group = "org.ukma"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":annotation"))
    // https://mvnrepository.com/artifact/com.squareup/javapoet
    implementation("com.squareup:javapoet:1.13.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("generateProcessorConfig") {
    doLast {
        val servicesDir = file("src/main/resources/META-INF/services")
        if (!servicesDir.exists()) {
            servicesDir.mkdirs()
        }

        val processorFile = file("src/main/resources/META-INF/services/javax.annotation.processing.Processor")
        processorFile.writeText("org.ukma.GenerateBuilderProcessor")
    }
}

tasks.named("compileJava") {
    dependsOn("generateProcessorConfig")
}