# Calories Calculator

## Project Description

**Calories Calculator** is a client-server application designed to calculate daily calorie intake and macronutrients based on user data, goals, and selected food items.

The project follows a classic client–server architecture and consists of three main parts:
- Android application (Kotlin) — user interface
- Web API (C#, ASP.NET) — server-side logic and database access
- SQL Server — persistent data storage

This separation allows the system to be scalable, maintainable, and easy to extend.

---

## Features

### Android Application
- User profile loading;
- Food search using an external API;
- Adding food items to the current ration with custom weight;
- Automatic calculation of:
  - calories
  - protein
  - fat
  - carbohydrates;
- Visual progress tracking using progress bars;
- Support for different modes:
  - weight loss
  - muscle gain;
- Saving daily rations;
- Loading saved rations;
- Deleting saved rations.

### Backend (ASP.NET Web API)
- REST API for communication with the mobile client;
- Endpoints for:
  - creating saved rations;
  - retrieving a list of saved rations;
  - loading a ration by ID;
  - deleting a ration;
- Transaction-based database operations;
- Input validation and error handling;
- Proper JSON serialization and deserialization.

### Database (SQL Server)
- Storage of user data;
- Storage of saved rations;
- Storage of food items inside rations;
- One-to-many relationships between rations and food items.

---

## Architecture Overview

Android App (Kotlin)
|
| HTTP / JSON
v
ASP.NET Web API (C#)
|
| SQL
v
SQL Server Database


This architecture:
- separates responsibilities between client and server;
- keeps database access on the backend only;
- follows standard client–server design principles.

---

## Repository Structure

Recommended repository layout:

CaloriesCalculator/
│
├── android-app/ # Android project (Kotlin)
│
├── backend-api/ # ASP.NET Web API project
│
├── database/
│ ├── schema.sql # Database structure (CREATE TABLE scripts)
│ ├── seed.sql # Test data (optional)
│ └── README.md
│
├── docs/
│ └── screenshots/ # Project screenshots
│
└── README.md


---

## Database Setup

The database structure is defined using SQL scripts (`schema.sql`) that contain `CREATE TABLE` statements.

### How to export database structure from SQL Server

Using **SQL Server Management Studio (SSMS)**:

1. Open SSMS and connect to the database
2. Expand `Databases → YourDatabase → Tables`
3. Right-click a table
4. Select  
   **Script Table as → CREATE To → New Query Editor Window**
5. Save the generated script into `schema.sql`

This approach avoids manually recreating the database structure.

---

## Documentation

- Screenshots of the application and API usage are stored in the `docs` folder
- This README focuses on explaining the architecture and logic of the project

---

## Notes

- This project was developed as a university assignment with an emphasis on architecture and data integrity.
- All calculations and data persistence are handled on the backend.
- The project can be easily extended with additional features such as daily history or analytics.

---

## Technologies Used

- **Android**: Kotlin, RecyclerView, Material Design
- **Backend**: C#, ASP.NET Web API
- **Database**: SQL Server
- **Networking**: REST, JSON
- **Tools**: Android Studio, Visual Studio, SQL Server Management Studio
