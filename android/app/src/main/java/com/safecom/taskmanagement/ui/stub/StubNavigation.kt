package com.safecom.taskmanagement.ui.stub

// Stub navigation actions to fix compilation errors
// These represent navigation destinations that need to be properly defined in nav_graph.xml

object StubNavigationActions {
    const val actionDashboardToCreateTask = -1
    const val actionMessagesToNewMessage = -2
    const val actionProfileToNotificationSettings = -3
    const val actionProfileToSecuritySettings = -4
    const val actionProfileToAbout = -5
    const val actionProfileToHelp = -6
    const val createTaskFragment = -7
    const val notificationsFragment = -8
    const val settingsFragment = -9
}

// Extension to provide missing navigation IDs
object R {
    object id {
        const val actionDashboardToCreateTask = StubNavigationActions.actionDashboardToCreateTask
        const val actionMessagesToNewMessage = StubNavigationActions.actionMessagesToNewMessage
        const val actionProfileToNotificationSettings = StubNavigationActions.actionProfileToNotificationSettings
        const val actionProfileToSecuritySettings = StubNavigationActions.actionProfileToSecuritySettings
        const val actionProfileToAbout = StubNavigationActions.actionProfileToAbout
        const val actionProfileToHelp = StubNavigationActions.actionProfileToHelp
        const val createTaskFragment = StubNavigationActions.createTaskFragment
        const val notificationsFragment = StubNavigationActions.notificationsFragment
        const val settingsFragment = StubNavigationActions.settingsFragment
    }
}
