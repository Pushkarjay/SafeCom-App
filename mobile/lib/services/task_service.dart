import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_app/models/task.dart';

class TaskService with ChangeNotifier {
  List<Task> _tasks = [];
  bool _isLoading = false;
  String? _errorMessage;

  List<Task> get tasks => _tasks;
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

  Future<void> fetchTasks(String token) async {
    _setLoading(true);
    _setError(null);
    try {
      // Replace with your actual API endpoint
      final response = await http.get(
        Uri.parse('YOUR_API_ENDPOINT/tasks'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> taskData = json.decode(response.body);
        _tasks = taskData.map((json) => Task.fromJson(json)).toList();
      } else {
        _setError('Failed to load tasks');
      }
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }

  Future<void> createTask(String token, Map<String, dynamic> taskData) async {
    _setLoading(true);
    _setError(null);
    try {
      final response = await http.post(
        Uri.parse('YOUR_API_ENDPOINT/tasks'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
        body: json.encode(taskData),
      );

      if (response.statusCode == 201) {
        await fetchTasks(token); // Refresh the task list
      } else {
        _setError('Failed to create task');
      }
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }

  Future<void> updateTask(String token, String taskId, Map<String, dynamic> taskData) async {
    _setLoading(true);
    _setError(null);
    try {
      final response = await http.put(
        Uri.parse('YOUR_API_ENDPOINT/tasks/$taskId'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
        body: json.encode(taskData),
      );

      if (response.statusCode == 200) {
        await fetchTasks(token); // Refresh the task list
      } else {
        _setError('Failed to update task');
      }
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }

  Future<void> deleteTask(String token, String taskId) async {
    _setLoading(true);
    _setError(null);
    try {
      final response = await http.delete(
        Uri.parse('YOUR_API_ENDPOINT/tasks/$taskId'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        await fetchTasks(token); // Refresh the task list
      } else {
        _setError('Failed to delete task');
      }
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }
}
