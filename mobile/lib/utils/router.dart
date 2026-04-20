import 'package:flutter/material.dart';
import 'package:flutter_app/screens/add_edit_task_screen.dart';
import 'package:flutter_app/screens/admin_dashboard_screen.dart';
import 'package:flutter_app/screens/customer_dashboard_screen.dart';
import 'package:flutter_app/screens/employee_dashboard_screen.dart';
import 'package:flutter_app/screens/login_screen.dart';
import 'package:flutter_app/screens/messages_screen.dart';
import 'package:flutter_app/screens/signup_screen.dart';
import 'package:flutter_app/screens/task_list_screen.dart';

class AppRouter {
  static const String loginRoute = '/';
  static const String signupRoute = '/signup';
  static const String adminDashboardRoute = '/admin';
  static const String customerDashboardRoute = '/customer';
  static const String employeeDashboardRoute = '/employee';
  static const String taskListRoute = '/tasks';
  static const String addTaskRoute = '/tasks/add';
  static const String messagesRoute = '/messages';

  static Route<dynamic> generateRoute(RouteSettings settings) {
    switch (settings.name) {
      case loginRoute:
        return MaterialPageRoute(builder: (_) => LoginScreen());
      case signupRoute:
        return MaterialPageRoute(builder: (_) => SignupScreen());
      case adminDashboardRoute:
        return MaterialPageRoute(builder: (_) => AdminDashboardScreen());
      case customerDashboardRoute:
        return MaterialPageRoute(builder: (_) => CustomerDashboardScreen());
      case employeeDashboardRoute:
        return MaterialPageRoute(builder: (_) => EmployeeDashboardScreen());
      case taskListRoute:
        return MaterialPageRoute(builder: (_) => TaskListScreen());
      case addTaskRoute:
        return MaterialPageRoute(builder: (_) => AddEditTaskScreen());
      case messagesRoute:
        return MaterialPageRoute(builder: (_) => MessagesScreen());
      default:
        return MaterialPageRoute(
            builder: (_) => Scaffold(
                  body: Center(
                      child: Text('No route defined for ${settings.name}')),
                ));
    }
  }
}
