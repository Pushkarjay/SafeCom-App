import 'package:flutter/material.dart';
import 'package:flutter_app/screens/add_edit_task_screen.dart';
import 'package:flutter_app/services/auth_service.dart';
import 'package:flutter_app/services/task_service.dart';
import 'package:provider/provider.dart';

class TaskListScreen extends StatefulWidget {
  @override
  _TaskListScreenState createState() => _TaskListScreenState();
}

class _TaskListScreenState extends State<TaskListScreen> {
  @override
  void initState() {
    super.initState();
    final authService = Provider.of<AuthService>(context, listen: false);
    final taskService = Provider.of<TaskService>(context, listen: false);
    if (authService.user != null) {
      authService.user!.getIdToken().then((token) {
        if (token != null && token.isNotEmpty) {
          taskService.fetchTasks(token);
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final taskService = Provider.of<TaskService>(context);
    final authService = Provider.of<AuthService>(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('Tasks'),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.of(context).push(
            MaterialPageRoute(builder: (context) => AddEditTaskScreen()),
          );
        },
        child: Icon(Icons.add),
      ),
      body: taskService.isLoading
          ? Center(child: CircularProgressIndicator())
          : taskService.errorMessage != null
              ? Center(child: Text(taskService.errorMessage!))
              : ListView.builder(
                  itemCount: taskService.tasks.length,
                  itemBuilder: (context, index) {
                    final task = taskService.tasks[index];
                    return Card(
                      margin: EdgeInsets.all(8.0),
                      child: ListTile(
                        title: Text(task.title),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(task.description),
                            Text('Assigned to: ${task.assignedTo}'),
                            Text('Due: ${task.dueDate.toLocal().toString().split(' ')[0]}'),
                          ],
                        ),
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Chip(
                              label: Text(task.status.replaceAll('_', ' ').toUpperCase()),
                              backgroundColor: _getStatusColor(task.status),
                            ),
                            PopupMenuButton(
                              onSelected: (value) async {
                                if (value == 'edit') {
                                  Navigator.of(context).push(
                                    MaterialPageRoute(
                                      builder: (context) => AddEditTaskScreen(task: task),
                                    ),
                                  );
                                } else if (value == 'delete') {
                                  _deleteTask(taskService, authService, task.id);
                                }
                              },
                              itemBuilder: (context) => [
                                PopupMenuItem(value: 'edit', child: Text('Edit')),
                                PopupMenuItem(value: 'delete', child: Text('Delete')),
                              ],
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                ),
    );
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'pending':
        return Colors.orange;
      case 'in_progress':
        return Colors.blue;
      case 'completed':
        return Colors.green;
      default:
        return Colors.grey;
    }
  }

  void _deleteTask(TaskService taskService, AuthService authService, String taskId) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Delete Task'),
        content: Text('Are you sure you want to delete this task?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.of(context).pop(true),
            child: Text('Delete'),
          ),
        ],
      ),
    );

    if (confirmed == true && authService.user != null) {
      final token = await authService.user!.getIdToken();
      if (token != null && token.isNotEmpty) {
        await taskService.deleteTask(token, taskId);
        
        if (taskService.errorMessage != null) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(taskService.errorMessage!)),
          );
        }
      }
    }
  }
}
