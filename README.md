# EarthMC-Wrapper
An unofficial Java API wrapper library allowing interaction with the [EarthMC Dynmap](https://earthmc.net/map/aurora/).

EMCW is built to be intuitive and optimized from the ground up.
<br>This library takes advantage of the following:
- Multithreading via the use of [Parallelism](https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html).
- [Google GSON](https://github.com/google/gson) - For serialization/deserialization of classes and objects.
- [Lombok Annotations](https://github.com/projectlombok/lombok) - Automates the process of writing getters/setters.
- [Interfaces](https://docs.oracle.com/javase/tutorial/java/IandI/createinterface.html) and [Abstraction](https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html) to achieve multiple inheritance while increasing modularity and re-usability.
- [Caffeine](https://github.com/ben-manes/caffeine) - High performance caching library built on top of `ConcurrentHashMap`, providing better concurrency & memory management with support for both size and time based eviction of entries.
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
      api 'io.github.emcw:emc-wrapper:0.8.6'
    }
    ```

- ### Import and Initialize

    ```java
    import io.github.emcw.core.*;
    import io.github.emcw.objects.*;
    import java.util.Map;

    public class Main {
        // Choose which maps we should initialize.
        static EMCWrapper emc = new EMCWrapper(true, false);
        static EMCMap Aurora, Nova;
  
        public static void main(String[] args) {
            Aurora = emc.getAurora(); // New instance of 'EMCMap'
            Nova = emc.getNova(); // Will return 'null' since we set false
  
            doSomethingWithTowns();
        }
  
        static void doSomethingWithTowns() {
            Map<String, Town> all = Aurora.Towns.all();
            System.out.println(all.size());
        }
    }
    ```
  
## Documentation
> **Javadoc**<br>
> You currently won't see any tooltips as you are using **EMCW**. However, I plan to gradually document new and existing fields, methods & classes post ***v1.0.0***.

<p><b>TLDR;</b><br>
Since this library uses Lombok, it is most likely that fields you try to access are private.<br>
Although public getters are provided which you can use like so:

```java
// Example entity
public class Nation {
    @Getter List<String> towns;
    @Getter String leader;
}

// Usage
public class Test {
    // Here we can see Lombok in use.
    // getAurora(), getTowns(), getLeader()
    
    EMCMap Aurora = new EMCWrapper(true, false).getAurora();
    
    public static void main(String[] args) {
        Nation exampleNation = Aurora.Nations.single("nationName");
        
        String leader = exampleNation.getLeader();
        Integer amtOfTowns = exampleNation.getTowns().size();
    }
}
```
</p>

- All map classes inherit methods:
  - `.all()` - Retrieve the entire map of entities.
  - `.single("name")` - Retrieve a single entity by its name.
  - `.get("name", "anotherName")` - Returns a map of entities by inputting their names.

### Map Classes
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