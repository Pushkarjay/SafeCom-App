import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_app/models/dashboard_data.dart';

class DashboardService with ChangeNotifier {
  DashboardData? _dashboardData;
  bool _isLoading = false;
  String? _errorMessage;

  DashboardData? get dashboardData => _dashboardData;
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;

  void _setLoading(bool loading) {
    _isLoading = loading;
    notifyListeners();
  }

  void _setError(String? message) {
    _errorMessage = message;
    notifyListeners();
  }

  Future<void> fetchDashboardData(String token) async {
    _setLoading(true);
    _setError(null);
    try {
      // Replace with your actual API endpoint
      final response = await http.get(
        Uri.parse('YOUR_API_ENDPOINT/dashboard'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        _dashboardData = DashboardData.fromJson(json.decode(response.body));
      } else {
        _setError('Failed to load dashboard data');
      }
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }
}
