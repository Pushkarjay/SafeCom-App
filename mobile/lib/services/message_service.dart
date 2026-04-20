import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_app/models/message.dart';

class MessageService with ChangeNotifier {
  List<Message> _messages = [];
  bool _isLoading = false;
  String? _errorMessage;

  List<Message> get messages => _messages;
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

  Future<void> fetchMessages(String token) async {
    _setLoading(true);
    _setError(null);
    try {
      // Replace with your actual API endpoint
      final response = await http.get(
        Uri.parse('YOUR_API_ENDPOINT/messages'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> messageData = json.decode(response.body);
        _messages = messageData.map((json) => Message.fromJson(json)).toList();
      } else {
        _setError('Failed to load messages');
      }
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }

  Future<void> sendMessage(String token, Map<String, dynamic> messageData) async {
    _setLoading(true);
    _setError(null);
    try {
      final response = await http.post(
        Uri.parse('YOUR_API_ENDPOINT/messages'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
        body: json.encode(messageData),
      );

      if (response.statusCode == 201) {
        await fetchMessages(token); // Refresh the message list
      } else {
        _setError('Failed to send message');
      }
    } catch (e) {
      _setError('An unknown error occurred.');
    } finally {
      _setLoading(false);
    }
  }

  Future<void> markAsRead(String token, String messageId) async {
    try {
      await http.patch(
        Uri.parse('YOUR_API_ENDPOINT/messages/$messageId/read'),
        headers: {'Authorization': 'Bearer $token'},
      );
      await fetchMessages(token); // Refresh the message list
    } catch (e) {
      _setError('Failed to mark message as read');
    }
  }
}
