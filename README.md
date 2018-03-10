# Mkdocs maven plugin

This is a simple plugin to build markdown documentation using [mkdocs](http://www.mkdocs.org/).

## Usage

The plugin use the goal "build" and must link a mkdocs config file (by default this file is named "mkdocs.yml")

```xml
<plugin>
    <groupId>com.github.fabienbarbero</groupId>
    <artifactId>mkdocs-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <executions>
        <execution>
            <id>test</id>
            <goals>
                <goal>build</goal>
            </goals>
            <configuration>
                <configFile>${basedir}/src/site/documentation/mkdocs.yml</configFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```