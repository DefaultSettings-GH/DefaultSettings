[![Build Status](https://gitlab.com/jomcraft-sources/defaultsettings/badges/1.8.x-1.12.x/pipeline.svg)](https://gitlab.com/jomcraft-sources/defaultsettings/tree/1.8.x-1.12.x)

### DefaultSettings

---

Adding DefaultSettings as a dependency for your mod (use DS as an API)

##### Add this to your build.gradle

```md
repositories {
    maven {
        url "https://maven.jomcraft.net/repository/release"
    }
}

dependencies {
   deobfCompile 'de.pt400c.defaultsettings:DefaultSettings-[EDITION]:[VERSION]'
}
```

Edition & version could be grabbed from [here](https://maven.jomcraft.net/repository/release/de/pt400c/defaultsettings/).