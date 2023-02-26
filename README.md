# EarthMC-Wrapper
An unofficial Java wrapper library letting you interact with the API of the [EarthMC Dynmap](https://earthmc.net/map/aurora/).

EMCW is built to be intuitive and optimized from the ground up.
<br>This library takes advantage of the following:
- [Google GSON](https://github.com/google/gson) - For serialization/deserialization of classes and objects.
- [Lombok Annotations](https://github.com/projectlombok/lombok) - Automates the process of writing getters/setters.
- Caching + parsing using `ConcurrentHashMap`s
- Interfaces for generic methods like `.single()` and `.all()`
- Multithreading via the use of [Parallelism](https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html).

## Installation (Not yet published)

**Gradle** (build.gradle)
```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/EarthMC-Toolkit/EarthMC-Wrapper")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
  // This will use the latest commit. You can also specify a version instead of 'main-SNAPSHOT'.
  implementation 'io.github.emcw:EMCWrapper:main-SNAPSHOT'
}
```

**Maven** (pom.xml)
```xml
<dependencies>  
  <dependency>
    <groupId>io.github</groupId>
    <artifactId>emcw</artifactId>
    <version>main-SNAPSHOT</version> 
  </dependency>
</dependencies>
```
