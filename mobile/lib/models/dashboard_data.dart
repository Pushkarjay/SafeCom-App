class DashboardData {
  final String userName;
  final String greeting;
  final int totalTasks;
  final int pendingTasks;
  final int inProgressTasks;
  final int completedTasks;

  DashboardData({
    required this.userName,
    required this.greeting,
    required this.totalTasks,
    required this.pendingTasks,
    required this.inProgressTasks,
    required this.completedTasks,
  });

  factory DashboardData.fromJson(Map<String, dynamic> json) {
    return DashboardData(
      userName: json['userName'],
      greeting: json['greeting'],
      totalTasks: json['totalTasks'],
      pendingTasks: json['pendingTasks'],
      inProgressTasks: json['inProgressTasks'],
      completedTasks: json['completedTasks'],
    );
  }
}
