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
1. Head to `Account` -> `Developer Settings` -> `Personal Access Token (classic)` -> `Generate New Token`
2. Give it any name and the appropriate repository permissions and hit 'Generate'
3. On your local system, create two new system environment variables like so:
    ```txt
    Name: USERNAME
    Value: yourGitHubUsername
    ```
   
    ```
    Name: GITHUB_TOKEN
    Value: yourTokenHere
    ```

- ### Add package dependency to build file
    #### Gradle (build.gradle)

    ```gradle
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/earthmc-toolkit/earthmc-wrapper")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    dependencies {
      // NOTE: This may not be up-to-date! Make sure to replace this version with the latest.
      implementation 'io.github.emcw:emc-wrapper:0.4.2'
    }
    ```

- ### Import and Initialize

    ```java
    import io.github.emcw.core.EMCMap;
    import io.github.emcw.core.EMCWrapper;
    import io.github.emcw.objects.*;
    import java.util.List;

    public class Main {
        EMCMap Aurora = new EMCWrapper().Aurora;
  
        public static void main(String[] args) {
            List<Town> all = Aurora.Towns.all();
            System.out.println(all.size());
        }
    }
    ```
