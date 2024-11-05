package org.c8springzeebetestexample.helpers;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.ProcessInstanceState;
import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.dto.TaskList;
import io.camunda.tasklist.dto.TaskSearch;
import io.camunda.tasklist.exception.TaskListException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.awaitility.Awaitility;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
public class HelperFunctions {

    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    private final ZeebeClient client;
    private final CamundaOperateClient operateClient;
    private final CamundaTaskListClient taskListClient;

    public HelperFunctions(ZeebeClient client, CamundaOperateClient operateClient, CamundaTaskListClient taskListClient) {
        this.client = client;
        this.operateClient = operateClient;
        this.taskListClient = taskListClient;
    }

    public void deployProcess(String processId) {
        client.newDeployResourceCommand()
                .addResourceFromClasspath(processId + ".bpmn")
                .send()
                .join();
    }

    public ProcessInstanceEvent startInstance(String id, Map<String, Object> variables) {
        return client.newCreateInstanceCommand()
                .bpmnProcessId(id)
                .latestVersion()
                .variables(variables)
                .send()
                .join();
    }

    public ProcessInstance waitForProcessInstance(long instanceKey) throws OperateException {
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(10))
                .timeout(TIMEOUT)
                .until(() -> {
                    try {
                        ProcessInstance instance = operateClient.getProcessInstance(instanceKey);
                        return instance != null;
                    } catch (RuntimeException e) {
                        if (e.getMessage().contains("404")) {
                            return false;
                        }
                        throw e;
                    }
                });

        return operateClient.getProcessInstance(instanceKey);
    }

    public ProcessInstance waitForProcessCompletion(long instanceKey) throws OperateException {
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(5))
                .timeout(TIMEOUT)
                .until(() -> {
                    try {
                        ProcessInstance instance = operateClient.getProcessInstance(instanceKey);
                        return instance != null && instance.getState() == ProcessInstanceState.COMPLETED;
                    } catch (RuntimeException e) {
                        if (e.getMessage().contains("404")) {
                            return false;
                        }
                        throw e;
                    }
                });

        return operateClient.getProcessInstance(instanceKey);
    }

    public TaskList retrieveTask(long instanceKey) {
        return Awaitility.await()
                .timeout(TIMEOUT)
                .until(() -> {
                    TaskSearch search = new TaskSearch().setProcessInstanceKey(String.valueOf(instanceKey));
                    return taskListClient.getTasks(search);
                }, taskList -> taskList.size() == 1);
    }

    public void completeTask(String taskId, Map<String, Object> variables) throws TaskListException {
        taskListClient.completeTask(taskId, variables);
    }
}