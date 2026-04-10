# PivotaConnect Android App

**A Unified Pan-African Listings Platform for Life Opportunities — Serving Formal AND Informal Economies**

[Download on Google Play](https://play.google.com/store/apps/details?id=com.pivotaconnect.app) | [Documentation](https://docs.pivotaconnect.com/mobile) | [API Reference](https://api.pivotaconnect.com)

---

## Screenshots

| Onboarding | Home Screen | Job Listings |
|------------|-------------|--------------|
| ![Onboarding](your-onboarding-image-link) | ![Home](your-home-image-link) | ![Jobs](your-jobs-image-link) |

| Property Detail | Professional Booking | M-PESA Payment |
|-----------------|---------------------|----------------|
| ![Property](your-property-image-link) | ![Booking](your-booking-image-link) | ![Payment](your-payment-image-link) |

| Profile Screen | Chat Screen | Trust Score |
|---------------|-------------|-------------|
| ![Profile](your-profile-image-link) | ![Chat](your-chat-image-link) | ![Trust](your-trust-image-link) |

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Architecture](#architecture)
4. [Technology Stack](#technology-stack)
5. [Project Structure](#project-structure)
6. [Prerequisites](#prerequisites)
7. [Getting Started](#getting-started)
8. [Environment Configuration](#environment-configuration)
9. [Build Variants](#build-variants)
10. [Dependencies](#dependencies)
11. [Authentication Flow](#authentication-flow)
12. [Data Layer](#data-layer)
13. [UI Layer](#ui-layer)
14. [Offline Support](#offline-support)
15. [Push Notifications](#push-notifications)
16. [Localization](#localization)
17. [Accessibility](#accessibility)
18. [Testing](#testing)
19. [Performance Optimization](#performance-optimization)
20. [Security](#security)
21. [Analytics & Monitoring](#analytics--monitoring)
22. [Building for Production](#building-for-production)
23. [Release Process](#release-process)
24. [Contributing](#contributing)
25. [License](#license)

---

## Project Overview

PivotaConnect Android app is a native mobile client for the PivotaConnect platform, providing a seamless, intuitive interface for Africans to access employment, housing, and social support opportunities. The app is built entirely with Jetpack Compose, following modern Android development best practices and Material Design 3 guidelines.

### Core Mission

Democratize access to opportunities for every African—from the formal professional to the informal worker, from the corporate employer to the individual landlord—through a mobile-first experience optimized for the African market.

### Key Differentiators

| Feature | PivotaConnect | Other Platforms |
|---------|---------------|-----------------|
| Offline-first support | Yes | No |
| M-PESA integration | Native | Limited |
| Swahili language support | Full | None |
| Low bandwidth mode | Yes | No |
| Voice input for listings | Yes | No |
| SMS fallback for OTP | Yes | No |
| Data saver mode | Yes | Rare |
| Informal worker profiles | Yes | No |

---

## Features

### Pillar 1: Employment

- Browse formal and informal job listings
- Apply with CV (formal) or portfolio (informal)
- Save jobs for offline viewing
- Job alerts based on preferences
- Application tracking
- Employer messaging
- Skill assessment tests
- Interview scheduling

### Pillar 2: Housing

- Search rentals and properties
- Filter by location, price, bedrooms, amenities
- Save favorite properties
- Contact landlords directly
- Schedule property viewings
- Tenant application submission
- Lease agreement signing (digital)
- Rent payment via M-PESA

### Pillar 3: Social Support

- Browse aid programs and grants
- Apply for food, cash, health assistance
- NGO directory
- Emergency assistance requests
- Community mutual aid
- Donation to verified causes
- Volunteer opportunities

### Professional Services

- Book verified professionals (electricians, plumbers, movers, etc.)
- Real-time availability checking
- In-app chat with professionals
- Service rating and reviews
- Payment via escrow
- Service history

### Trust & Safety

- ID verification
- Trust score display
- Escrow-protected transactions
- Dispute filing
- Emergency contact sharing
- SOS button

### Platform Features

- Multi-language (English, Swahili)
- Offline mode
- Biometric authentication (fingerprint/face)
- Dark mode
- Accessibility support
- Data saver mode
- Voice search
- Share listings via WhatsApp

---

## Architecture

### Clean Architecture with MVVM

The app follows Clean Architecture principles with MVVM (Model-View-ViewModel) pattern, separating concerns into three layers:

**Presentation Layer (UI)**
- Activities and Composables
- ViewModels (AndroidX Lifecycle)
- UI state management
- Event handling
- Navigation (Compose Navigation)

**Domain Layer (Business Logic)**
- Use cases / Interactors
- Business rules
- Repository interfaces
- Entity models

**Data Layer (Data Management)**
- Repositories (implementation)
- Local data sources (Room)
- Remote data sources (Retrofit)
- Cache management
- Data mappers

### Architecture Diagram

[Insert Architecture Diagram Here]

The diagram should show:
- UI Layer (Composables -> ViewModels)
- Domain Layer (Use Cases -> Repository Interfaces)
- Data Layer (Repository Impl -> Local/Remote Sources)
- External dependencies (Room, Retrofit, Datastore, WorkManager)

### Dependency Direction
