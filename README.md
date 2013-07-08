# ampache-top50-rest

A Clojure project for a REST interface to provide song play statistics for an Ampache server (for example, top 10 tracks of the last week or top 100 albums of all time)

## Installation

leiningen

    [com.madeye.clojure.ampache/ampache-top100 "0.1.0"]

maven

    <dependency>
      <groupId>com.madeye.clojure.ampache</groupId>
      <artifactId>ampache-top100</artifactId>
      <version>0.1.0</version>
    </dependency>

## Usage

ampache-top50-rest requires configuration supplied in a Java-style properties file containing the following items:

    # Database connection parameters
    # Database host
    database-host=localhost
    # Database port
    database-port=3306
    # Database name
    database-db=ampache
    # Database username
    database-user=ampache
    # Database passwrd
    database-password=ampache


    # Default number of items to return in "top n" queries
    default-top=50

    # Should full URL be used in links?
    links.use-full-urls=true

    # Base URL for full URLs
    links.base-url=http://localhost:8080

    # Port to expose server on
    server-port=8080

ampache-top50-rest can then be run as any Leiningen application via lein:

    $ lein run conf/config.properties

or as an uberjar:

    $ lein uberjar
    $ java -jar target/ampache-top50-0.1.0-standalone.jar

## License

Copyright © 2013 Madeye Software Ltd.

Distributed under the Eclipse Public License, the same as Clojure.
