## Number Four programming challenge
### Version 0.1, October 2nd, 2013
### By Bruno Unna <bruno.unna@gmail.com>

## Architectural description

The project's architecture is based on the principles of Domain Driven Design. The 
package structure shows a differentiation between the basic (and application) classes, 
the domain-related ones, and those that deal with infrastructural issues.

From the _application_ point of view, the _umbrella_ service is the `AgileService` 
class, which, in turn, depends on the services of the `ProjectService` and 
`TeamService` classes.

In the opposite side of the architectural spectrum, the infrastructural classes are 
responsible of dealing with the back-end services the application requires: 
`Github` and `MongoDB`.

Finally, in the core of the application, there are classes identified as the 
domain of the problem: `Project` (and its manager `ProjectManager`) and `Team` (once 
again, with its manager `TeamManager`).

## Dependencies

This project relies heavily on the spray framework for its inner workings. The 
framework is at its turn based upon [Akka](http://akka.io/). See more details 
at the official page of [Spray](http://spray.io/).

Requirements of the project (all attainable via [Homebrew](http://brew.sh/)):

* jvm (1.6 or superior)
* scala (2.10.2)
* sbt (0.13.0)
* mongodb (tested with 2.4.6)

All other requirements will be automatically obtained as dependencies, 
as declared in the file `build.sbt`:

* spray-can
* spray-routing
* spray-client
* spray-testkit
* spray-json
* akka-actor
* akka-testkit
* specs2
* casbah
* slf4j-simple
* grizzled-slf4j
* org.eclipse.egit.github.core
* junit

## Configuration

The configuration of the application lies in the file `src/main/resources/application.conf`.
The relevant keys there are:

* numberfour.mongodb.host
* numberfour.mongodb.port
* numberfour.mongodb.db-name
* numberfour.github.user
* numberfour.github.token

## Usage

Follow these steps to get started:

1. Unpackage the delivered compressed file (you surely have already done that):

        $ unzip numberFourChallenge.zip

2. Change into the created directory:

        $ cd numberFourChallenge

3. Launch SBT:

        $ sbt

4. Compile everything and run all tests:

        > test

5. Start the application:

        > re-start

6. Browse to the desired endpoint

7. Stop the application:

        > re-stop
