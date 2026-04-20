import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_app/services/auth_service.dart';
import 'package:flutter_app/services/dashboard_service.dart';
import 'package:flutter_app/services/message_service.dart';
import 'package:flutter_app/services/task_service.dart';
import 'package:flutter_app/utils/router.dart';
import 'package:provider/provider.dart';

// Only import Firebase on non-web platforms
import 'firebase_init.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Initialize Firebase only on mobile platforms
  if (!kIsWeb) {
    try {
      await initializeFirebase();
    } catch (e) {
      debugPrint('Firebase initialization error: $e');
    }
  } else {
    debugPrint('Running in demo/web mode - Firebase not initialized');
  }
  
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthService()),
        ChangeNotifierProvider(create: (_) => DashboardService()),
        ChangeNotifierProvider(create: (_) => TaskService()),
        ChangeNotifierProvider(create: (_) => MessageService()),
      ],
      child: MaterialApp(
        title: 'SafeCom',
        theme: ThemeData(
          primarySwatch: Colors.blue,
        ),
        onGenerateRoute: AppRouter.generateRoute,
        initialRoute: AppRouter.loginRoute,
      ),
    );
  }
}
