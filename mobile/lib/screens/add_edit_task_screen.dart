import 'package:flutter/material.dart';
import 'package:flutter_app/models/task.dart';
import 'package:flutter_app/services/auth_service.dart';
import 'package:flutter_app/services/task_service.dart';
import 'package:provider/provider.dart';

class AddEditTaskScreen extends StatefulWidget {
  final Task? task;

  AddEditTaskScreen({this.task});

  @override
  _AddEditTaskScreenState createState() => _AddEditTaskScreenState();
}

class _AddEditTaskScreenState extends State<AddEditTaskScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _assignedToController = TextEditingController();
  String _selectedStatus = 'pending';
  DateTime _selectedDueDate = DateTime.now().add(Duration(days: 7));

  @override
  void initState() {
    super.initState();
    if (widget.task != null) {
      _titleController.text = widget.task!.title;
      _descriptionController.text = widget.task!.description;
      _assignedToController.text = widget.task!.assignedTo;
      _selectedStatus = widget.task!.status;
      _selectedDueDate = widget.task!.dueDate;
    }
  }

  @override
  Widget build(BuildContext context) {
    final taskService = Provider.of<TaskService>(context);
    final authService = Provider.of<AuthService>(context);

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.task == null ? 'Add Task' : 'Edit Task'),
        actions: [
          IconButton(
            icon: Icon(Icons.save),
            onPressed: () => _saveTask(taskService, authService),
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: _titleController,
                decoration: InputDecoration(labelText: 'Title'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter a title';
                  }
                  return null;
                },
              ),
              TextFormField(
                controller: _descriptionController,
                decoration: InputDecoration(labelText: 'Description'),
                maxLines: 3,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter a description';
                  }
                  return null;
                },
              ),
              TextFormField(
                controller: _assignedToController,
                decoration: InputDecoration(labelText: 'Assigned To'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter who this is assigned to';
                  }
                  return null;
                },
              ),
              DropdownButtonFormField<String>(
                value: _selectedStatus,
                decoration: InputDecoration(labelText: 'Status'),
                items: ['pending', 'in_progress', 'completed']
                    .map((status) => DropdownMenuItem(
                          value: status,
                          child: Text(status.replaceAll('_', ' ').toUpperCase()),
                        ))
                    .toList(),
                onChanged: (value) {
                  setState(() {
                    _selectedStatus = value!;
                  });
                },
              ),
              ListTile(
                title: Text('Due Date'),
                subtitle: Text('${_selectedDueDate.toLocal()}'.split(' ')[0]),
                trailing: Icon(Icons.calendar_today),
                onTap: () => _selectDate(context),
              ),
              SizedBox(height: 20),
              taskService.isLoading
                  ? CircularProgressIndicator()
                  : ElevatedButton(
                      onPressed: () => _saveTask(taskService, authService),
                      child: Text(widget.task == null ? 'Create Task' : 'Update Task'),
                    ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDueDate,
      firstDate: DateTime.now(),
      lastDate: DateTime(2025),
    );
    if (picked != null && picked != _selectedDueDate) {
      setState(() {
        _selectedDueDate = picked;
      });
    }
  }

  void _saveTask(TaskService taskService, AuthService authService) async {
    if (_formKey.currentState!.validate()) {
      final taskData = {
        'title': _titleController.text,
        'description': _descriptionController.text,
        'assignedTo': _assignedToController.text,
        'status': _selectedStatus,
        'dueDate': _selectedDueDate.toIso8601String(),
      };

      if (authService.user != null) {
        final token = await authService.user!.getIdToken();
        if (token != null && token.isNotEmpty) {
          if (widget.task == null) {
            await taskService.createTask(token, taskData);
          } else {
            await taskService.updateTask(token, widget.task!.id, taskData);
          }

          if (taskService.errorMessage == null) {
            Navigator.of(context).pop();
          } else {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(taskService.errorMessage!)),
            );
          }
        }
      }
    }
  }
}
