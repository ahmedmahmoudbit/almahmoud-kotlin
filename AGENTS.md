# Islamic App Rebuild Rules

## Project Goal

This project is a complete rewrite of an existing Islamic Android application.

Source project path:

`/Users/mac/Downloads/Al-mahmoud-2.0.0`

The old Java project is the source of truth for:

* Data
* Features
* Business Logic
* Assets
* Navigation Flow

The application must be rebuilt from scratch using modern Android development practices.

---

# Technology Stack

## Mandatory

* Kotlin
* Jetpack Compose
* Material 3
* Coroutines
* Flow
* Navigation Compose
* Hilt Dependency Injection
* Room Database (if local storage is required)
* DataStore instead of SharedPreferences
* Coil for images

## Forbidden

* Java code
* XML layouts
* findViewById
* AsyncTask
* LiveData (prefer StateFlow)
* Global mutable states
* Business logic inside Composables

---

# Architecture

Use strict Clean Architecture.

Structure:

app/
├── core/
├── data/
├── domain/
├── presentation/
├── di/

Every feature must contain:

feature/
├── data/
│ ├── datasource/
│ ├── repository/
│
├── domain/
│ ├── model/
│ ├── repository/
│ ├── usecase/
│
├── presentation/
│ ├── screen/
│ ├── components/
│ ├── state/
│ ├── viewmodel/

Rules:

* UI never talks directly to repositories.
* UI only talks to ViewModel.
* ViewModel only talks to UseCases.
* UseCases talk to repositories.
* Repositories talk to data sources.

Never violate this flow.

---

# Design Patterns

Use appropriate design patterns.

Preferred patterns:

* Repository Pattern
* Singleton Pattern
* Factory Pattern
* Strategy Pattern
* State Holder Pattern
* Dependency Injection Pattern

Avoid unnecessary patterns.

Choose the simplest solution.

---

# Singleton Rules

Singletons must be used carefully.

Allowed:

* DataStore manager
* Database instance
* Repository instances
* Network manager
* Preferences manager

Forbidden:

* UI state
* Screen state
* Compose state

Never create multiple instances when one instance is enough.

---

# Compose Rules

Use Compose best practices.

Mandatory:

* Stateless composables when possible
* State hoisting
* Reusable composables
* Small composable functions

Forbidden:

* Huge composables
* Repeated UI code
* Business logic inside composables

A composable should have one responsibility.

---

# Shared Components

Create reusable components.

Examples:

core/ui/components/

* AppButton
* AppTextField
* AppToolbar
* AppCard
* LoadingView
* ErrorView
* EmptyView
* AppDialog
* AppBottomSheet
* AppScaffold

Before creating a new component:

Check if an existing component can be reused.

Avoid duplicated UI.

---

# Performance Rules

Performance is critical.

Avoid:

* Unnecessary recompositions
* Repeated calculations
* Heavy work inside composables

Use:

* remember
* rememberSaveable
* derivedStateOf
* immutable models

Only when necessary.

Never optimize blindly.

Measure first.

---

# Side Effects

Handle side effects carefully.

Use:

* LaunchedEffect
* DisposableEffect
* SideEffect

Only when required.

Rules:

* Never trigger API calls repeatedly.
* Never place unstable objects in effect keys.
* Keep effect scopes small.

Prevent unnecessary recompositions.

---

# State Management

Use:

* StateFlow
* MutableStateFlow

Expose immutable state.

Example:

private val _state

val state

Never expose mutable state publicly.

---

# Theming

Must support:

* Light Mode
* Dark Mode

Requirements:

* Dynamic colors when available
* Centralized theme configuration
* No hardcoded colors

All colors must come from theme definitions.

---

# Localization

Languages:

* Arabic
* English

Requirements:

* All strings inside resources
* No hardcoded text
* RTL support
* LTR support

Every screen must work correctly in both directions.

---

# Navigation

Use Navigation Compose.

Rules:

* Typed routes when possible
* No hardcoded route duplication
* Centralized navigation definitions

---

# Animations

Application should feel modern.

Use:

* AnimatedVisibility
* Crossfade
* AnimatedContent
* animateFloatAsState
* animateDpAsState

Animation goals:

* Smooth
* Lightweight
* Meaningful

Avoid excessive animations.

---

# Data Migration

When rebuilding a feature:

1. Analyze original Java implementation.
2. Understand business rules.
3. Preserve functionality.
4. Refactor architecture.
5. Improve code quality.

Do not blindly copy old code.

---

# Code Quality

Mandatory:

* DRY (Don't Repeat Yourself)
* SOLID Principles
* KISS Principle
* Clean Code

Forbidden:

* Code duplication
* Magic numbers
* Hardcoded values
* Long functions

Function target:

Maximum 40 lines.

Split when needed.

---

# Naming Rules

Classes:

PrayerRepository

Functions:

loadPrayerTimes()

Variables:

prayerTimes

Constants:

MAX_RETRY_COUNT

Use meaningful names.

---

# Error Handling

Never ignore errors.

Always:

* Catch exceptions
* Return Result types
* Log failures

Avoid:

* Empty catch blocks
* Silent failures

---

# ViewModel Rules

ViewModels must:

* Contain screen logic
* Call use cases
* Manage UI state

ViewModels must not:

* Access UI directly
* Access Android Views
* Access repositories directly

---

# Dependency Injection

Use Hilt.

Requirements:

* Constructor injection first
* Module injection only when necessary

Avoid service locator patterns.

---

# Documentation

Complex business logic should contain concise comments.

Do not comment obvious code.

Code should explain itself.

---

# AI Instructions

When generating code:

When generating code:

1. Read the existing project structure first.
2. Reuse existing components whenever possible.
3. Avoid creating duplicate functionality.
4. Follow Clean Architecture strictly.
5. Always prefer maintainable solutions to shortcuts.
6. Always prefer reusable code to quick fixes.
7. Preserve application performance.
8. Preserve RTL support.
9. Preserve Dark Mode compatibility.
10. Generate production-ready code only.
11. 
Never generate temporary, demo, or placeholder implementations unless explicitly requested.
