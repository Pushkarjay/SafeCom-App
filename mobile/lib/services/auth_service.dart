import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AuthService with ChangeNotifier {
  late final FirebaseAuth _auth;
  final GoogleSignIn _googleSignIn = GoogleSignIn();

  bool _isLoading = false;
  String? _errorMessage;
  User? _user;
  bool _isWebDemo = false;

  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  User? get user => _user;

  AuthService() {
    // Initialize Firebase only on mobile/iOS platforms
    if (!kIsWeb) {
      _auth = FirebaseAuth.instance;
      _auth.authStateChanges().listen((User? user) {
        _user = user;
        notifyListeners();
      });
    } else {
      // Web platform - use demo mode
      _isWebDemo = true;
      debugPrint('🌐 Web Platform Detected - Using Demo Mode');
    }
  }

  void _setLoading(bool loading) {
    _isLoading = loading;
    notifyListeners();
  }

  void _setError(String? message) {
    _errorMessage = message;
    notifyListeners();
  }

  Future<void> login(String email, String password) async {
    _setLoading(true);
    _setError(null);
    try {
      if (kIsWeb) {
        // Web demo mode - simulate login
        await Future.delayed(Duration(seconds: 1));
        debugPrint('✅ Demo Web Login: $email');
      } else {
        // Mobile - Use Firebase
        await _auth.signInWithEmailAndPassword(email: email, password: password);
      }
    } on FirebaseAuthException catch (e) {
      _setError(e.message);
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }

  Future<void> signUp(String email, String password) async {
    _setLoading(true);
    _setError(null);
    try {
      if (kIsWeb) {
        // Web demo mode - simulate signup
        await Future.delayed(Duration(seconds: 1));
        debugPrint('✅ Demo Web Signup: $email');
      } else {
        // Mobile - Use Firebase
        await _auth.createUserWithEmailAndPassword(email: email, password: password);
      }
    } on FirebaseAuthException catch (e) {
      _setError(e.message);
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }


  Future<void> signInWithGoogle() async {
    _setLoading(true);
    _setError(null);
    try {
      if (kIsWeb) {
        // Web demo mode - simulate Google sign-in
        await Future.delayed(Duration(seconds: 1));
        debugPrint('✅ Demo Web Google Sign-In');
      } else {
        // Mobile - Use Firebase + Google Sign-In
        final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
        if (googleUser == null) {
          _setLoading(false);
          return;
        }

        final GoogleSignInAuthentication googleAuth = await googleUser.authentication;
        final AuthCredential credential = GoogleAuthProvider.credential(
          accessToken: googleAuth.accessToken,
          idToken: googleAuth.idToken,
        );

        await _auth.signInWithCredential(credential);
      }
    } on FirebaseAuthException catch (e) {
      _setError(e.message);
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }

  Future<void> signOut() async {
    if (!kIsWeb) {
      await _auth.signOut();
      await _googleSignIn.signOut();
    } else {
      debugPrint('✅ Demo Web Sign Out');
    }
  }
}
