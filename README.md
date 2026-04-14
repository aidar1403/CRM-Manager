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

