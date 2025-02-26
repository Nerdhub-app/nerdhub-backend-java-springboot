# Feature `user`

Documentation for the **User** feature.
> **Important:** The `user` feature is not the only existing feature. Rather, we have multiple features but this README
> only serves as a documentation and a templating guide for all the features.

## Table of content

- [Explanation](#explanation)
- [Domain](#domain)
    - [Entity](#entity)
    - [Value object](#value-object)
    - [Data store](#data-store)
        - [Data store parameters](#data-store-parameters)
    - [Graph store](#graph-store)
    - [Service](#service)
    - [Event](#event)
        - [Publisher](#publisher)
    - [Domain layer configuration](#domain-layer-configuration)
- [Application](#application)
    - [Use case](#use-case)
    - [Input](#input)
    - [Exception](#exception)
    - [Application layer folder structure](#application-layer-folder-structure)
- [Infrastructure](#infrastructure)
    - [JPA Entity](#jpa-entity)
        - [Domain Entity mapper](#domain-entity-mapper)
    - [JPA Repository](#jpa-repository)
    - [Data store implementation](#data-store-implementation)
    - [Neo4J](#neo4j)
        - [Neo4J Node](#neo4j-node)
            - [Domain graph entity mapper](#domain-graph-entity-mapper)
        - [Neo4J Repository](#neo4j-repository)
        - [Graph store implementation](#graph-store-implementation)
    - [MinIO service](#minio-service)
    - [RabbitMQ](#rabbitmq)
        - [Exchange](#exchange)
        - [Producer](#producer)
        - [Consumer](#consumer)
    - [Infrastructure layer folder structure](#infrastructure-layer-folder-structure)
- [Presentation](#presentation)
    - [Controller](#controller)
    - [Data Transfer Object](#data-transfer-object)
        - [Use case input mapper](#use-case-input-mapper)
    - [Request parameter annotation](#request-parameter-annotation)
        - [Request parameter annotation resolver](#request-parameter-annotation-resolver)
    - [Presentation layer folder structure](#presentation-layer-folder-structure)

## Explanation

A **feature** package contains a feature's related concerns. It also follows the clean architecture so it contains the 4
layers packages: `domain`, `application`, `infrastructure`, and `presentation`.  
This README is specifically focused on the `user` feature, but you are also intended to translate it into the structure
of any other feature.
> **N.B.** The name of a feature does not necessarily map to the name of a database table / entity exactly; and a
> feature is not tied to only one entity but rather it encompasses a set of related entities that can be logically
> grouped
> together.

## Domain

The `domain` packages represents the domain layer related to the user feature. It encapsulates the core business logic,
rules, and entities of an application, independent of external concerns like databases or user interfaces.

### Entity

An entity in the domain layer is an object that represents a distinct business concept with a unique identity. In cour
case, a user is represented the `User` class. One entity is mapped to one **database table** in general.

```java
public class User extends CommonUserEntity {
    public String email;
    public String password;
    public OAuthData oauth;
    public UserRole role;
}
```

Folder structure example:

```
user/
├── domain/
├── ├── entity/
├── ├── ├── User.java
├── ├── ├── UserProfile.java
├── ├── ├── UserPreferences.java
```

### Value Object

A value object in the domain layer is an immutable object that represents a descriptive aspect of the domain with no
unique identity, defined only by its attributes. In cour case, a user's OAuth data is represented the `UserOAuthData`
class.

```java
public class OAuthData {
    public String provider;
    public String accountId;
}
```

Folder structure example:

```
user/
├── domain/
├── ├── entity/
├── ├── valueobject/
├── ├── ├── UserOAuthData.java
```

### Data Store

A data store is a **Repository** object that abstracts the CRUD operations to the database specific to one entity. For
example, the data store specific to the user entity is represented by an **interface** called `UserDs`.

```java
public interface UserDs {
    Optional<User> findByUsername(String username);

    void save(User user);
}
```

Folder structure example:

```
user/
├── domain/
├── ├── entity/
├── ├── valueobject/
├── ├── datastore/
├── ├── ├── UserDs.java
├── ├── ├── UserPreferenceDs.java
```

#### Data Store parameters

For most query operations that take in **pagination** and **filter** parameters, we need to define types for those
parameters. That where **data store parameters** come into play. For query operations, the name of the class/record ends
with the `QueryParams`, and in case of mutation operations, it ends with `MutationParams`.  
Here is an example of a filter params class for the **get users** operation on the user data store:

```java
public record GetUsersQueryFiltersParams(UserRole role, String accountType) {
}
```

Current folder structure:

```
user/
├── domain/
├── ├── entity/
├── ├── valueobject/
├── ├── datastore/
├── ├── ├── params/
├── ├── ├── ├── GetUsersQueryFiltersParams.java
├── ├── ├── ├── ... other data store params
├── ├── ├── UserDs.java
├── ├── ├── UserPreferenceDs.java
```

### Graph store

A graph store is essentially the same a data store, with the difference that the underlying database is a **graph**
database that holds relationships between the entities as a graph instead of a traditional row-based database. For our
application, the graph store for the user entity is represented by the `UserGs` interface.

```java
public interface UserGs {
    List<GraphUser> findNonContactUsersWithMostContacts(Long userId);
}
```

Folder structure:

```
user/
├── domain/
├── ├── entity/
├── ├── valueobject/
├── ├── datastore/
├── ├── graphstore/
├── ├── ├── UserGs.java
```

### Service

A service in the domain layer encapsulates core business logic that is not naturally part of an entity or value object.
An example of service is an object storage service that abstract the object storage operations related to a domain.  
In our case, the service for user storage is represented by the **interface** `UserStorageService`.

```java
public interface UserStorageService {
    StoreProfilePictureResult storeProfilePicture(Object picture);
}
```

Folder structure:

```
user/
├── domain/
├── ├── entity/
├── ├── valueobject/
├── ├── datastore/
├── ├── graphstore/
├── ├── service/
├── ├── ├── UserStorageService.java
```

### Event

An event is an immutable object that represents a significant business occurrence within the domain, typically
triggering side effects or state changes in response to entity actions.  
Besides the actual event object, we also define **event publishers** and **event subscribers**. An event publisher is an
abstraction that publishes an event, whereas an event subscriber tells the action to do in response to an event.  
For instance, we can define a **user created** event whenever a user registers to the application. That event can be
described by the class `UserCreatedEvent`.

```java
public class UserCreatedEvent {
    public Long userId;
    public String username;
    public String email;
}
```

#### Publisher

Next, we can define a publisher of that event that is to be invoked somewhere in our use cases. The **user created**
event's publisher is represented by the `UserCreatedEventPublisher` **interface**.

```java
public interface UserCreatedEventPublisher {
    void publish(UserCreatedEvent event);
}
```

The folder structure of that provided example is as follows:

```
user/
├── domain/
├── ├── entity/
├── ├── valueobject/
├── ├── datastore/
├── ├── graphstore/
├── ├── service/
├── ├── event/
├── ├── ├── UserCreatedEvent.java
├── ├── ├── ... other events
├── ├── ├── publisher/
├── ├── ├── ├── UserCreatedEventPublisher.java
├── ├── ├── ├── ... other publishers
```

### Domain layer configuration

The Spring Boot configuration of **Beans** in the Domain layer are done inside the root config class of the feature's
package.  
For the user feature, the domain layer's config class is `UserFeatureConfig`.

The folder structure of that provided example is as follows:

```
user/
├── domain/
├── ├── entity/
├── ├── valueobject/
├── ├── datastore/
├── ├── graphstore/
├── ├── service/
├── ├── event/
├── ├── ├── UserCreatedEvent.java
├── ├── ├── ... other events
├── ├── ├── publisher/
├── ├── ├── ├── UserCreatedEventPublisher.java
├── ├── ├── ├── ... other publishers
├── UserFeatureConfig.java
```

## Application

The `application` packages represents the application layer related to the user feature. It encapsulates the core
business logic, rules, and entities of an application, independent of external concerns like databases or user
interfaces.

### Use case

A use case represents a specific business action or process that an application performs to fulfill a user request,
encapsulating application logic while coordinating interactions between the domain layer and external components.  
Domain components such as **datastore**, **graphstore**, and **service** are injected to a use case to be used during
the execution of the use case.  
A use case implements 2 methods:

- `execute`: The actual method that executes the use case.
- `authorizes`: Authorizes the use case's execution against the authenticated user's id.

Here is an example of use case for updating a user profile:

```java
public class UpdateUserProfileUseCase {
    private UserProfileDs userProfileDs;
    private UserStorageService userStorageService;

    public UpdateUserProfileUseCase(UserProfileDs userProfileDs, UserStorageService userStorageService) {
        this.userProfileDs = userProfileDs;
        this.userStorageService = userStorageService;
    }

    public boolean authorizes(Long authUserId, UserProfile profile) {
        if (profile.userId != authUserId) {
            throw new NotUserProfileOwnerException();
        }
        return true;
    }

    public UserProfile execute(UserProfile profile, UpdateUserProfileInput input) {
        profile.firstName = input.firstName;
        profile.lastName = input.lastName;
        profile.username = input.username;
        profile.profilePicture = this.userStorageService.storeProfilePicture(input.profilePicture).url;
        this.userProfileDs.save(profile);
        return profile;
    }
}
```

### Input

An input object is the argument to be passed to a use case's executor method in order to perform the operation. An input
can either be **query parameters** or **mutation payload** from the request's DTO.  
The following is an example of the input for a use case that gets the contacts.

```java
public class GetUserContactsInput extends CommonPagePaginationInput {
    public ContactStatus status;
}
```

Here is another example of an input for a use case that updates a user preference:

```java
public record UpdateUserPreferenceInput(
        boolean notificationsSound,
        boolean secureStorage,
        DarkMode darkMode) {
}
```

### Exception

Those are custom exceptions classes that are intentionally thrown during the execution of a use case in case of wrong
inputs or unauthorized actions from the authenticated user.  
Ideally, a custom exception should extend one of the common parent exception classes in the `global` package so that
they can be specifically handled by controllers advices in the _infrastructure_ layer.  
Here is an example of exception that could be raised when the authenticated user tries to write to a user profile entity
that is not his own:

```java
public class NotUserProfileOwnerException extends CommonUnauthorizedException {
    public NotUserProfileOwnerException() {
        super("The user profile does not belong to you");
    }
}
```

### Application layer folder structure

With the examples that we have illustrated so far in the application layer, here is what the final folder structure
looks like:

```
user/
├── domain/
├── application/
├── ├── usecase/
├── ├── ├── UpdateUserPreferenceUseCase.java
├── ├── ├── ... other use cases
├── ├── input/
├── ├── ├── UpdateUserPreferenceInput.java
├── ├── ├── ... other inputs
├── ├── exception/
├── ├── ├── NotUserProfileOwnerException.java
├── ├── ├── ... other exceptions
├── UserFeatureConfig.java
```

## Infrastructure

The infrastructure layer in Clean Architecture handles external concerns such as database access, networking, file
systems, and third-party services, serving as a bridge between the application and external systems.  
The infrastructure layer code is under `infrastructure` package.

### JPA Entity

A JPA entity is a class that represents a database table.
In our case, a user is represented the `UserEntity` class. One entity is mapped to one **database table**.  
The JPA entities lie inside the `entity` package.

```java
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
}
```

#### Domain entity mapper

A **JPA Entity** should map to one **Domain Entity**. Therefore, we should a **mapper** that converts a **JPA Entity**
to a **Domain Entity**, and potentially **vice versa**.  
**Domain Entity mappers** lie inside the `mapper`.  
Here is an example of a mapper between a **JPA user entity** and a **domain user entity**:

```java

@Service
public class UserMapper {
    User toDomain(UserEntity userEntity) {
        User user = new User();
        user.setEmail(userEntity.email);
        /* ... more conversions */
        return user;
    }

    UserEntity toJpa(User user) {
        User userEntity = new UserEntity();
        userEntity.setEmail(user.email);
        /* ... more conversions */
        return userEntity;
    }
} 
```

### JPA Repository

A JPA repository is an interface that defines the operations to be performed on a database table.
In our case, a user is represented the `UserRepository` interface. One repository is mapped to one
**database table**.  
The JPA repositories lie inside the `repository` package.

```java
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
```

### Data store implementation

These are the implementations of the data store interface in the domain layer. The data store implementation depends on
the corresponding entity's JPA repository. The name of a data store implementation class is the same as the name of the
data store interface with the `Impl` suffix.  
The data store implementations are put inside the `datastore` package.
Here is an example of a data store implementation:

```java
public class UserDsImpl implements UserDs {
    private final UserJpaRepository userRepository;

    public UserDsImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity save(UserEntity user) {
        return this.userRepository.save(user);
    }
}
```

### Neo4J

We will group all the infrastructure concerns around the **Neo4J** graph database inside the `neo4j` package.

#### Neo4J Node

A Neo4J entity is a class that represents a Node in a Neo4J database.
In our case, a user is represented the `UserNode` class. One node is mapped to one **node label**.  
The Neo4J nodes lie inside the `node` package.

```java
import org.springframework.data.neo4j.core.schema.*;

@Node("User")  // Maps to a "Person" node in Neo4j
public class UserNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type = "FRIEND_WITH", direction = Relationship.Direction.OUTGOING)
    private List<UserNode> friends = new ArrayList<>();

    // Constructors, Getters, and Setters
}
```

##### Domain graph entity mapper

A Neo4J node should correspond to a **graph entity** in the **domain layer**. Therefore, we should define a **mapper**
that converts a **Neo4J node** to a **graph entity**, and potentially **vice versa**.  
Graph entity mappers lie inside the `mapper` package.  
Here is an example of a mapper that converts a `UserNode` to `GraphUser`:

```java

@Service
public class GraphUserMapper {
    GraphUser toEntity(UserNode userNode) {
        GraphUser user = new GraphUser();
        user.setEmail(user.email);
        /* ... more conversions */
        return user;
    }
}
```

#### Neo4J Repository

A Neo4J repository is an interface that defines the operations to be performed specifically on nodes.
In our case, a user is represented the `UserNeo4jRepository` interface. One repository is mapped to one **database label
**.  
The Neo4J repositories lie inside the `repository` package.

```java

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<PersonNode, Long> {
    // Find by name
    List<PersonNode> findByName(String name);

    // Custom Cypher Query
    @Query("MATCH (u:User) WHERE u.name = $name RETURN u")
    UserNode findUserByName(String name);
}
```

#### Graph store implementation

These are the implementations of the graph store interface in the domain layer. The graph store implementation depends
on
the corresponding entity's JPA repository. The name of a graph store implementation class is the same as the name of the
graph store interface with the `Impl` suffix.  
The graph store implementations are put inside the `graphstore` package.  
Here is an example of a graph store implementation:

```java
public class UserGsImpl implements UserGs {
    private final UserNeo4JRepository userRepository;

    public UserGsImpl(UserNeo4JRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserNode save(UserNode user) {
        return this.userRepository.save(user);
    }
}
```

### MinIO service

These are the **storage service** implementations in the domain layer, which use **MinIO** as the actual object storage
infrastructure.  
The storage services using MinIO are put inside the `minio` package.  
Here is an example of the user storage service implementation:

```java
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class UserMinIOService implements UserStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String uploadProfilePicture(MultipartFile file, String userId) throws Exception {
        String fileName = userId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }

        return minioClient.getObjectUrl(bucketName, fileName);
    }
}
```

### RabbitMQ

Here lie the code related to **RabbitMQ**, the **message broker** of our system.  
This concern is put inside the `rabbitmq` package.  
Then, these are the 3 distinct aspects of RabbitMQ that we will be dealing with:

- **Exchanges**
- **Producers**
- **Consumers**

Let's look at each of those in the next subsections.

#### Exchange

An exchange is a central point of communication between **producers** and **consumers**.  
Exchanges lie inside the `exchange` package.  
Here is an example of an exchange for the messages related to a user registration:

```java
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRegistrationExchangeConfig {

    public static final String EXCHANGE_NAME = "userRegistrationExchange";
    public static final String QUEUE_NAME = "user.registered.queue";
    public static final String ROUTING_KEY = "user.registered";

    // 1️⃣ Define Direct Exchange
    @Bean
    public DirectExchange userRegistrationExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // 2️⃣ Define Queue
    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(QUEUE_NAME, true); // durable queue
    }

    // 3️⃣ Bind Queue to Exchange with Routing Key
    @Bean
    public Binding binding(Queue userRegisteredQueue, DirectExchange userRegistrationExchange) {
        return BindingBuilder.bind(userRegisteredQueue).to(userRegistrationExchange).with(ROUTING_KEY);
    }
}
```

#### Producer

A producer is a component that sends **messages** to an exchange.

> **N.B.** In RabbitMQ, a **message** is the data that is sent to an exchange. In our application, a message maps to an
**event** in the _domain layer_. Therefore, when a producer sends a message, it essentially **publishes** an **event**.

With that note, producers are classes that implement the **event publisher** interfaces in the domain layer.  
Here is an example of a producer for the **user created** event:

```java
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationProducer implements UserCreatedEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE_NAME = "userRegistrationExchange";
    private static final String ROUTING_KEY = "user.created";

    public UserRegistrationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publishes a "user created" event to the user registration exchange.
     * @param event The event containing user details.
     */
    @Override
    public void publish(UserCreatedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
        System.out.println("📩 Sent 'user created' event: " + event);
    }
}
```

#### Consumer

A consumer is a component that **receives** **messages** i.e. **events** from an exchange through the **queue** they are
subscribed to.    
It executes code upon the arrival of those events.  
Here is an example of a consumer for the **user registration** exchange:

```java
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationExchangeConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_INFO)
    public void runUserRegistrationAnalytics(UserCreatedEvent event) {
        /* Analytics */
    }

    /* ... other event handlers */
}
```

### Infrastructure layer folder structure

With all those components of the infrastructure layer in place, let's have a high-level look at the folder structure of
the infrastructure layer:

```
user/
├── domain/
├── application/
├── infrastructure/
├── ├── entity/
├── ├── ├── mapper/
├── ├── ├── ├── UserMapper.java
├── ├── ├── ├── ... other domain entity mappers
├── ├── ├── UserEntity.java
├── ├── ├── ... other JPA entities
├── ├── repository/
├── ├── ├── UserJpaRepository.java
├── ├── ├── ... other JPA repositories
├── ├── datastore/
├── ├── ├── UserDsImpl.java
├── ├── ├── ... other datastore implementations
├── ├── neo4j/
├── ├── ├── node/
├── ├── ├── ├── mapper/
├── ├── ├── ├── ├── GraphUserMapper.java
├── ├── ├── ├── ├── ... other domain graph entity mappers
├── ├── ├── ├── UserNode.java
├── ├── ├── ├── ... other Neo4J nodes
├── ├── ├── repository/
├── ├── ├── ├── UserNeo4jRepository.java
├── ├── ├── ├── ... other Neo4J repositories
├── ├── ├── graphstore/
├── ├── ├── ├── UserGsImpl.java
├── ├── ├── ├── ... other graphstore implementations
├── ├── minio/
├── ├── ├── UserMinioService.java
├── ├── ├── ... other MinIO services
├── ├── rabbitmq/
├── ├── ├── exchange/
├── ├── ├── ├── UserRegistrationExchange.java
├── ├── ├── ├── ... other RabbitMQ exchanges
├── ├── ├── producers/
├── ├── ├── ├── UserRegistrationExchangeProducer.java
├── ├── ├── ├── ... other event producers
├── ├── ├── consumers/
├── ├── ├── ├── UserRegistrationExchangeConsumer.java
├── ├── ├── ├── ... other exchange producers
├── UserFeatureConfig.java
```

## Presentation

The Presentation Layer is responsible for handling user interactions and presenting data.  
The presentation layer is represented by the `presentation` layer.

### Controller

A **controller** is a class that handles HTTP requests and responses. It acts as the entry point of the presentation
layer,
processing user input and returning appropriate responses.  
A controller is put inside the `controller` package.  
Here is an example of a `UserController` controller for users-related routes:

```java

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    /**
     * Get the profile of the authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getAuthenticatedUser(@AuthUser User authUser) {
        UserDto userDto = userUseCase.getUserProfile(authUser.getId());
        return ResponseEntity.ok(userDto);
    }

    /**
     * Get another user's profile by user ID.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@RequestUser User user) {
        UserDto userDto = userUseCase.getUserProfile(user.getId());
        return ResponseEntity.ok(userDto);
    }

    /* ... more route handlers */
}
```

### Data Transfer Object

A **data transfer object** (DTO) is a class that represents a data structure that is used to transfer data between
multiple layers of the applications.  
For the sake of distinction in our application, we will only refer to **DTOs** as the objects transferred between the
clients and the presentation layer.  
**DTOs** are put inside the `dto` package.  
Here is an example of a **DTO** for the request of the update of a user preferences:

```java
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserPreferenceRequestDTO(
        @NotNull(message = "Dark mode preference cannot be null")
        Boolean darkMode,

        @NotNull(message = "Language cannot be null")
        @Pattern(regexp = "^(en|fr|es|de|zh|ru)$", message = "Language must be one of: en, fr, es, de, zh, ru")
        String language,

        @NotNull(message = "Notification preference cannot be null")
        Boolean notificationsEnabled
) {
}

```

#### Use case input mapper

Request DTOs are to be passed down to use cases. Therefore, we define **mappers** that convert request DTOs to use case
inputs in order to respect the separation of boundaries between the layers.  
The use case input mappers are put inside the `mapper` package.  
Here is an example of a mapper that converts the **DTO** of a **get users request** to an **input** for the **get users
use case**:

```java

@Service
public class GetUsersInputMapper {
    private final PaginationQueryParamsMapper paginationParamsMapper;

    public GetUsersUseCaseInputMapper(PaginationQueryParamsMapper paginationParamsMapper) {
        this.paginationParamsMapper = paginationParamsMapper;
    }

    public GetUsersInput toInput(GetUsersRequestDTO dto) {
        return new GetUsersInput(
                this.paginationParamsMapper.toQueryParams(dto),
                new GetUsersQueryFilters(dto.ROLE, dto.accountType)
        );
    }
}
```

### Request parameter annotation

Since our webservices is a **REST API**, we will have multiple endpoints that include some **resource id parameter**
which will be then used by **repositories** to fetch the actual resource object.  
Now, in order to prevent the redundancy of importing **repositories** in each controller and including a fetch operation
inside route handlers, we define **custom annotations** that will resolve the actual resource object specified by the
request parameter as a method parameter of the route handler.  
Those custom annotations are put inside the `annotations` package, and it also contains a subpackage called `resolvers`
where we will put the **resolvers** of those annotations.  
Here is an example of a **request parameter annotation** for the **user id**:

```java
package com.example.app.presentation.resolvers;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestUser {
}
```

#### Request parameter annotation resolver

As mentioned before, those request parameters annotations must be resolved to resource objects.
**Resolvers** are the classes that are responsible for those resource objects resolutions by accessing the **request
parameters** and using a **repository** to fetch the actual resource from a database.  
Here is an example of the resolver of the **request parameter annotation** for the **user id**:

```java
package com.example.app.presentation.resolvers;

import com.example.app.domain.Product;
import com.example.app.application.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class RequestUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    public RequestUserArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestProduct.class) &&
                parameter.getParameterType().equals(UserEntity.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String userIdStr = request.getParameter("userId");

        if (userIdStr == null) {
            throw new IllegalArgumentException("Missing required 'userId' parameter");
        }

        Long userId = Long.parseLong(userIdStr);
        return userService.getProductById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
```

### Presentation layer folder structure

With the examples that we have illustrated so far in the presentation layer, here is what the final folder structure
of the presentation layer looks like:

```
user/
├── domain/
├── application/
├── infrastructure/
├── presentation/
├── ├── controller/
├── ├── ├── UserController.java
├── ├── ├── ... other controllers
├── ├── dto/
├── ├── ├── mappers/
├── ├── ├── ├── GetUsersInputMapper.java
├── ├── ├── ├── ... other use case input mappers
├── ├── ├── UpdateUserPreferenceRequestDTO.java
├── ├── ├── ... other DTOs
├── ├── annotations/
├── ├── ├── resolvers/
├── ├── ├── ├── RequestUserResolver.java
├── ├── ├── ├── ... other request parameter id resolvers
├── ├── ├── RequestUser.java
├── ├── ├── ... other request parameter id annotations
├── UserFeatureConfig.java
```