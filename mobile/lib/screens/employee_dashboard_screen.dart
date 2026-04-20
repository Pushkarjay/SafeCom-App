import 'package:flutter/material.dart';
import 'package:flutter_app/models/dashboard_data.dart';
import 'package:flutter_app/services/auth_service.dart';
import 'package:flutter_app/services/dashboard_service.dart';
import 'package:provider/provider.dart';

class EmployeeDashboardScreen extends StatefulWidget {
  @override
  _EmployeeDashboardScreenState createState() =>
      _EmployeeDashboardScreenState();
}

class _EmployeeDashboardScreenState extends State<EmployeeDashboardScreen> {
  @override
  void initState() {
    super.initState();
    final authService = Provider.of<AuthService>(context, listen: false);
    final dashboardService =
        Provider.of<DashboardService>(context, listen: false);
    if (authService.user != null) {
      authService.user!.getIdToken().then((token) {
        if (token != null && token.isNotEmpty) {
          dashboardService.fetchDashboardData(token);
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final dashboardService = Provider.of<DashboardService>(context);
    final authService = Provider.of<AuthService>(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('Employee Dashboard'),
        actions: [
          IconButton(
            icon: Icon(Icons.logout),
            onPressed: () {
              authService.signOut();
              Navigator.of(context)
                  .pushNamedAndRemoveUntil('/', (route) => false);
            },
          )
        ],
      ),
      body: dashboardService.isLoading
          ? Center(child: CircularProgressIndicator())
          : dashboardService.errorMessage != null
              ? Center(child: Text(dashboardService.errorMessage!))
              : dashboardService.dashboardData == null
                  ? Center(child: Text('No data available'))
                  : _buildDashboard(dashboardService.dashboardData!),
    );
  }

  Widget _buildDashboard(DashboardData data) {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            data.greeting,
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          Text(
            data.userName,
            style: Theme.of(context).textTheme.titleLarge,
          ),
          SizedBox(height: 20),
          Expanded(
            child: GridView.count(
              crossAxisCount: 2,
              children: [
                _buildDashboardCard(
                    'Total Tasks', data.totalTasks.toString()),
                _buildDashboardCard(
                    'Pending Tasks', data.pendingTasks.toString()),
                _buildDashboardCard(
                    'In Progress', data.inProgressTasks.toString()),
                _buildDashboardCard(
                    'Completed', data.completedTasks.toString()),
              ],
            ),
          ),
          SizedBox(height: 20),
          ElevatedButton(
            onPressed: () {
              Navigator.of(context).pushNamed('/tasks');
            },
            child: Text('View Tasks'),
          ),
          SizedBox(height: 10),
          ElevatedButton(
            onPressed: () {
              Navigator.of(context).pushNamed('/messages');
            },
            child: Text('Messages'),
          ),
        ],
      ),
    );
  }

  Widget _buildDashboardCard(String title, String value) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              title,
              style: Theme.of(context).textTheme.titleMedium,
            ),
            SizedBox(height: 10),
            Text(
              value,
              style: Theme.of(context).textTheme.headlineMedium,
            ),
          ],
        ),
      ),
    );
  }
}
