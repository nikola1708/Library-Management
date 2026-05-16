# Library Management System (Sistem Manajemen Perpustakaan & Sirkulasi)

##  Project Overview

### This is group project

**Library Management System** is a Java-based desktop application designed to manage the operations of a library, including book cataloging, member registration, and book circulation (borrowing and returning). The system provides a comprehensive solution for librarians to manage their collection and track member borrowing activities.

### Purpose

This project serves as a **complete library management solution** that bridges the gap between library administrators and members by providing:
- **Book Inventory Management**: Maintain a catalog of books with detailed information
- **Member Management**: Register and manage library members with borrowing limits
- **Circulation System**: Handle book borrowing and returning transactions
- **Activity Logging**: Track all system activities for audit purposes
- **Admin Dashboard**: Monitor overall library operations with comprehensive data visualization

### Key Objectives
- Automate library operations to reduce manual work
- Maintain accurate book inventory
- Enforce borrowing rules and quotas
- Create an audit trail of all transactions
- Provide easy-to-use interfaces for both members and administrators

---
### Core Technologies
| Component | Technology | Version |
|-----------|-----------|---------|
| **Programming Language** | Java | 8+ |
| **GUI Framework** | Swing (javax.swing) | Built-in |
| **Database** | MySQL | 5.7+ |
| **JDBC Driver** | MySQL Connector/J | 8.0+ |
| **IDE** | IntelliJ IDEA / NetBeans / VS Code | Any |

### Key Libraries & Dependencies
- `javax.swing.*` - GUI components (JFrame, JPanel, JTable, etc.)
- `javax.swing.table.DefaultTableModel` - Table data management
- `java.sql.*` - Database connectivity
- `java.io.*` - File operations (CSV export)
- `java.util.*` - Collection framework

---

##  Project Features

### 1. **Book Catalog Management**
   - View complete list of available books
   - Search books by title, author, or category
   - Categorize books into two types:
     - **Fiction** (with genre: Fantasy, Novel, etc.)
     - **Non-Fiction** (with subject: Programming, Science, etc.)
   - Real-time availability status for each book

### 2. **Member Management**
   - User-friendly member registration
   - Unique identification via email address
   - Customizable borrowing quota (1-10 books)
   - Contact information storage (phone number)
   - Member login system

### 3. **Book Circulation (Borrowing & Returning)**
   - Members can borrow multiple books up to their quota
   - Interactive dropdown menu for selecting books
   - Automatic status update (Available ↔ Borrowed)
   - Return books by title search
   - Prevents overborrowing with quota validation

### 4. **Administrative Dashboard**
   - Add new books to the collection
   - Real-time monitoring of:
     - Activity logs (all transactions)
     - Current loans (global view)
     - Member status and borrowing records
   - Export activity logs to CSV format

### 5. **Activity Logging & Audit Trail**
   - Automatic logging of all actions:
     - Book additions
     - Member registrations
     - Borrowing transactions
     - Return transactions
   - Timestamp for each activity
   - Searchable and exportable logs

### 6. **Multi-Tab Interface**
   - **Tab 1 - Katalog Buku**: Browse and search book catalog
   - **Tab 2 - Area Member**: Member login and borrowing operations
   - **Tab 3 - Admin Dashboard**: Administrative controls and monitoring

---

##  System Architecture

