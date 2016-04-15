Datalake website
================

This application makes it possible to upload MS-Access files and then query those with an SQL engine.

The application uses Liquibase to create and upgrade the database, and Thymeleaf as the templating engine.
You can find the layout template at src/main/webapp/WEB-INF/thymeleaf/layout.html.

Dependencies
------------
* Tomcat 7
* Java 1.7
* Spring 4
* Thymeleaf 2.1.4
* H2 Database Engine

Automated tests
---------------
There are test examples of both controllers and data access objects using the Spring test package.

Building a Docker image
-----------------------

It is possible to build, test and push a Docker image of Eionet DataLake to EEA's Docker registry. To do so you activate the `docker` profile. The `install` goal will do a test start up of the container. The `docker:push` will push the Docker image to dockerrepo.eionet.europa.eu:5000.
```
mvn -Pdocker install docker:push
```
To use `docker:push` you must have an account and add these lines to your `~/.m2/settings.xml`:
```
<server>
  <id>dockerrepo.eionet.europa.eu:5000</id>
  <username>{account}</username>
  <password>{password}</password>
</server>
```

Deployment of WAR file
----------------------
The default configuration is to allow you to deploy to your own workstation directly. You install the target/datalake.war to Tomcat's webapps directory as ROOT.war. You can make it create an initial user with administrator rights by setting system properties to configure the application.

On a CentOS system you can start Tomcat with the environment variable JAVA_OPTS set to some value or add lines to /etc/sysconfig/tomcat that looks like this:
```
JAVA_OPTS="-Dcas.service=http://datalake.com -Dinitial.username=myname"
JAVA_OPTS="$JAVA_OPTS -Ddb.url=jdbc:h2:tcp://localhost:8043//work/datalakedb -Dstorage.dir=/work -Dupload.dir=/work"
```
These are the properties you can set:
```
db.driver
db.url
db.username
db.password
storage.dir
upload.dir
initial.username # Not needed when integrated with CAS.
initial.password
cas.service
cas.server.host
```
The default values are in src/main/resources/application.properties and src/main/resources/cas.properties.

