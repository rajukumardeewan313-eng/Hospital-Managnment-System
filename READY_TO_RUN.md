## Hospital Management System (HMS) - Ready to Run!

**✅ STATUS: FULLY COMPILED AND READY TO USE**

### Quick Start

Simply run one of these commands from the HMS directory:

#### Option 1: Windows Batch File (Easiest)
```batch
run.bat
```

#### Option 2: Windows PowerShell
```powershell
.\run.ps1
```

#### Option 3: Manual Command
```bash
java -cp out App
```

---

### What Has Been Done

✅ **Database Setup**: Uses built-in Java serialization for persistence
- No external database needed
- No driver installation required  
- Data automatically saved to `data/hms.dat`

✅ **Complete Compilation**: All 800+ lines of code compiled successfully
- All Java files compiled to `out/` directory
- Ready to run immediately

✅ **Demo Users Created**: Login with these credentials:

| Username | Password  | Role  |
|----------|-----------|-------|
| admin    | Admin@123 | Admin |
| user     | User@123  | User  |

---

### Features Ready to Use

- 👤 **User Management**: Login/Signup with role-based access
- 👨‍⚕️ **Doctor Management**: Add, update, and search doctors
- 👥 **Patient Management**: Complete patient profiles with history
- 📅 **Appointments**: Book and manage appointments
- 🏥 **Medical Records**: Create and manage patient medical records
- 💰 **Billing System**: Generate bills with itemized charges
- 📊 **Reports Dashboard**: View statistics and analytics
- 🔐 **Secure Passwords**: Hashed password storage with validation

---

### Project Structure

```
HMS/
├── src/                 # All source code (compiles to out/)
│   ├── App.java        # Main entry point
│   └── hms/
│       ├── ui/         # Swing GUI components
│       ├── service/    # Business logic (HospitalSystem, AuthenticationService)
│       ├── model/      # Domain objects (User, Patient, Doctor, etc.)
│       ├── dao/        # Data access objects (optional - uses in-memory)
│       ├── billing/    # Billing system with polymorphism
│       ├── persistence/# DataStore for serialization
│       └── util/       # Utilities (password hashing, validation)
│
├── out/                # Compiled classes (ready to run)
├── data/               # Persistent storage
│   └── hms.dat        # Serialized HospitalSystem object
│
└── run.bat            # Quick run script (Windows)
└── run.ps1            # PowerShell run script
```

---

### How It Works

1. **App.java** starts the application
2. **DataStore.load()** loads the serialized HospitalSystem from `data/hms.dat`
3. **Demo users are created** if the system is empty
4. **LoginFrame** displays the login GUI
5. **After authentication**, **MainFrame** launches the main application

All data is saved automatically to `data/hms.dat` using Java serialization.

---

### System Requirements

- **Java 8 or higher** (Standard JDK)
- **Windows, Linux, or macOS**
- **No external dependencies** - everything is built-in!

---

### Troubleshooting

**If the GUI doesn't appear:**
- The window may have opened behind other windows
- Check the taskbar/dock for the HMS window
- The terminal output confirms the app is running

**To reset all data:**
- Delete `data/hms.dat`
- The system will create a fresh one on next run

**To recompile:**
```bash
cd HMS
javac -d out -sourcepath src $(find src -name "*.java")
```

---

### Ready to Use!

The application is **fully compiled** and **ready to run**. Just execute `run.bat` or use `java -cp out App` to start!
