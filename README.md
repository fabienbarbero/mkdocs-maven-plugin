# Mkdocs maven plugin

This is a simple plugin to build markdown documentation using [mkdocs](http://www.mkdocs.org/).
You must use Maven 3.5.1+ to use this plugin.


## Goals

### "build" : build the documentation

This goals build the mkdocs documentation to HTML.

| parameter name | required | description                                                  |
| -------------- | -------- | ------------------------------------------------------------ |
| configFile     | yes      | The path to mkdocs configuration file (usually "mkdocs.yml") |

```xml
<plugin>
    <groupId>com.github.fabienbarbero</groupId>
    <artifactId>mkdocs-maven-plugin</artifactId>
    <version>1.1</version>
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

### "gh-deploy" : deploy documentation to github

You can build and deploy the documentation to github pages. Your repository must contains a branch named "gh-pages".
The built documentation will be pushed on this branch.


| parameter name | required | description                                                  |
| -------------- | -------- | ------------------------------------------------------------ |
| configFile     | yes      | The path to mkdocs configuration file (usually "mkdocs.yml") |
| commitMessage  | no       | The commit message used when pushing to "gh-pages" branch    |


```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.fabienbarbero</groupId>
                <artifactId>mkdocs-maven-plugin</artifactId>
                <version>1.1</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>gh-deploy</goal>
                        </goals>
                        <configuration>
                            <configFile>${basedir}/docs/mkdocs.yml</configFile>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
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
