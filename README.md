# Radio [![Travis CI](https://secure.travis-ci.org/MinerAp/radio.png)](http://travis-ci.org/#!/MinerAp/radio)

Radio is a plugin for Bukkit which allows for player-created radios for the purpose of long-distance message transmissions.
It is meant to be run on a server with a global chat limit (i.e. players can only talk to those nearby them), but this is not required for the plugin.

## Administrators

### Download

You can find the latest builds of the plugin at my [ci](http://ci.nikitapek.in/job/radio/).
You can find stable downloads with detailed changelog information in the [files](http://dev.bukkit.org/bukkit-plugins/radio/files/) section of the plugin's BukkitDev page.

### Installation

Simply drop the latest .jar into the /plugins directory of your server.

### Usage

Further plugin information can be found at the plugin's [BukkitDev](http://dev.bukkit.org/bukkit-plugins/radio/) page.

## Developers

Radio does not have a formal API at the moment, but you can download the latest version via maven by adding the following snippets to your plugin's pom.xml.

### Repository

    <repositories>
      <repository>
        <id>indiv0's Repo</id>
        <url>http://repo.nikitapek.in/maven/releases</url>
      </repository>
    </repositories>

### Dependency

    <dependency>
      <groupId>in.nikitapek</groupId>
      <artifactId>radio</artifactId>
      <version>1.16.0</version>
    </dependency>
