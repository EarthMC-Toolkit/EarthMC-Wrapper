plugins {
    id("java")
    id("java-library")
    id("maven-publish")

    kotlin("plugin.lombok") version "1.9.10"
    id("io.freefair.lombok") version "6.6"

    kotlin("kapt") version "1.9.10"
}

kotlinLombok {
    lombokConfigurationFile(file("lombok.config"))
}

kapt {
    keepJavacAnnotationProcessors = true
}

group = project.property("maven_group").toString()
version = project.property("wrapper_version").toString()

allprojects {
    repositories {
        mavenCentral()

        repositories {
            maven {
                url = uri("https://maven.pkg.github.com/earthmc-toolkit/earthmc-wrapper")
                credentials {
                    username = project.property("USERNAME").toString()
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

dependencies {
    //#region Implementations for "main"
    compileOnly("org.jetbrains:annotations:24.0.1")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.jsoup:jsoup:1.15.4")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:okhttp-brotli:4.10.0")

    implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")
    //#endregion

    //#region Implementations for "test"
    testCompileOnly("io.github.emcw:emc-wrapper:${version}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    //#endregion

    //#region Plugins
//    compileOnly("org.projectlombok:lombok:1.18.26")
//    annotationProcessor("org.projectlombok:lombok:1.18.26")
    //#endregion
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
    //withJavadocJar()
}

tasks.jar {
    // Will include every single runtime dependency
    from(configurations.runtimeClasspath.get().map{ if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

publishing {
    java {
        tasks.withType<Javadoc>().configureEach {
            isFailOnError = false
            options {
                this as StandardJavadocDocletOptions

                addStringOption("Xdoclint:none", "-quiet")
                addStringOption("encoding", "UTF-8")
                addStringOption("charSet", "UTF-8")
            }
        }

        withJavadocJar()
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/earthmc-toolkit/earthmc-wrapper")
            credentials {
                username = project.property("USERNAME").toString()
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = property("maven_group").toString()
            artifactId = property("lib_name").toString()

            version = property("wrapper_version").toString()

            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}