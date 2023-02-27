# EarthMC-Wrapper
An unofficial Java API wrapper library allowing interaction with the [EarthMC Dynmap](https://earthmc.net/map/aurora/).

EMCW is built to be intuitive and optimized from the ground up.
<br>This library takes advantage of the following:
- [Google GSON](https://github.com/google/gson) - For serialization/deserialization of classes and objects.
- [Lombok Annotations](https://github.com/projectlombok/lombok) - Automates the process of writing getters/setters.
- Caching + parsing using `ConcurrentHashMap`s
- Interfaces for generic methods like `.single()` and `.all()`
- Multithreading via the use of [Parallelism](https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html).

## Installation
- ### Authenticate to GitHub Packages
1. Create a file named `.env` in your project root and include it in your `.gitignore`.
2. Head to `Account` -> `Developer Settings` -> `Personal Access Token (classic)` -> `Generate New Token`.
3. Copy & paste the token and your account name into the `.env` file like so: 
    ```txt
    USERNAME=yourGithubUsername
    TOKEN=yourTokenHere
    ```
4. Paste the following code at the top of your `build.gradle` file.
    ```gradle
    Properties properties = new Properties()
    def propertiesFile = project.rootProject.file('.env')
    if (propertiesFile.exists()) {
        properties.load(propertiesFile.newDataInputStream())
    }
    ```

- ### Add package dependency to build file
    #### Gradle (build.gradle)

    ```gradle
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/earthmc-toolkit/earthmc-wrapper")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }

    dependencies {
      // NOTE: This may not be up-to-date! Make sure to replace this version with the latest.
      implementation 'io.github.emcw:emc-wrapper:0.3.4'
    }
    ```

- ### Import and Initialize

    ```java
    import io.github.emcw.core.*;
    import io.github.emcw.objects.Town;
    import java.util.List;

    EMCMap Aurora = new EMCWrapper().Aurora;
    List<Town> all = Aurora.Towns.all();

    System.out.println(all.size());
    ```
