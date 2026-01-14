# Library Management System (Sistem Manajemen Perpustakaan & Sirkulasi)

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Project Features](#project-features)
4. [System Architecture](#system-architecture)
5. [Database Schema](#database-schema)
6. [Installation & Setup](#installation--setup)
7. [Usage Guide](#usage-guide)
8. [Code Structure](#code-structure)
9. [API Documentation](#api-documentation)
10. [Troubleshooting](#troubleshooting)

---

## 🎯 Project Overview

### What is This Project?

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

## 🛠️ Technology Stack

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

## ✨ Project Features

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

## 🏗️ System Architecture

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

### Design Patterns Used
1. **MVC (Model-View-Controller)**: Separation of concerns
2. **DAO (Data Access Object)**: Database operations in Perpustakaan.java
3. **Object-Oriented Programming**: Inheritance (Buku parent class)
4. **Singleton Pattern**: Database connection management

---

## 📊 Database Schema

### Database Name: `manajemenperpus`

#### Table 1: `buku` (Books)
```sql
CREATE TABLE buku (
    id INT PRIMARY KEY AUTO_INCREMENT,
    judul VARCHAR(255) NOT NULL,
    pengarang VARCHAR(100) NOT NULL,
    jenis ENUM('Fiksi', 'NonFiksi') NOT NULL,
    info_khusus VARCHAR(100),          -- Genre (for Fiksi) or Subject (for NonFiksi)
    is_dipinjam BOOLEAN DEFAULT FALSE,
    peminjam_id INT,                   -- Foreign Key to member.id
    FOREIGN KEY (peminjam_id) REFERENCES member(id) ON DELETE SET NULL
);
```

**Columns:**
- `id`: Unique book identifier (auto-increment)
- `judul`: Book title
- `pengarang`: Author name
- `jenis`: Book type (Fiction or Non-Fiction)
- `info_khusus`: Genre (Fiction) or Subject (Non-Fiction)
- `is_dipinjam`: Boolean status (0 = available, 1 = borrowed)
- `peminjam_id`: ID of member currently borrowing the book

---

#### Table 2: `member` (Library Members)
```sql
CREATE TABLE member (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,         -- Unique identifier
    no_telepon VARCHAR(15),
    batas_pinjam INT DEFAULT 3,        -- Borrowing quota
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Columns:**
- `id`: Unique member identifier (auto-increment)
- `nama`: Member's full name
- `email`: Email address (unique constraint)
- `no_telepon`: Phone number
- `batas_pinjam`: Maximum number of books allowed to borrow simultaneously
- `created_at`: Registration timestamp

---

#### Table 3: `perpustakaan` (Activity Log)
```sql
CREATE TABLE perpustakaan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tanggal TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    aksi VARCHAR(50),                  -- Action type (TAMBAH_BUKU, PINJAM, KEMBALI, etc.)
    judul_buku VARCHAR(255),           -- Book title involved
    nama_member VARCHAR(100),          -- Member involved (NULL for admin actions)
    keterangan TEXT                    -- Additional details
);
```

**Columns:**
- `id`: Log entry identifier
- `tanggal`: Timestamp of action
- `aksi`: Type of action performed
- `judul_buku`: Title of book involved in transaction
- `nama_member`: Name of member involved
- `keterangan`: Additional information about the action

---

## 🚀 Installation & Setup

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

## 📖 Usage Guide

### For Library Members

#### Registration & Login
1. Open the application and navigate to **"Area Member"** tab
2. Enter your name in the text field
3. Click **"Masuk / Daftar"** button
4. If you're a new member, fill the registration form:
   - Nama Lengkap (Full Name)
   - Email (must be unique)
   - No. Telepon (Phone Number, min 10 digits)
   - Batas Pinjam (Borrowing quota: 1-10 books)
5. Click **"DAFTAR"** to complete registration

#### Borrowing a Book
1. After login, status shows green with your name and email
2. Click **"PINJAM BUKU"** button
3. Enter book title or keyword in the dialog
4. Select the desired book from the dropdown list
5. Confirm to borrow
6. Your borrowed books appear in the list below

#### Returning a Book
1. Click **"KEMBALIKAN BUKU"** button
2. Enter the exact title (or partial match) of the book you're returning
3. System confirms successful return

#### Browsing the Catalog
1. Navigate to **"Katalog Buku"** tab
2. View all available books in the table
3. Use **"Cari Judul"** (Search) button with keywords
4. Click **"Refresh Full"** to see all books again
5. Check status column to see if book is available or borrowed

---

### For Librarians/Administrators

#### Adding New Books
1. Navigate to **"Admin Dashboard"** tab
2. In the left panel, fill the form:
   - Judul Buku (Book Title)
   - Pengarang (Author Name)
   - Tipe Buku (Fiction or Non-Fiction)
   - Genre / Subjek (Genre for Fiction, Subject for Non-Fiction)
3. Click **"Simpan Buku"** to add to collection
4. Book appears immediately in catalog

#### Monitoring Activities

**Activity Log Tab:**
- View all system transactions in real-time
- Columns show: Waktu (Time), Aksi (Action), Member, Info
- Shows last 100 activities

**Sedang Dipinjam (Global) Tab:**
- See all books currently borrowed by any member
- Columns: Judul Buku, Pengarang, Peminjam (Borrower)
- Useful for inventory tracking

**Daftar Member Tab:**
- View all registered members
- Columns: ID, Nama Member, Batas Pinjam, Status Pinjam
- Shows current loan count per member

#### Exporting Logs
1. Click **"Export Log ke CSV"** button
2. Choose location to save file
3. File is saved as `log_aktivitas.csv`
4. Can be opened with Excel or any spreadsheet application
5. Contains: Waktu, Aksi, Member, Info columns

---

## 💻 Code Structure

### File Organization

```
Library-Management/
├── Main.java              # GUI Application (Swing-based UI)
├── Perpustakaan.java      # Core business logic
├── Buku.java              # Book model and hierarchy
├── Member.java            # Member model
├── Database.java          # Database connection manager
└── README.md              # Documentation
```

### Class Descriptions

#### 1. **Main.java** (GUI Layer)
**Purpose**: Create and manage the graphical user interface
**Key Components**:
- `JTabbedPane`: Three-tab interface
- `JTable`: Display data (books, members, logs)
- `JPanel`: Organize UI sections
- `JButton`: Action triggers
- `JTextField`, `JTextArea`: User inputs

**Main Methods**:
| Method | Purpose |
|--------|---------|
| `Main()` | Constructor - builds entire UI |
| `refreshAllData()` | Refresh all data displays |
| `refreshTabelBuku()` | Update book catalog table |
| `refreshListPinjamanMember()` | Update personal loans list |
| `refreshAdminTables()` | Update admin monitoring tables |
| `bukaFormRegistrasi()` | Member registration dialog |
| `escapeCsv()` | Helper for CSV export |

---

#### 2. **Perpustakaan.java** (Business Logic Layer)
**Purpose**: Core operations and database interactions

**Main Methods by Category**:

**Book Management**:
```java
tambahBuku(Buku b)           // Add new book to collection
getSemuaBuku()               // Retrieve all books
cariBukuByJudul(String)      // Search books by title
```

**Member Management**:
```java
daftarAnggota(...)           // Register new member
getAnggota(String nama)      // Get member by name
getAnggotaByEmail(String)    // Get member by email
tampilkanSemuaAnggota()      // Display all members
```

**Circulation**:
```java
pinjamBuku(String, Buku)     // Process book borrowing
kembalikanBuku(String, String) // Process book return
getBukuDipinjam(String)      // Get books borrowed by member
tampilkanSemuaBukuDipinjam() // Display all active loans
```

**Logging & Monitoring**:
```java
catatLog(...)                // Record activity
tampilkanRiwayatLog()        // Display activity log
getLogData()                 // Get logs for GUI display
getGlobalLoanData()          // Get all current loans
getMemberData()              // Get member information
```

---

#### 3. **Buku.java** (Model Layer)
**Purpose**: Represent books with polymorphic types

**Class Hierarchy**:
```
Buku (Abstract Class)
├── BukuFiksi (Fiction)
│   └── Properties: genre
└── BukuNonFiksi (Non-Fiction)
    └── Properties: subjek (subject)
```

**Key Methods**:
```java
abstract String getKategori()      // Get category (overridden by subclasses)
abstract String getInfoKhusus()    // Get genre or subject
getJudul(), getPengarang()         // Getters
isDipinjam(), setDipinjam()        // Status management
```

**Why Inheritance?**
- Different book types have different attributes
- Single `kategori` method works polymorphically
- Cleaner code than numerous if-else checks

---

#### 4. **Member.java** (Model Layer)
**Purpose**: Represent library members

**Key Properties**:
| Property | Type | Purpose |
|----------|------|---------|
| `id` | int | Database identifier |
| `nama` | String | Member's name |
| `email` | String | Unique identifier |
| `noTelepon` | String | Contact information |
| `batasPinjam` | int | Borrowing quota |
| `daftarDipinjam` | List | Currently borrowed books |

**Key Methods**:
```java
getters/setters                    // Access properties
tambahPinjamanLokal(Buku)          // Add to borrowed list
setDaftarDipinjam(List)            // Update borrowed list from DB
```

---

#### 5. **Database.java** (Data Access Layer)
**Purpose**: Manage database connections

**Key Features**:
- Static method `getConnection()` for all database access
- Loads MySQL JDBC driver
- Handles connection errors
- Singleton pattern approach (single connection pool)

**Configuration**:
```java
URL = "jdbc:mysql://localhost:3306/manajemenperpus"
USER = "root"
PASS = "1234"  // Customize with your password
```

---

## 📚 API Documentation

### Core Methods (Perpustakaan.java)

#### Book Operations

**1. Add Book to Collection**
```java
public void tambahBuku(Buku b)
```
- **Parameters**: `Buku b` - Book object (BukuFiksi or BukuNonFiksi)
- **Returns**: void
- **Behavior**: Inserts book into DB, logs action
- **Validation**: Checks for duplicate title+author

**Example**:
```java
lib.tambahBuku(new BukuFiksi("Harry Potter", "J.K. Rowling", "Fantasy"));
lib.tambahBuku(new BukuNonFiksi("Clean Code", "Robert Martin", "Programming"));
```

---

**2. Retrieve All Books**
```java
public List<Buku> getSemuaBuku()
```
- **Parameters**: None
- **Returns**: `List<Buku>` - All books in collection
- **SQL Query**: SELECT * FROM buku

---

**3. Search Books by Title**
```java
public List<Buku> cariBukuByJudul(String keyword)
```
- **Parameters**: `String keyword` - Search term
- **Returns**: `List<Buku>` - Matching books
- **Search Type**: Case-insensitive partial match
- **SQL Query**: LIKE operator on title

**Example**:
```java
List<Buku> results = lib.cariBukuByJudul("potter");
// Returns books with "potter" in title
```

---

#### Member Operations

**1. Register New Member**
```java
public Member daftarAnggota(String nama, String email, String noTelepon, int batasPinjam)
```
- **Parameters**:
  - `nama`: Member's full name
  - `email`: Unique email address (primary identifier)
  - `noTelepon`: Phone number (min 10 characters)
  - `batasPinjam`: Maximum books to borrow (1-10)
- **Returns**: `Member` object or null if failed
- **Validation**: Email uniqueness, field validation
- **Auto**: Logs registration action

**Example**:
```java
Member newMember = lib.daftarAnggota("John Doe", "john@email.com", "0812345678", 5);
```

---

**2. Get Member by Name**
```java
public Member getAnggota(String nama)
```
- **Parameters**: `String nama` - Member's name
- **Returns**: `Member` object with borrowed books list
- **Note**: Case-insensitive search; returns first match

---

**3. Get Member by Email**
```java
public Member getAnggotaByEmail(String email)
```
- **Parameters**: `String email` - Email address
- **Returns**: `Member` object (unique match)
- **Advantage**: Email is unique identifier

---

#### Circulation Operations

**1. Borrow Book**
```java
public String pinjamBuku(String namaMember, Buku bukuDipilih)
```
- **Parameters**:
  - `namaMember`: Name of borrowing member
  - `bukuDipilih`: Book object to borrow
- **Returns**: String message (success or error)
- **Validation**:
  - Member exists
  - Book available (not already borrowed)
  - Quota not exceeded
- **Actions**:
  - Updates `is_dipinjam` and `peminjam_id` in DB
  - Logs transaction
  - Updates member's borrowed list

**Return Messages**:
```
"Berhasil meminjam: [Book Title]"
"Member belum terdaftar."
"Buku sedang dipinjam orang lain."
"Gagal: batas maksimum pinjaman tercapai."
```

---

**2. Return Book**
```java
public String kembalikanBuku(String namaMember, String judulKeyword)
```
- **Parameters**:
  - `namaMember`: Name of returning member
  - `judulKeyword`: Book title or partial title
- **Returns**: String message (success or error)
- **Search**: Case-insensitive substring match
- **Actions**:
  - Sets `is_dipinjam = 0` and `peminjam_id = NULL`
  - Logs return action
  - Updates member's list

---

**3. Get Books Borrowed by Member**
```java
public List<Buku> getBukuDipinjam(String namaMember)
```
- **Parameters**: `String namaMember` - Member's name
- **Returns**: `List<Buku>` - Currently borrowed books
- **SQL**: JOIN query on buku and member tables

---

#### Logging & Monitoring

**1. Get Activity Logs**
```java
public Object[][] getLogData()
```
- **Returns**: 2D array suitable for JTable
- **Columns**: [Waktu, Aksi, Member, Info]
- **Limit**: Last 100 entries

---

**2. Get All Active Loans (Global)**
```java
public Object[][] getGlobalLoanData()
```
- **Returns**: 2D array of all borrowed books
- **Columns**: [Judul Buku, Pengarang, Peminjam]

---

**3. Get Member List**
```java
public Object[][] getMemberData()
```
- **Returns**: 2D array of all members
- **Columns**: [ID, Nama Member, Batas Pinjam, Status Pinjam]

---

## 🔍 Troubleshooting

### Common Issues & Solutions

#### 1. **Database Connection Error**
**Error Message**: `Koneksi Error: Cannot load driver class com.mysql.cj.jdbc.Driver`

**Causes & Solutions**:
```
1. MySQL JDBC driver not in classpath
   → Download mysql-connector-java-x.x.xx.jar
   → Add to project libraries

2. MySQL server not running
   → Start MySQL: sudo service mysql start (Linux)
   → Or: brew services start mysql (macOS)

3. Wrong credentials in Database.java
   → Check username and password
   → Verify database name is correct

4. Database doesn't exist
   → Run: CREATE DATABASE manajemenperpus;
```

---

#### 2. **"Member tidak ditemukan" After Registration**
**Cause**: Registration succeeded but member lookup failed

**Solution**:
```java
// In Main.java, after registration
// Use getAnggotaByEmail() instead of getAnggota()
currentMember = lib.getAnggotaByEmail(emailJustRegistered);
```

---

#### 3. **CSV Export Shows Garbled Characters**
**Cause**: Encoding issue with special characters

**Current Solution**: UTF-8 encoding is already configured
```java
new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)
```

---

#### 4. **"Buku sedang dipinjam orang lain" for Available Book**
**Cause**: Stale data - UI not refreshed

**Solution**:
```java
// Refresh after every operation
refreshAllData();  // Reloads from database
```

---

#### 5. **Table Shows No Data After Adding Book/Member**
**Cause**: Data layer updated but UI not refreshed

**Solution**:
- Manually click "Refresh Full" or "Refresh" button
- Or system should auto-refresh via `refreshAllData()`

---

#### 6. **Member Can Borrow Same Book Twice**
**Cause**: No constraint preventing duplicate borrowing

**Current Behavior**: Correct - one person can only borrow one copy
**If Issue Occurs**: Check database for duplicate entries

---

#### 7. **Permission Denied When Creating CSV File**
**Cause**: No write permission to selected directory

**Solution**:
- Choose a directory with write permissions
- Typically: home folder, Documents, Desktop, Downloads
- Avoid Windows system directories

---

### Database Debugging Tips

**Check MySQL Connection**:
```bash
# Test connection
mysql -u root -p manajemenperpus -e "SHOW TABLES;"

# Should output:
# | Tables_in_manajemenperpus |
# | buku                      |
# | member                    |
# | perpustakaan              |
```

**Verify Data Integrity**:
```sql
-- Check books
SELECT COUNT(*) as total_buku FROM buku;

-- Check members
SELECT COUNT(*) as total_member FROM member;

-- Check active loans
SELECT COUNT(*) as pinjaman_aktif FROM buku WHERE is_dipinjam = 1;

-- View recent logs
SELECT * FROM perpustakaan ORDER BY tanggal DESC LIMIT 10;
```

**Fix Orphaned Loans** (book borrowed but peminjam_id invalid):
```sql
-- Find orphaned loans
SELECT * FROM buku WHERE is_dipinjam = 1 AND peminjam_id NOT IN (SELECT id FROM member);

-- Fix orphaned loans
UPDATE buku SET is_dipinjam = 0, peminjam_id = NULL 
WHERE peminjam_id NOT IN (SELECT id FROM member);
```

---

## 🎓 Educational Notes

### Object-Oriented Principles Used

1. **Inheritance**: Buku → BukuFiksi, BukuNonFiksi
2. **Polymorphism**: `getKategori()` overridden in subclasses
3. **Encapsulation**: Private fields with public getters/setters
4. **Abstraction**: Abstract `Buku` class forces implementation

### Design Patterns

1. **MVC Pattern**: Separation of GUI, Logic, Data
2. **DAO Pattern**: All DB ops in Perpustakaan.java
3. **Singleton**: Database connection (static methods)

### Best Practices Demonstrated

- SQL Prepared Statements (prevents SQL injection)
- Try-with-resources (proper resource management)
- Proper error handling (catch SQLException)
- Clear method naming and documentation
- Separation of concerns (each class has single responsibility)

---

## 📝 Summary

This **Library Management System** is a comprehensive solution for managing library operations combining:
- ✅ Modern Java GUI with Swing
- ✅ Robust MySQL database with proper schema
- ✅ Complete CRUD operations
- ✅ Real-time data monitoring
- ✅ Audit logging and export capabilities
- ✅ User-friendly interfaces for both members and admins

The project demonstrates professional software engineering practices suitable for academic projects and small-to-medium library operations.

---

## 📞 Support & Maintenance

For issues or improvements:
1. Check the Troubleshooting section
2. Verify database integrity
3. Review application logs via Admin Dashboard
4. Check Java console for error messages

**Last Updated**: January 2026  
**Project Version**: 1.0  
**Compatibility**: Java 8+, MySQL 5.7+
