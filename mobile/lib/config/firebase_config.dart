// Firebase Configuration for SafeCom Flutter App
// This file contains the Firebase configuration for the SafeCom project
// Replace these values with your actual Firebase project configuration

class FirebaseConfig {
  static const String projectId = 'safecom-task-management';
  static const String projectNumber = '344799045102';
  static const String webApiKey = 'AIzaSyCxoXVuxbQxiNMkJnXyF1PKpXmLoEo-cMU';
  static const String appId = '1:344799045102:android:0a07e2d233032e0286daff';
  static const String packageName = 'com.safecom.taskmanagement';
  
  // Firebase options for Android
  static const androidOptions = {
    'projectId': projectId,
    'appId': appId,
    'apiKey': webApiKey,
    'messagingSenderId': projectNumber,
  };
  
  // Firebase options for iOS (when you add iOS app to Firebase)
  static const iosOptions = {
    'projectId': projectId,
    'appId': 'YOUR_IOS_APP_ID', // Add this when you create iOS app in Firebase
    'apiKey': webApiKey,
    'messagingSenderId': projectNumber,
    'iosBundleId': 'com.safecom.taskmanagement',
  };
  
  // Firebase options for Web
  static const webOptions = {
    'projectId': projectId,
    'appId': 'YOUR_WEB_APP_ID', // Add this when you create Web app in Firebase
    'apiKey': webApiKey,
    'messagingSenderId': projectNumber,
    'authDomain': '$projectId.firebaseapp.com',
    'storageBucket': '$projectId.appspot.com',
  };
}
