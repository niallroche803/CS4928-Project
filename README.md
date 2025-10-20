# Cafe POS System

A Java-based Point of Sale (POS) system for a cafe, demonstrating various software design patterns and principles.

## Overview

This project implements a comprehensive cafe ordering system that supports product management, order processing, payment handling, and real-time notifications. The system is built using multiple design patterns including Factory, Decorator, Strategy, and Observer patterns.

## Features

- **Product Catalog**: Manage coffee products (Espresso, Latte, Cappuccino)
- **Customizable Orders**: Add extras like shots, milk alternatives, syrups, and size upgrades
- **Flexible Pricing**: Dynamic pricing based on base products and add-ons
- **Multiple Payment Methods**: Cash, Card, and Wallet payment options
- **Order Notifications**: Real-time updates to kitchen, delivery, and customer systems
- **Factory Pattern**: Simplified product creation using recipe strings

## Architecture

The system follows a modular architecture with clear separation of concerns:

```
com.cafepos/
├── catalog/          # Product management and catalog
├── common/           # Shared utilities (Money class)
├── decorator/        # Product decorators for add-ons
├── demo/             # Demo applications
├── domain/           # Core domain objects (Order, LineItem)
├── factory/          # Product factory for recipe parsing
├── observers/        # Observer pattern implementation
└── payment/          # Payment strategy implementations
```

## Design Patterns Used

### 1. Factory Pattern

- **ProductFactory**: Creates products from recipe strings (e.g., "ESP+SHOT+OAT+L")

### 2. Decorator Pattern

- **ProductDecorator**: Base decorator for product enhancements
- **ExtraShot**, **OatMilk**, **Syrup**, **SizeLarge**: Concrete decorators

### 3. Strategy Pattern

- **PaymentStrategy**: Interface for different payment methods
- **CashPayment**, **CardPayment**, **WalletPayment**: Concrete strategies

### 4. Observer Pattern

- **OrderObserver**: Interface for order notifications
- **KitchenDisplay**, **DeliveryDesk**, **CustomerNotifier**: Concrete observers

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Building the Project

```bash
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Running the Demo

```bash
mvn exec:java -Dexec.mainClass="com.cafepos.demo.WeekXDemo"
```

## Usage Examples

### Creating Products with Factory

```java
ProductFactory factory = new ProductFactory();

// Simple espresso
Product espresso = factory.create("ESP");

// Large latte with extra shot and oat milk
Product customLatte = factory.create("LAT+SHOT+OAT+L");
```

### Manual Product Construction

```java
// Equivalent to "ESP+SHOT+OAT+L"
Product product = new SizeLarge(
    new OatMilk(
        new ExtraShot(
            new SimpleProduct("P-ESP", "Espresso", Money.of(2.50))
        )
    )
);
```

### Creating and Processing Orders

```java
Order order = new Order(OrderIds.next());
order.addItem(new LineItem(product, 2));

Money subtotal = order.subtotal();
Money tax = order.taxAtPercent(10);
Money total = order.totalWithTax(10);
```

## Recipe Format

Products can be created using recipe strings with the following format:

- **Base drinks**: ESP (Espresso), LAT (Latte), CAP (Cappuccino)
- **Add-ons**: SHOT (Extra Shot), OAT (Oat Milk), SYP (Syrup), L (Large Size)
- **Format**: `BASE+ADDON1+ADDON2+...`

Examples:

- `"ESP"` - Simple Espresso
- `"LAT+L"` - Large Latte
- `"CAP+SHOT+OAT+SYP"` - Cappuccino with extra shot, oat milk, and syrup

## Testing

The project includes comprehensive unit tests covering:

- Product creation and pricing
- Order management and calculations
- Payment processing
- Observer notifications
- Factory vs manual construction comparison

Run specific test classes:

```bash
mvn test -Dtest=ProductTest
mvn test -Dtest=OrderTest
mvn test -Dtest=FactoryManualComparisonTest
```

## Project Structure

```
project/
├── src/
│   ├── main/java/com/cafepos/
│   │   ├── catalog/           # Product interfaces and implementations
│   │   ├── common/            # Money utility class
│   │   ├── decorator/         # Decorator pattern implementations
│   │   ├── demo/              # Demo applications
│   │   ├── domain/            # Order and LineItem classes
│   │   ├── factory/           # ProductFactory implementation
│   │   ├── observers/         # Observer pattern implementations
│   │   └── payment/           # Payment strategy implementations
│   └── test/java/com/cafepos/ # Unit tests
├── target/                    # Compiled classes and build artifacts
├── pom.xml                    # Maven configuration
└── README.md                  # This file
```

## Recommended Construction Approach

For application developers, the Factory pattern approach should be exposed rather than manual decorator construction. As demonstrated in `FactoryManualComparisonTest.java`, both approaches produce functionally equivalent products with identical names, prices, and order totals. However, the factory method `new ProductFactory().create("ESP+SHOT+OAT+L")` is significantly more readable and maintainable than the nested decorator construction `new SizeLarge(new OatMilk(new ExtraShot(new SimpleProduct(...))))`. The factory approach also reduces the likelihood of developer errors and provides a cleaner API that abstracts away the complexity of the decorator pattern implementation.

## Contributing

This project is part of CS4928 - Theory and Practice of Software Design coursework. It is not open to contribution.

## Design Notes

**Code Smells Removed**: Eliminated Long Parameter Lists in product constructors by implementing Factory pattern; removed Duplicate Code across payment methods through Strategy pattern; addressed Feature Envy in order calculations by encapsulating tax logic within Order class.

**Refactorings Applied**: Extract Method for tax calculations; Replace Constructor with Factory Method for product creation; Replace Conditional with Polymorphism for payment processing; Introduce Parameter Object for Money handling.

**SOLID Principles**: Single Responsibility - each class has one reason to change (Product, Order, Payment strategies); Open/Closed - system open for extension via decorators and strategies, closed for modification; Liskov Substitution - all payment strategies and decorators are interchangeable; Interface Segregation - focused interfaces (PaymentStrategy, OrderObserver); Dependency Inversion - depends on abstractions, not concrete implementations.

**Adding New Discount Types**: Create new discount decorator classes implementing ProductDecorator interface, add discount parsing logic to ProductFactory, and register discount codes in recipe format - no existing classes require modification due to Open/Closed principle compliance.