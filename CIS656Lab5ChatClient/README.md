# gae-restlet-client-example
A simple example Java app that uses the RESTlet framework's client connector to issue HTTP requests to a simple REST api. 

## Setup

Make changes to the URL / HTTP commands in SampleRESTClient.java as you see fit.  

To build the program issue the following maven command

```mvn clean compile assembly:single```

## Running the program

To run the program after building, make sure your server is running and enter the following commands:

```
cd target
java -jar gae-restlet-client-example-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Note that a simple web app that uses the RESTlet framework to implement a simple REST API (and deployable to AppEngine) 
is available [here](https://github.com/jengelsma/gae-restlet-example).  
