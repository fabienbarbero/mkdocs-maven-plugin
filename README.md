# Mkdocs maven plugin

This is a simple plugin to build markdown documentation using [mkdocs](http://www.mkdocs.org/).


## Usage

The plugin use the goal "build" and must link a mkdocs config file (by default this file is named "mkdocs.yml")

```xml
<plugin>
    <groupId>com.github.fabienbarbero</groupId>
    <artifactId>mkdocs-maven-plugin</artifactId>
    <version>1.0</version>
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

The built documentation can be found in "target/mkdocs/{executionId}".
A ZIP archive is also built and stored in the "target" directory. It is attached to the project and can be retrieved
in another project when using the following dependency :

```xml
<dependency>
    <groupId>my.project</groupId>
    <artifactId>Test</artifactId>
    <type>mkdocs</type>
    <classifier>test</classifier> <!-- This is the execution id defined when generating the documentation -->
</dependency>
``` 

## How installing mkdocs

First of all you must install python an pip :
```text
sudo apt install python-pip
```
Then install mkdocs via pip
```text
sudo pip install mkdocs
```

For more details, please visit the [mkdocs web site](http://www.mkdocs.org/).
