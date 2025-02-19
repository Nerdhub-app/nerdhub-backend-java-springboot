# Global package `global`

This is the package for both **global** and **common** concerns of the application.

## Table of content

- [Explanation](#explanation)
    - [Global](#global)
    - [Common](#common)
- [Domain](#domain)
- [Application](#application)
- [Infrastructure](#infrastructure)
- [Presentation](#presentation)
- [Configurations](#configurations)
- [Folder structure](#folder-structure)

## Explanation

Both **global** and **common** concerns are **not tied to specific features**.

### Global

**Global** concerns are code that are addressed to the application as a whole.  
Here are a few examples of global concerns:

- Configurations for external drivers and APIs (e.g. Database, Message brokers, etc...)
- Client instances for external drivers and APIs (e.g. JDBC client, RabbitMQ client, etc...)
- Spring Security setup: Configuration, Filters, Authentication Endpoints, etc

### Common

**Common** concerns are code that represent **common/shared traits** across the whole application and features. They are
meant to be **reused** everywhere.  
Here are a few examples of common concerns:

- Base parent class for common entity attributes
- Base classes for common use cases exceptions
- Common classes for use cases query inputs

## Domain

Contains both **global** and **common** domain concerns.  
**_N.B._** Global drivers for external APIs are abstracted here as **Gateways** or **Services** interfaces.

## Application

Mostly contains **common** classes for application-related concerns (inputs, exceptions).

## Infrastructure

This is where most of both **global** and **common** lie since it deals with the actual implementations
and the specific drivers used.  
Here are a few examples of what is put here:

- JDBC client
- Neo4J client
- RabbitMQ instance template
- Spring Security configurations

## Presentation

This layer is responsible for handling HTTP requests, controllers, and UI-related logic.  
For our application, **auth**-related **controllers** are put here.

## Configurations

Spring Boot configurations i.e. the registration of **Beans** to the application context related to global concerns is
done at the main class `WebServicesApplication`.

## Folder structure

```
WebServicesApplication.java
global/
│-- domain/
│-- │-- entity/
│-- │-- services/
│-- │-- .../
│-- architecture/
│-- │-- dto
│-- │-- exception
│-- infrastructure/
│-- presentation/
... features
```