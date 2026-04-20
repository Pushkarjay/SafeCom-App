import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';

/// Initialize Firebase for mobile platforms (Android/iOS)
/// This is separated from main.dart to avoid Firebase initialization on web
Future<void> initializeFirebase() async {
  try {
    await Firebase.initializeApp();
    debugPrint('Firebase initialized successfully');
  } catch (e) {
    debugPrint('Firebase initialization failed: $e');
    rethrow;
  }
}
