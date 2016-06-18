# GeoCluster

GeoCluster is a clustering library for geospatial objects. It is made in Java and provides a clustering interface for complex geometries like polygons. It is also able to annotate the resulting clusters with textual information, providing a context to simplify the process of interpreting them.

It has been developed as a Final Project in Computer Science at the [EINA](https://eina.unizar.es/).


## Info for users

### Using the library
GeoCluster can be used in a Java project as a JAR file. You can import this project to your favorite IDE and then export it to a JAR, or [use the jar command](https://docs.oracle.com/javase/tutorial/deployment/jar/build.html).

## Info for developers

### Building and testing the project
GeoCluster has been built around **gradle**. You can build the project by using:
```
gradle build
```

The project contains a set of unit tests made with JUnit. You can run these tests using:
```
gradle check
```
**Note:** before running these tests, please read the next step. Remember that it can take more than 2 minutes to run all tests.

### MySQL configuration for unit tests
Some unit tests require a database to run. If connection with this database is not available, these tests are just ignored. To replicate the database used for testing, follow the next steps:

* Launch a MySQL server on your machine, port 3306.
* Create a database called `geocluster_1`.
```sql
CREATE DATABASE geocluster_1;
USE geocluster_1;_
```
* Create a user named `geouser`@`localhost` with password `geopass`.
```sql
CREATE USER 'geouser'@'localhost' IDENTIFIED BY 'geopass';
```
* Create the table below in the db just created: 
```sql
CREATE TABLE Features (Id int NOT NULL, Geom geometry NOT NULL, Name varchar(255) NOT NULL, PRIMARY KEY (Id));
```
* Do the following inserts: 
```sql
INSERT INTO Features values (1, ST_GeomFromText('POINT(1 0)'), 'Zgz');
INSERT INTO Features values (2, ST_GeomFromText('POINT(2 1)'), 'Bcn');
```
* Create a user named `geouser`@`localhost` with password `geopass` with grants
```sql
CREATE USER 'geouser'@'localhost' IDENTIFIED BY 'geopass';
GRANT ALL PRIVILEGES ON geocluster_1.* TO 'geouser'@'localhost';
```

## License
The source code of this library is licensed under the GNU General Public License version 3.

## Credits
* Developer: [Javier Beltrán Jorba](https://github.com/MrJavo94)
* Supervisor: [Francisco Javier López Pellicer](https://github.com/fjlopez)
