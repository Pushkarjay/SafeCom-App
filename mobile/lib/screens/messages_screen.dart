import 'package:flutter/material.dart';
import 'package:flutter_app/models/message.dart';
import 'package:flutter_app/services/auth_service.dart';
import 'package:flutter_app/services/message_service.dart';
import 'package:provider/provider.dart';

class MessagesScreen extends StatefulWidget {
  @override
  _MessagesScreenState createState() => _MessagesScreenState();
}

class _MessagesScreenState extends State<MessagesScreen> {
  final _messageController = TextEditingController();
  final _receiverController = TextEditingController();

  @override
  void initState() {
    super.initState();
    final authService = Provider.of<AuthService>(context, listen: false);
    final messageService = Provider.of<MessageService>(context, listen: false);
    if (authService.user != null) {
      authService.user!.getIdToken().then((token) {
        if (token != null && token.isNotEmpty) {
          messageService.fetchMessages(token);
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final messageService = Provider.of<MessageService>(context);
    final authService = Provider.of<AuthService>(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('Messages'),
      ),
      body: Column(
        children: [
          Expanded(
            child: messageService.isLoading
                ? Center(child: CircularProgressIndicator())
                : messageService.errorMessage != null
                    ? Center(child: Text(messageService.errorMessage!))
                    : ListView.builder(
                        itemCount: messageService.messages.length,
                        itemBuilder: (context, index) {
                          final message = messageService.messages[index];
                          final isCurrentUser = message.senderId == authService.user?.uid;
                          
                          return Container(
                            margin: EdgeInsets.symmetric(vertical: 4, horizontal: 8),
                            child: Row(
                              mainAxisAlignment: isCurrentUser 
                                  ? MainAxisAlignment.end 
                                  : MainAxisAlignment.start,
                              children: [
                                Container(
                                  constraints: BoxConstraints(
                                    maxWidth: MediaQuery.of(context).size.width * 0.7,
                                  ),
                                  padding: EdgeInsets.all(12),
                                  decoration: BoxDecoration(
                                    color: isCurrentUser 
                                        ? Colors.blue[100] 
                                        : Colors.grey[200],
                                    borderRadius: BorderRadius.circular(12),
                                  ),
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      if (!isCurrentUser)
                                        Text(
                                          message.senderName,
                                          style: TextStyle(
                                            fontWeight: FontWeight.bold,
                                            fontSize: 12,
                                          ),
                                        ),
                                      Text(message.content),
                                      SizedBox(height: 4),
                                      Text(
                                        '${message.timestamp.hour}:${message.timestamp.minute.toString().padLeft(2, '0')}',
                                        style: TextStyle(
                                          fontSize: 10,
                                          color: Colors.grey[600],
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
          ),
          Container(
            padding: EdgeInsets.all(8),
            child: Column(
              children: [
                TextField(
                  controller: _receiverController,
                  decoration: InputDecoration(
                    labelText: 'To (User ID)',
                    border: OutlineInputBorder(),
                  ),
                ),
                SizedBox(height: 8),
                Row(
                  children: [
                    Expanded(
                      child: TextField(
                        controller: _messageController,
                        decoration: InputDecoration(
                          hintText: 'Type a message...',
                          border: OutlineInputBorder(),
                        ),
                        maxLines: null,
                      ),
                    ),
                    SizedBox(width: 8),
                    messageService.isLoading
                        ? CircularProgressIndicator()
                        : IconButton(
                            onPressed: () => _sendMessage(messageService, authService),
                            icon: Icon(Icons.send),
                          ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void _sendMessage(MessageService messageService, AuthService authService) async {
    if (_messageController.text.trim().isEmpty || _receiverController.text.trim().isEmpty) {
      return;
    }

    if (authService.user != null) {
      final token = await authService.user!.getIdToken();
      if (token != null && token.isNotEmpty) {
        final messageData = {
          'receiverId': _receiverController.text.trim(),
          'content': _messageController.text.trim(),
          'senderId': authService.user!.uid,
          'senderName': authService.user!.displayName ?? 'Unknown',
        };

        await messageService.sendMessage(token, messageData);
        
        if (messageService.errorMessage == null) {
          _messageController.clear();
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(messageService.errorMessage!)),
          );
        }
      }
    }
  }
}
