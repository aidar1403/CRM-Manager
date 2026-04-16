# CRM Manager System

Group: COMFCI-25
Student Name: [Kalysbekov Aidar]

---

## Description

**CRM Manager** is a console-based Customer Relationship Management application built in Java. It allows users to efficiently manage client records, track interactions, and perform full CRUD (Create, Read, Update, Delete) operations. The system features a user-friendly command-line interface, robust input validation, and file-based data persistence. Data can be exported to and imported from CSV files, ensuring compatibility with external tools like Microsoft Excel.

## Objectives

- Provide a complete CRUD system for managing client information.
- Demonstrate core OOP principles: **Encapsulation**, **Inheritance**, and **Polymorphism**.
- Ensure data persistence across application sessions using file storage.
- Implement a clean, modular architecture separating `model`, `service`, `storage`, and `ui` packages.
- Add CSV **import/export** functionality for data portability.
- Deliver a stable console application with comprehensive error handling and input validation.

##  Project Requirements (10+)

1. **Create Client** – Add a new client with name, email, phone, and company.
2. **Read Clients** – View all clients or find a specific client by ID.
3. **Update Client** – Edit existing client information.
4. **Delete Client** – Remove a client with confirmation prompt.
5. **Add Interaction** – Record a note/interaction linked to a client.
6. **View Interactions** – Display all interactions for a specific client.
7. **Statistics** – Show total clients, total interactions, and average interactions per client.
8. **File Persistence** – Automatically save/load clients using serialization (`clients.dat`).
9. **CSV Export** – Export all client data to a CSV file via menu option.
10. **CSV Import** – Import client data from a CSV file (adds to existing records).
11. **Input Validation** – Validate email format, empty fields, and numeric IDs.
12. **Error Handling** – Graceful handling of IO exceptions, invalid input, and missing files.

##  Algorithms & Data Structures

| Component | Data Structure / Algorithm | Rationale |

| Client Storage (in memory) | `ArrayList<Client>` | Dynamic sizing, efficient iteration for search/display. |
| Client Search by ID | Linear Search (O(n)) | Simple to implement; acceptable for small to medium datasets (<1000 clients). |
| ID Generation | Auto-increment (`nextId`) | Ensures unique, sequential IDs without database. |
| Interaction Storage | `List<String>` inside `Client` | Direct association with client, easy to add/display. |
| CSV Parsing | Line-by-line reading with `BufferedReader` | Memory-efficient for large files; simple `split(",")` for basic format. |
| Data Persistence | Java Serialization (`ObjectOutputStream`) | Built-in mechanism; restores full object state including interactions. |

##  Test Cases & Expected Outputs

| Function | Sample Input | Expected Output |

| **Add Client** | Name: John, Email: john@mail.com | `Client added! ID: 1` |
| **Invalid Email** | Email: wrong-email | ` Invalid email. Please enter a valid email.` |
| **View All** | (after adding) | List of clients with IDs, names, companies |
| **Find by ID** | ID: 1 | Shows full client details + interaction count |
| **Update Client** | New name: Johnny | `Client updated successfully!` |
| **Delete Client** | ID: 1, confirm 'y' | `Client deleted successfully!` |
| **Add Interaction** | Note: "Called about invoice" | `Interaction added successfully!` |
| **Statistics** | (after data) | Total clients, total interactions, average |
| **Export CSV** | Filename: backup | ` Exported 3 clients to data/backup.csv` |
| **Import CSV** | Filename: backup | ` Imported 3 clients from data/backup.csv` |

##  Project Structure

src/com/crm/
├── Main.java # Entry point
├── model/
│ ├── Person.java # Abstract parent class (name, email, phone)
│ └── Client.java # Child class (id, company, interactions)
├── service/
│ └── CRMService.java # CRUD operations, interaction management
├── storage/
│ ├── FileStorage.java # Save/load using serialization (.dat)
│ └── CsvExporter.java # Export to / import from CSV
└── ui/
└── ConsoleUI.java # Menu-driven console interface

## Bonus Features Implemented

 Authentication and User Roles (+10 bonus points)
 
- **Login system** with username/password
- **Admin role** (admin/admin123) – full access (CRUD, import/export)
- **User role** (user/user123) – read-only access (view clients, interactions, statistics)
- Access control implemented in ConsoleUI with role-based menu

##  Database Integration (SQLite) - BONUS +10 points

The project uses **SQLite database** instead of file-based storage:

- **Clients table** – stores all client information (id, name, email, phone, company, created_at)
- **Interactions table** – stores notes linked to clients (client_id, note, created_at)
- Automatic table creation on first run
- Data persistence between sessions
- Foreign key constraint ensures data integrity

##  Authentication & User Roles - BONUS +10 points

| Role | Username | Password | Permissions |
|------|----------|----------|-------------|
| **ADMIN** | `admin` | `admin123` | Full access (CRUD, import/export, interactions) |
| **USER** | `user` | `user123` | Read-only access (view clients, interactions, statistics) |

### Access Control Features:
- Login screen before accessing the system
- Role-based menu display
- Permission checks for all operations
- Logout / Switch User option (menu item 11)

## 🛠️ Technologies Used

- Java 17+
- SQLite (JDBC)
- CSV (import/export)
- Java Serialization (legacy, replaced by SQLite)

##  Database Schema

sql
-- Clients table
CREATE TABLE clients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT,
    company TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

- Interactions table
CREATE TABLE interactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    client_id INTEGER NOT NULL,
    note TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

##  Link to presentation with Screenshots

https://1drv.ms/p/c/3d63479195e1ba1b/IQBKG_bCfZRkRp9nPb1H7kF0AbjHdrpZ5Sw2a9UvsT3yKhc?e=woBzM7
