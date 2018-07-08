# Images service

Simple web based service for images resizing.

## Version
0.0.1-SNAPSHOT

## Prerequisites
* Maven 3.x
* Java 8

## Build
```
mvn clean package
```

## Run
```sh
java -jar target\image-scaler-0.0.1-SNAPSHOT.jar
```

### Usage note:
* URL parameter should be properly encoded.
* Only scales images down.
* Adds padding to fill up the exact required space while original image is positioned in the middle.

## Implementation details
 - Project creates executable jar bundled with all necessary dependencies.
 - Based on Spring boot.
 - No requests caching (was not explicitly requested)

## To Do
1. Add controller tests.
2. Better error handling.

 
