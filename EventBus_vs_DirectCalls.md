# Architecture Decision Record: EventBus vs Direct Method Calls for Component Communication

## Context
The cafe POS system needs to notify multiple components (kitchen display, delivery desk, customer notifications) when order events occur. We needed to decide between direct method calls and an event-driven architecture using an EventBus.

## Decision
We chose to implement an EventBus pattern for inter-component communication rather than direct method calls between components.

## Alternatives Considered

### Option 1: Direct Method Calls
- Order class directly calls methods on KitchenDisplay, DeliveryDesk, CustomerNotifier
- Simple and straightforward implementation
- Tight coupling between Order and all notification components

### Option 2: Observer Pattern Only
- Traditional Observer pattern with OrderObserver interface
- Order maintains list of observers and notifies them directly
- Better than direct calls but still couples Order to observer management

### Option 3: EventBus (Chosen)
- Centralized event dispatching system
- Components register for events they care about
- Complete decoupling between event producers and consumers

## Consequences

### Positive
- **Loose Coupling**: Order class doesn't need to know about specific notification components
- **Extensibility**: New notification components can be added without modifying existing code
- **Testability**: Components can be tested in isolation more easily
- **Flexibility**: Events can be handled by multiple components or none at all
- **Single Responsibility**: Order focuses on order management, EventBus handles communication

### Negative
- **Complexity**: Additional abstraction layer adds some complexity
- **Runtime Discovery**: Event flow is less obvious from static code analysis
- **Performance**: Small overhead from event dispatching mechanism
- **Debugging**: Event-driven flow can be harder to trace during debugging

## Implementation
The EventBus is implemented in `com.cafepos.app.events.EventBus` with type-safe event handling. Order events like `OrderCreated` and `OrderPaid` are emitted through the bus and handled by registered components.