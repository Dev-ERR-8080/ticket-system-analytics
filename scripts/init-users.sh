#!/bin/bash

# Script to initialize sample users for testing

echo "Creating sample users..."

# Create Student users
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","role":"Student"}' \
  && echo " ✓ Created John Doe (Student)"

curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Smith","role":"Student"}' \
  && echo " ✓ Created Jane Smith (Student)"

curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Johnson","role":"Student"}' \
  && echo " ✓ Created Alice Johnson (Student)"

# Create Admin user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin User","role":"Admin"}' \
  && echo " ✓ Created Admin User (Admin)"

# Create Warden user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Hostel Warden","role":"Warden"}' \
  && echo " ✓ Created Hostel Warden (Warden)"

echo ""
echo "Sample users created successfully!"
echo "You can now create complaints using these users."
