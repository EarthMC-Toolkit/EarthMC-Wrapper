# EarthMC-Wrapper
[![view - Documentation](https://img.shields.io/badge/view-Documentation-blue?style=for-the-badge)](https://earthmc-toolkit.github.io/EarthMC-Wrapper/ "Go to project documentation")

An unofficial Java API wrapper/client to interact with the [EarthMC Map](https://earthmc.net/map/aurora/) and [Official API](https://earthmc.net/docs/api).\
EMCW is built to be intuitive and optimized as it takes advantage of the following:
- Multithreading via the use of [Parallelism](https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html).
- [Caffeine](https://github.com/ben-manes/caffeine) - High performance caching library built on top of `ConcurrentHashMap`, providing better concurrency & memory management with support for both size and time based eviction of entries.
- [Google GSON](https://github.com/google/gson) - For serializing/deserializing objects to and from JSON.
- [Lombok Annotations](https://github.com/projectlombok/lombok) - Automates the process of writing getters/setters.
  
## Installation
- ### Authenticate with GitHub Packages
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

- ### Add package dependency to build file.
  
    `build.gradle`
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
      api 'io.github.emcw:emc-wrapper:0.12.0'
    }
    ```

- ### Import and initialize.
    ```java
    import io.github.emcw.EMCMap;
    import io.github.emcw.EMCWrapper;
    import io.github.emcw.KnownMap;
    
    import java.util.Map;

    public class Main {
        static final EMCWrapper emcw = new EMCWrapper(KnownMap.AURORA);
        static final EMCMap auroraMap = emcw.getMap(KnownMap.AURORA);

        static OfficialAPI.V3 auroraOAPI = new OfficialAPI.V3(KnownMap.AURORA);
    
        public static void main(String[] args) {
            // Call the map
            Map<String, Town> all = auroraMap.Towns.all();
            System.out.println(all.size());

            // Call the Official API
            System.out.println(auroraOAPI.serverInfo());
        }
    }
    ```
  
## Documentation
> [!NOTE]
> You currently won't see much embedded documentation as you are using **EMCW**. However, I plan to gradually document new and existing fields, methods & classes post ***v1.0.0*** to give more context.
> For now, the syntax should be closely similar to the [NPM Package](https://www.npmjs.com/package/earthmc) although wrapper/map initialization may slightly differ.
<br><br>
> [Visit the Javadoc page.](https://earthmc-toolkit.github.io/EarthMC-Wrapper/index-all.html)

> [!NOTE]
> Since this library uses Lombok, it is most likely that fields you try to access are private, though public getters are provided.
> ```java
> // Example EMCW class
> public class Nation {
>     @Getter String leader;
> }
> 
> // Usage
> public class Test {
>     public static void main(String[] args) {
>         Nation exampleNation = Aurora.Nations.single("nationName");
>
>         // Here we can see Lombok in use.
>         String leader = exampleNation.leader; // Does not work
>         String leader = exampleNation.getLeader(); // Works
>    }
> }
> ```
</p>

### Map Classes
- All map classes inherit the following methods:
  - `.all()` - Retrieve the entire map of entities.
  - `.single("name")` - Retrieve a single entity by its name.
  - `.get("name", "anotherName")` - Returns a map of entities by inputting their names.

<details>
  <summary><b>Towns</b></summary>
</details>

<details>
  <summary><b>Nations</b></summary>
</details>

<details>
  <summary><b>Residents</b></summary>
</details>

<details>
  <summary><b>Players</b></summary>
</details>
