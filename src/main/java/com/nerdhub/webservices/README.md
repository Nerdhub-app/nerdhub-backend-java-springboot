# Root package `java`

Documentation of the root package `java`.

## Table of content

- [Main class](#main-class)
- [Clean architecture](#clean-architecture)
- [Feature packages](#feature-packages)
    - [Database tables repartition per feature](#database-tables-repartition-per-feature)
- [Global package](#global-package)

## Main class

The main class `WebServicesApplication` is the class that starts the application.

## Clean architecture

The application is built following the clean architecture. Each package contains sub-packages that represent the 4
layers: `domain`, `application`, `infrastructure`, `presentation`.

## Feature packages

While following the clean architecture, we also package our codebase by feature. Therefore, the `java` package will
contain packages for each feature of the application: `user`, `contact`, etc. Each feature package contains the
`domain`, `application`, `infrastructure` and `presentation` packages.  
Here is an example of the tree view of our packages:

```
WebServicesApplication.java
user/
│-- domain/         # Business rules and entities related to users
│-- architecture/   # Application logic, use cases, and services
│-- infrastructure/ # Database repositories, controllers, external APIs, persistence, and more
│-- presentation/ # REST endpoints
contact/
|-- ... layers
... other features /
```

### Database tables repartition per feature

| Feature | Tables                                                                                                                                  |
|---------|-----------------------------------------------------------------------------------------------------------------------------------------|
| User    | users, user_active_statuses, user_preferences, profile, devices, passkeys, user_subscriptions                                           |
| Plan    | plans, plans_features                                                                                                                   |
| Contact | contacts, contact_messages, contact_chat_preferences, contacts_last_message_visibilities, contact_chat_preferences, ss_contact_messages |
| E2EE    | e2ee_contact_participants, e2ee_pub_onetime_prekeys, e2ee_missing_contact_messages                                                      |

## Global package

In addition to the **feature** packages, we also a `global` package that contains the code for both **global** and *
*common** concerns that are not specifically tied to any feature. This `global` package also follows the clean
architecture so it also contains the following packages as children: `domain`, `application`, `infrastructure` and
`presentation`.  
The Tree view of packages now looks like this:

```
WebServicesApplication.java
global/
│-- domain/
│-- architecture/
│-- infrastructure/
│-- presentation/
user/
│-- domain/         # Business rules and entities related to users
│-- architecture/   # Application logic, use cases, and services
│-- infrastructure/ # Database repositories, controllers, external APIs, persistence, and more
│-- presentation/ # REST endpoints
contact/
|-- ... layers
... other features /
```