# End-to-End Zeebe Process Test 

### General Guidance

This application provides automated guidance for managing workflows within Camunda, including task assignment, completion tracking, and real-time feedback. It leverages Camunda Tasklist, Zeebe, and Operate clients to interact with the Camunda platform, perform process instance management, and retrieve workflow states.

The application also includes a suite of tests to verify workflow behavior, task handling, and state transitions, ensuring that processes run as expected.


### Features
1. Task Assignment and Tracking: Automates task assignment and keeps track of task completion across various stages.
2. Real-Time Process Guidance: Provides users with immediate feedback and guidance based on workflow progress.
3. State Management: Monitors and updates process state using Camunda Operate for accurate real-time visibility.

### Camunda Integration

This application utilizes several key Camunda components to handle workflows and processes:

1. Zeebe Client: Connects to the Camunda process engine, allowing the application to create and manage process instances.
2. Camunda Tasklist Client: Manages user tasks, allowing for dynamic task retrieval, assignment, and completion.
3. Camunda Operate Client: Enables process state retrieval and monitoring, providing insights into current process instances and states for a detailed view of the workflow.

These clients allow the application to interact directly with Camunda’s backend services, making it possible to start and monitor workflows, retrieve task information, and ensure consistent process flow.

### Configuration

Configuration settings are managed in the application.properties file. Key settings include:

##### Camunda Client Settings. These settings connect the application to your Camunda cluster.
``` 
camunda.client.clusterId=xxxxxxx
camunda.client.auth.clientId=xxxxxxx
camunda.client.auth.clientSecret=xxxxxxx
camunda.client.region=bru-2
camunda.client.mode=saas
```

##### Configure these settings to enable monitoring and querying process instances in Camunda Operate.
```
operate.client.region=bru-2
operate.client.clusterId=xxxxxxx
operate.client.clientId=xxxxxxx
operate.client.clientSecret=xxxxxxx
operate.client.profile=saas
```

##### These settings allow the application to interact with Camunda Tasklist for managing user tasks.
```
tasklist.client.region=bru-2
tasklist.client.clusterId=xxxxxxx
tasklist.client.clientId=xxxxxxx
tasklist.client.clientSecret=xxxxxxx
tasklist.client.profile=saas
```

### Tip: You need those credentials with the right access level created on your Camunda Console
### Testing

The application includes a comprehensive test suite that verifies the following:

1. Workflow Start and Completion: Tests the creation of process instances and ensures workflows reach the desired completion state.
2. Task Handling: Checks that tasks are correctly retrieved, assigned, and completed through the Camunda Tasklist client.
3. Process State Verification: Uses Camunda Operate to verify that process instances achieve the expected states during their lifecycle.

The tests leverage Awaitility to handle asynchronous polling and waiting, ensuring that process instances and tasks are fully available before actions are taken.

### Compatibility

Important: This application is compatible with Camunda versions of to 8.6.x. For other versions, refer to Camunda Documentation.
