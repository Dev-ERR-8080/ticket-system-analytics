# Hostel Complaint Management System

A full-stack web application for managing hostel complaints with automatic assignment based on complaint categories.

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- Maven

### Frontend
- ReactJS 18
- React Router DOM
- Axios
- CSS3

## Features

- **Complaint Management**: Create, view, and update complaints
- **Automatic Assignment**: Complaints are automatically assigned based on category:
  - CARPENTRY → Ram
  - RAGGING → Shyam
  - ELECTRICAL → Electric Team
  - PLUMBING → Plumber Team
- **File Upload**: Attach files to complaints
- **Status Tracking**: Track complaint status (OPEN, IN_PROGRESS, RESOLVED)
- **User Management**: Simple user creation and selection

## Prerequisites

1. **Java 17** or higher
2. **Maven 3.6+**
3. **Node.js 16+** and npm
4. **PostgreSQL 12+**

## Database Setup

1. Ensure PostgreSQL is installed and running
2. The application will connect to the default `postgres` database with:
   - URL: `jdbc:postgresql://localhost:5432/postgres`
   - Username: `postgres`
   - Password: `postgres`
3. Tables will be created automatically by Hibernate

## Installation & Running

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the Spring Boot application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the React development server:
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## API Endpoints

### User APIs
- `GET /api/users` - Get all users
- `POST /api/users` - Create a new user

### Complaint APIs
- `GET /api/complaints` - Get all complaints
- `GET /api/complaints/{id}` - Get complaint by ID
- `POST /api/complaints` - Create a new complaint (multipart/form-data)
- `PUT /api/complaints/{id}/status` - Update complaint status

## Project Structure

### Backend Structure
```
backend/
├── src/main/java/com/hostel/
│   ├── ComplaintManagementApplication.java
│   ├── config/
│   │   └── WebConfig.java
│   ├── controller/
│   │   ├── ComplaintController.java
│   │   └── UserController.java
│   ├── dto/
│   │   ├── ComplaintDTO.java
│   │   ├── CreateComplaintRequest.java
│   │   ├── UpdateStatusRequest.java
│   │   └── UserDTO.java
│   ├── entity/
│   │   ├── Category.java
│   │   ├── Complaint.java
│   │   ├── Status.java
│   │   └── User.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   ├── repository/
│   │   ├── ComplaintRepository.java
│   │   └── UserRepository.java
│   └── service/
│       ├── ComplaintService.java
│       └── UserService.java
└── src/main/resources/
    └── application.properties
```

### Frontend Structure
```
frontend/
├── public/
│   └── index.html
└── src/
    ├── components/
    │   ├── ComplaintCard.js
    │   ├── ComplaintCard.css
    │   ├── Navbar.js
    │   └── Navbar.css
    ├── pages/
    │   ├── ComplaintDetails.js
    │   ├── ComplaintDetails.css
    │   ├── CreateComplaint.js
    │   ├── CreateComplaint.css
    │   ├── Dashboard.js
    │   └── Dashboard.css
    ├── services/
    │   ├── api.js
    │   ├── complaintService.js
    │   └── userService.js
    ├── App.js
    ├── index.js
    └── index.css
```

## Usage

1. **Create Users**: First, create some users via POST request to `/api/users` or use the API directly:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","role":"Student"}'
```

2. **Create Complaints**: Navigate to "Create Complaint" page, fill in the details, and submit

3. **View Complaints**: The Dashboard displays all complaints in a table format

4. **View Details**: Click on any complaint row to view full details

5. **Update Status**: In the complaint details page, use the dropdown to update the complaint status

## File Uploads

- Uploaded files are stored in the `uploads/` directory (created automatically)
- Files are accessible via `/uploads/{filename}` URL
- Supported file types: images, PDF, DOC, DOCX

## Notes

- No authentication is implemented as per requirements
- CORS is configured to allow requests from `http://localhost:3000`
- File upload size limit is set to 10MB
- Database tables are created automatically using JPA's `ddl-auto=update`

## Troubleshooting

### Backend Issues
- Ensure PostgreSQL is running and accessible
- Check if port 8080 is available
- Verify database credentials in `application.properties`

### Frontend Issues
- Ensure Node.js and npm are installed
- Clear npm cache if dependencies fail: `npm cache clean --force`
- Check if port 3000 is available

### Database Issues
- Check PostgreSQL logs if connection fails
- Ensure the postgres user has proper permissions
- Verify the database URL, username, and password

## License

This project is created for educational purposes.
