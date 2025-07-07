# iUI API

This is the API module of the **iUI** repository.
A lightweight, flexible inventory UI library for Bukkit/Spigot plugins. Easily create custom GUIs for your Minecraft server with minimal code.

## Features
- Simple API for creating inventory-based UIs
- Clickable items with custom actions
- Async event handling
- Customizable titles, backgrounds, and sounds

## Installation (API module only)

Add the following to your `build.gradle` or `pom.xml` using [JitPack](https://jitpack.io):

### Gradle
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.diesesfloo.iUI:api:{version}'
}
```

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.github.diesesfloo.iUI</groupId>
        <artifactId>api</artifactId>
        <version>{version}</version>
    </dependency>
</dependencies>
```

## Example Usage

```java
JavaPlugin plugin = ...;
UserInterface ui = new UserInterface(plugin)
    .setLines(3)
    .setTitle(Component.text("My Custom GUI"));

ClickableItem item = new ClickableItem(new ItemStack(Material.DIAMOND))
    .setOnClick(() -> System.out.println("Diamond clicked!"));

ui.setItem(13, item); // Set item in the center slot

// To open the UI for a player:
ui.open(player);
```

## License
MIT