### Architecture Diagram
```
┌─────────────────────────────────────────────────────────────────┐
│                     Presentation Layer (GUI)                     │
│                        Main.java (Swing)                         │
│  ┌──────────────────┬──────────────────────┬─────────────────┐  │
│  │  Tab 1: Katalog  │  Tab 2: Area Member  │  Tab 3: Admin   │  │
│  │     Buku         │    (Pinjam/Kembali)  │    Dashboard    │  │
│  └──────────────────┴──────────────────────┴─────────────────┘  │
└──────────────────────────────────┬──────────────────────────────┘
                                   │
┌──────────────────────────────────▼──────────────────────────────┐
│                    Business Logic Layer                          │
│                   Perpustakaan.java (Core)                       │
│  ┌──────────────┬──────────────┬────────────┬──────────────┐   │
│  │ Book Manager │Member Manager│ Circulation│  Audit Logs  │   │
│  │ (CRUD Buku)  │(CRUD Member) │ (Pinjam)   │ (Logging)    │   │
│  └──────────────┴──────────────┴────────────┴──────────────┘   │
└──────────────────────────────────┬──────────────────────────────┘
                                   │
┌──────────────────────────────────▼──────────────────────────────┐
│                      Data Layer                                  │
│  ┌────────────────────┬──────────────────────────────────────┐  │
│  │ Database.java      │ Model Classes (Buku, Member)        │  │
│  │ (Connection Pool)  │ - Buku.java (Abstract)              │  │
│  │                    │ - BukuFiksi.java (Extends Buku)     │  │
│  │                    │ - BukuNonFiksi.java (Extends Buku)  │  │
│  │                    │ - Member.java                       │  │
│  └────────────────────┴──────────────────────────────────────┘  │
└──────────────────────────────────┬──────────────────────────────┘
                                   │
┌──────────────────────────────────▼──────────────────────────────┐
│                      MySQL Database                             │
│  ┌──────────┬──────────┬───────────┬────────────────────────┐  │
│  │  buku    │  member  │perpustakaan│ (Relational Tables)   │  │
│  └──────────┴──────────┴───────────┴────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```
---

##  Installation & Setup

### Prerequisites
- **Java Development Kit (JDK)**: Version 8 or higher
- **MySQL Server**: Version 5.7 or higher
- **MySQL JDBC Driver**: mysql-connector-java (included in project libraries)

### Step 1: Install MySQL & Create Database

#### 1.1 Install MySQL
```bash
# On Ubuntu/Debian
sudo apt-get install mysql-server

# On macOS
brew install mysql

# On Windows
Download from: https://dev.mysql.com/downloads/mysql/
```

#### 1.2 Create Database & Tables
```bash
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE manajemenperpus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Use the database
USE manajemenperpus;

# Create tables
CREATE TABLE buku (
    id INT PRIMARY KEY AUTO_INCREMENT,
    judul VARCHAR(255) NOT NULL,
    pengarang VARCHAR(100) NOT NULL,
    jenis ENUM('Fiksi', 'NonFiksi') NOT NULL,
    info_khusus VARCHAR(100),
    is_dipinjam BOOLEAN DEFAULT FALSE,
    peminjam_id INT,
    FOREIGN KEY (peminjam_id) REFERENCES member(id) ON DELETE SET NULL
);

CREATE TABLE member (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    no_telepon VARCHAR(15),
    batas_pinjam INT DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE perpustakaan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tanggal TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    aksi VARCHAR(50),
    judul_buku VARCHAR(255),
    nama_member VARCHAR(100),
    keterangan TEXT
);
```

### Step 2: Configure Database Connection

Edit `Database.java` with your MySQL credentials:
```java
public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/manajemenperpus";
    private static final String USER = "root";      // Change if needed
    private static final String PASS = "1234";      // Your MySQL password
    
    // Rest of the code...
}
```

### Step 3: Compile & Run the Project

#### Using Command Line
```bash
# Navigate to project directory
cd /home/mowli/PROJECTS/Library-Management

# Compile all Java files
javac -cp ".:mysql-connector-java-8.0.xx.jar" *.java

# Run the application
java -cp ".:mysql-connector-java-8.0.xx.jar" Main
```

#### Using IDE (IntelliJ IDEA / NetBeans)
1. Open the project
2. Add MySQL JDBC driver to project libraries
3. Configure Database.java with your credentials
4. Click "Run" or press Shift+F10

---

**Project Version**: 1.0  
**Compatibility**: Java 8+, MySQL 5.7+
