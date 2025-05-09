# hmc-hmi-inbound-adapter

[![Build Status](https://travis-ci.org/hmcts/hmc-hmi-inbound-adapter.svg?branch=master)](https://travis-ci.org/hmcts/hmc-hmi-inbound-adapter)

## Getting Started
Please note that this microservice is also available within [ccd-docker](https://github.com/hmcts/ccd-docker).

### Prerequisites

- [JDK 21](https://java.com)
- [Docker](https://www.docker.com)

## Building and deploying the application

### Building the application

The project uses [Gradle](https://gradle.org) as a build tool. It already contains
`./gradlew` wrapper script, so there's no need to install gradle.

To build the project execute the following command:

```bash
  ./gradlew build
```

### Running the application

Create the image of the application by executing the following command:

```bash
  ./gradlew assemble
```

Create docker image:

```bash
  docker-compose build
```

Run the distribution (created in `build/install/hmc-hmi-inbound-adapter` directory)
by executing the following command:

```bash
  docker-compose up
```

This will start the API container exposing the application's port
(set to `4559` in this template app).

In order to test if the application is up, you can call its health endpoint:

```bash
  curl http://localhost:4559/health
```

You should get a response similar to this:

```
  {"status":"UP","diskSpace":{"status":"UP","details":{...}}}
```

### Alternative script to run application

To skip all the setting up and building, just execute the following command:

```bash
./bin/run-in-docker.sh
```

For more information:

```bash
./bin/run-in-docker.sh -h
```

Script includes bare minimum environment variables necessary to start api instance. Whenever any variable is changed or any other script regarding docker image/container build, the suggested way to ensure all is cleaned up properly is by this command:

```bash
docker-compose rm
```

It clears stopped containers correctly. Might consider removing clutter of images too, especially the ones fiddled with:

```bash
docker images

docker image rm <image-id>
```

There is no need to remove postgres and java or similar core images.

## Azure Service Bus & Local Testing

### Azure Service Bus

To enable publishing to an Azure Service Bus destination:

1. Set the Azure Service Bus connection string in the `HMC_SERVICE_BUS_CONNECTION_STRING` environment variable
1. Set the Azure Service Bus queue name in the `HMC_SERVICE_BUS_QUEUE` environment variable
1. Restart the application

## Developing

### Unit tests

To run all unit tests execute the following command:
```bash
  ./gradlew test
```

### Integration tests

To run all integration tests execute the following command:
```bash
  ./gradlew integration
```

### Code quality checks
We use [Checkstyle](http://checkstyle.sourceforge.net/).
To run all local checks execute the following command:

```bash
  ./gradlew check
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
