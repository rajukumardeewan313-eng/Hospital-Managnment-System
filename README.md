# 🏥 Hospital Management System (HMS)

[![Java Version](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![GUI Framework](https://img.shields.io/badge/UI-Swing-blue.svg)](https://en.wikipedia.org/wiki/Swing_(Java))
[![Persistence](https://img.shields.io/badge/Persistence-Serialization-green.svg)](https://docs.oracle.com/javase/8/docs/technotes/guides/serialization/)
[![License](https://img.shields.io/badge/License-MIT-brightgreen.svg)](LICENSE)

A robust, enterprise-grade Java Swing desktop application designed for efficient hospital administration. This project showcases advanced Object-Oriented Programming (OOP) principles through a vibrant, user-friendly interface.

---

## 🚀 Key Features

| Module | Functionality |
| :--- | :--- |
| **📊 Dashboard** | Real-time system stats, unpaid bills monitoring, and doctor schedules. |
| **👥 Patient Management** | Comprehensive CRUD operations with search by Name or ID. |
| **👨‍⚕️ Doctor Directory** | Manage medical staff profiles, specializations, and consultation fees. |
| **📅 Appointment Booking** | Smart booking system with **conflict detection** and status tracking. |
| **📝 Medical Records** | Detailed diagnosis and prescription tracking linked to appointments. |
| **💳 Billing & Invoicing** | Automated bill generation with support for lab tests, medicine, and consultation fees. |

---

## 🛠 Tech Stack

- **Language:** Java (JDK 8 or higher)
- **GUI Library:** Java Swing & AWT
- **Data Storage:** Java Object Serialization (Current) / SQL Support (Planned)
- **Design Pattern:** Layered Architecture / DAO Pattern

---

## 🏛 OOP Implementation

This project is a practical demonstration of the four pillars of OOP:

1.  **Encapsulation:** All domain models (Patient, Doctor, etc.) use private fields with strict validation in setters.
2.  **Abstraction:** Uses an `abstract class Person` and a `BillItem` interface to hide implementation details.
3.  **Inheritance:** `Patient` and `Doctor` hierarchies extend the base `Person` class, promoting code reuse.
4.  **Polymorphism:** Dynamic method dispatch is used in billing calculations and object display info.

---

## ⚙️ Installation & Running

### Prerequisites
- Java Development Kit (JDK) 8 or 17+ installed.

### Quick Start (Windows)
Double-click the `run.bat` file in the root directory, or use PowerShell:
```powershell
.\run.ps1
```

### Manual Execution
If you prefer the terminal:
```bash
# Compile
javac -d out -sourcepath src src/App.java

# Run
java -cp out App
```

---

## 📂 Project Structure

```text
src/
├── hms/
│   ├── model/         # Domain Entities (OOP Models)
│   ├── billing/       # Invoicing & Pricing Logic
│   ├── service/       # Business Logic (HospitalSystem engine)
│   ├── persistence/   # Data Save/Load Operations
│   └── ui/            # Swing Frames and Panels
└── App.java           # Main Entry Point
```

---

## 👤 Credentials (Demo)

- **Admin Username:** `admin` | **Password:** `Admin@123`
- **User Username:** `user` | **Password:** `User@123`

---

## 📝 License

Distributed under the MIT License. See `LICENSE` for more information.

---
**Developed with ❤️ by the HMS Team**
