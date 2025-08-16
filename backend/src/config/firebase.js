const admin = require('firebase-admin');

let firebaseInitialized = false;

// Check if Firebase credentials are properly configured
const hasValidFirebaseConfig = () => {
  return process.env.FIREBASE_PROJECT_ID && 
         (process.env.FIREBASE_PRIVATE_KEY || process.env.FIREBASE_PRIVATE_KEY_BASE64) && 
         process.env.FIREBASE_CLIENT_EMAIL;
};

// Get private key from environment (handle both base64 and direct formats)
const getPrivateKey = () => {
  if (process.env.FIREBASE_PRIVATE_KEY_BASE64) {
    return Buffer.from(process.env.FIREBASE_PRIVATE_KEY_BASE64, 'base64').toString('utf8');
  }
  return process.env.FIREBASE_PRIVATE_KEY?.replace(/\\n/g, '\n');
};

// Initialize Firebase Admin SDK only if credentials are available
try {
  if (hasValidFirebaseConfig() && !admin.apps.length) {
    const serviceAccount = {
      type: "service_account",
      project_id: process.env.FIREBASE_PROJECT_ID,
      private_key_id: process.env.FIREBASE_PRIVATE_KEY_ID,
      private_key: getPrivateKey(),
      client_email: process.env.FIREBASE_CLIENT_EMAIL,
      client_id: process.env.FIREBASE_CLIENT_ID,
      auth_uri: "https://accounts.google.com/o/oauth2/auth",
      token_uri: "https://oauth2.googleapis.com/token",
      auth_provider_x509_cert_url: "https://www.googleapis.com/oauth2/v1/certs",
      client_x509_cert_url: process.env.FIREBASE_CLIENT_X509_CERT_URL
    };

    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      projectId: process.env.FIREBASE_PROJECT_ID,
      storageBucket: `${process.env.FIREBASE_PROJECT_ID}.firebasestorage.app`
    });
    
    firebaseInitialized = true;
    console.log('Firebase Admin SDK initialized successfully');
  } else {
    console.warn('Firebase credentials not configured. Push notifications will be disabled.');
  }
} catch (error) {
  console.error('Failed to initialize Firebase:', error.message);
  console.warn('Continuing without Firebase. Push notifications will be disabled.');
}

// Export Firebase services with safety checks
const firebaseAdmin = {
  messaging: () => {
    if (!firebaseInitialized) {
      throw new Error('Firebase not initialized. Check your credentials.');
    }
    return admin.messaging();
  },
  auth: () => {
    if (!firebaseInitialized) {
      throw new Error('Firebase not initialized. Check your credentials.');
    }
    return admin.auth();
  },
  firestore: () => {
    if (!firebaseInitialized) {
      throw new Error('Firebase not initialized. Check your credentials.');
    }
    return admin.firestore();
  },
  isInitialized: () => firebaseInitialized
};

module.exports = firebaseAdmin;
