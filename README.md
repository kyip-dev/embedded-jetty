Embedded Jetty Server
======

Copy the webappStartConfig to any folder, and pass it as system property when executing jar

e.g.
``` 
-DconfigFilePath=C:\etc\webappStartConfig.properties
```


Command sample of starting embedded Jetty with JSP supported enabled:

```
java -jar -DconfigFilePath=C:\etc\webappStartConfig.properties -Dorg.apache.jasper.compiler.disablejsr199=true target/start-embedded-jetty-server-0.0.1-SNAPSHOT.jar
```

