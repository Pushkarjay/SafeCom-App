require('dotenv').config();
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const User = require('./src/models/User');

const seedDatabase = async () => {
  try {
    // Connect to MongoDB
    const mongoUri = process.env.MONGODB_URI || 'mongodb://localhost:27017/safecom';
    console.log(`🔄 Connecting to MongoDB: ${mongoUri.replace(/:[^:]*@/, ':****@')}`);
    
    await mongoose.connect(mongoUri, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
      connectTimeoutMS: 10000,
      serverSelectionTimeoutMS: 5000
    });
    console.log('✅ Connected to MongoDB\n');

    // Test user credentials
    const testUsers = [
      {
        name: 'Demo Admin',
        email: 'admin@safecom.test',
        password: 'Demo@1234',
        role: 'Admin',
        phoneNumber: '+1234567890',
        department: 'Management'
      },
      {
        name: 'Demo Customer',
        email: 'customer@safecom.test',
        password: 'Demo@1234',
        role: 'Employee',
        phoneNumber: '+1111111111',
        department: 'Customer Service'
      },
      {
        name: 'Demo Employee',
        email: 'employee@safecom.test',
        password: 'Demo@1234',
        role: 'Employee',
        phoneNumber: '+0987654321',
        department: 'Operations'
      },
      {
        name: 'Demo Manager',
        email: 'manager@safecom.test',
        password: 'Demo@1234',
        role: 'Manager',
        phoneNumber: '+1122334455',
        department: 'Human Resources'
      }
    ];

    // Remove existing test users
    await User.deleteMany({
      email: { $in: ['admin@safecom.test', 'customer@safecom.test', 'employee@safecom.test', 'manager@safecom.test'] }
    });
    console.log('🗑️  Cleared existing test users\n');

    // Create new test users
    for (const userData of testUsers) {
      const user = new User(userData);
      await user.save();
      console.log(`✅ Created: ${userData.name} (${userData.email})`);
    }

    displayTestCredentials();

    await mongoose.connection.close();
    console.log('✅ Database seeding complete - connection closed');
    process.exit(0);

  } catch (error) {
    console.error('❌ Database connection failed:', error.message);
    console.log('\n⚠️  Could not connect to MongoDB, but here are your test credentials:\n');
    
    // Display credentials even if DB connection fails
    displayTestCredentials();
    
    console.log('\n📚 Setup Instructions:');
    console.log('─────────────────────────────────────────────────');
    console.log('1. Ensure MongoDB is running locally or accessible');
    console.log('2. Update MONGODB_URI in server/.env if needed:');
    console.log('   - Local: mongodb://localhost:27017/safecom');
    console.log('   - Atlas: mongodb+srv://user:pass@cluster.mongodb.net/safecom');
    console.log('3. Run: npm run seed');
    console.log('─────────────────────────────────────────────────\n');
    
    process.exit(1);
  }
};

function displayTestCredentials() {
  console.log('\n📋 Test User Credentials:');
  console.log('═══════════════════════════════════════════════════');
  console.log('Admin Account:');
  console.log('  Email:    admin@safecom.test');
  console.log('  Password: Demo@1234');
  console.log('  Role:     Administrator\n');
  console.log('Customer Account:');
  console.log('  Email:    customer@safecom.test');
  console.log('  Password: Demo@1234');
  console.log('  Role:     Customer\n');
  console.log('Employee Account:');
  console.log('  Email:    employee@safecom.test');
  console.log('  Password: Demo@1234');
  console.log('  Role:     Employee\n');
  console.log('Manager Account:');
  console.log('  Email:    manager@safecom.test');
  console.log('  Password: Demo@1234');
  console.log('  Role:     Manager');
  console.log('═══════════════════════════════════════════════════\n');
}

seedDatabase();
