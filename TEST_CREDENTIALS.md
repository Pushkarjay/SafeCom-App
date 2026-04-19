# SafeCom Test User Credentials

## Test Accounts for Development & Testing

The following test user accounts have been seeded into the database. Use these credentials to test the SafeCom application across different user roles.

### ✅ Admin Account
```
Email:    admin@safecom.test
Password: Demo@1234
Role:     Administrator
Department: Management
```

### ✅ Employee Account
```
Email:    employee@safecom.test
Password: Demo@1234
Role:     Employee
Department: Operations
```

### ✅ Manager Account
```
Email:    manager@safecom.test
Password: Demo@1234
Role:     Manager
Department: Human Resources
```

---

## How to Seed Test Users

### Prerequisites
- MongoDB running locally or accessible via connection string in `.env`
- Node.js and npm installed
- Dependencies installed: `npm install` in the `server/` directory

### Run the Seed Script

```bash
cd server
npm run seed
```

This will:
1. Connect to MongoDB using the URI from `.env`
2. Remove any existing test users with the above emails
3. Create fresh test user accounts with hashed passwords
4. Display confirmation of created users

### Expected Output
```
✅ Connected to MongoDB
🗑️  Cleared existing test users
✅ Created: Demo Admin (admin@safecom.test)
✅ Created: Demo Employee (employee@safecom.test)
✅ Created: Demo Manager (manager@safecom.test)

📋 Test User Credentials:
═══════════════════════════════════════════
Admin Account:
  Email:    admin@safecom.test
  Password: Demo@1234
  Role:     Administrator
...
✅ Database seeding complete - connection closed
```

---

## Testing the Application

### Web Application
1. Navigate to `file:///path/to/SafeCom-App/web/login.html`
2. Select role from dropdown: "Administrator", "Customer", or "Employee"
3. Enter test credentials (email and password from above)
4. Click "Log In"

### Mobile App (Flutter on Web)
1. Run: `cd mobile && flutter run -d chrome --web-port 54703`
2. Click "Enter as Demo User" button for quick demo mode
3. Or enter test credentials manually

---

## Database Setup

### Local MongoDB
If using local MongoDB:
```bash
# Update .env to use local connection:
MONGODB_URI=mongodb://localhost:27017/safecom

# Ensure MongoDB is running:
# Windows: mongod (from MongoDB installation)
# macOS: brew services start mongodb-community
# Linux: sudo systemctl start mongod
```

### MongoDB Atlas (Cloud)
If using MongoDB Atlas, ensure:
1. Your IP address is whitelisted in Atlas network access
2. Database user credentials are correct in `.env`
3. Connection string format matches your cluster setup

---

## Notes

- All test passwords are set to `Demo@1234` for easy testing
- Passwords are hashed using bcryptjs before storage
- Test users can be recreated anytime by running `npm run seed`
- Do not use these credentials in production environments
- Change JWT_SECRET in `.env` for production deployments

---

## Troubleshooting

### Connection Error: "querySrv ENOTFOUND"
- Check MongoDB connection string in `.env`
- Verify MongoDB service is running
- Ensure IP is whitelisted (for Atlas)

### "Email already registered" Error
- Run `npm run seed` again to recreate users
- Or manually delete test users from MongoDB and reseed

### Password validation errors
- Ensure password meets minimum requirements (8+ chars)
- Test password follows: letters, numbers, special characters
