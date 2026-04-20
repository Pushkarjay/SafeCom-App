class Task {
  final String id;
  final String title;
  final String description;
  final String status;
  final String assignedTo;
  final DateTime dueDate;

  Task({
    required this.id,
    required this.title,
    required this.description,
    required this.status,
    required this.assignedTo,
    required this.dueDate,
  });

  factory Task.fromJson(Map<String, dynamic> json) {
    return Task(
      id: json['_id'],
      title: json['title'],
      description: json['description'],
      status: json['status'],
      assignedTo: json['assignedTo'],
      dueDate: DateTime.parse(json['dueDate']),
    );
  }
}
