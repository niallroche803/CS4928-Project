# Architecture Decision Record: State Pattern vs Enum-based State Management for Order Lifecycle

## Context
The cafe POS system needs to manage order lifecycle states (New, Paid, Preparing, Ready, Delivered, Cancelled) with specific transition rules and state-dependent behavior. Orders must follow a defined workflow where certain actions are only valid in specific states, and invalid transitions should be prevented.

## Decision
We chose to implement the State Pattern with concrete state classes rather than using enum-based state management with conditional logic for order lifecycle management.

## Alternatives Considered

### Option 1: Enum with Switch Statements
- Order status as enum: `OrderStatus.NEW, OrderStatus.PAID, etc.`
- State transitions handled by switch statements in Order class
- Simple implementation with centralized transition logic
- All state behavior concentrated in Order class

### Option 2: Enum with State Machine Library
- External state machine framework managing transitions
- Configuration-driven state definitions and rules
- Powerful but adds external dependency
- Learning curve for team members

### Option 3: State Pattern (Chosen)
- Each state as separate class implementing State interface
- State-specific behavior encapsulated in state classes
- OrderFSM context delegates actions to current state
- States handle their own transitions and validation

## Consequences

### Positive
- **Encapsulation**: Each state encapsulates its own behavior and transition logic
- **Open/Closed Principle**: New states can be added without modifying existing code
- **Single Responsibility**: Each state class has one reason to change
- **Polymorphism**: State-specific behavior handled through method dispatch
- **Maintainability**: State logic is isolated and easier to understand
- **Testability**: Individual states can be unit tested in isolation

### Negative
- **Complexity**: More classes and indirection compared to simple enum approach
- **Object Creation**: State transitions may involve object creation overhead
- **Class Proliferation**: Each state requires its own class file
- **Learning Curve**: Pattern may be unfamiliar to some developers
- **Debugging**: State transitions less obvious in debugger than enum values

## Implementation
The State Pattern is implemented with:
- `State` interface defining common operations (`pay()`, `prepare()`, `markReady()`, etc.)
- Concrete state classes (`NewState`, `PaidState`, `PreparingState`, `ReadyState`, `DeliveredState`, `CancelledState`)
- `OrderFSM` context class managing current state and delegating operations
- Each state handles valid transitions and throws exceptions for invalid ones

States are located in `com.cafepos.state` package with clear separation of concerns between state management and business logic.