package org.c8springzeebetestexample;

import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.ProcessInstanceState;
import org.c8springzeebetestexample.helpers.HelperFunctions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.UUID;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaperReviewTest {

    @Autowired
    private HelperFunctions helperFunctions;

    private static final String PROCESS_ID = "PaperReviewProcess";

    @BeforeEach
    public void setup() {
        helperFunctions.deployProcess(PROCESS_ID);
    }

    @Test
    @Order(1)
    public void testProcessHasStarted() throws OperateException {
        // Given: Start process instance with variables
        var reviewId = UUID.randomUUID().toString();
        var instance = helperFunctions.startInstance(PROCESS_ID, Map.of("credible", true, "reviewId", reviewId));
        long instanceKey = instance.getProcessInstanceKey();

        // When: Retrieve the started process instance from Operate
        ProcessInstance retrievedInstance = helperFunctions.waitForProcessInstance(instanceKey);

        // Then: Verify process instance is retrieved
        Assertions.assertNotNull(retrievedInstance, "Process instance should be retrievable from Operate.");
        helperFunctions.cancelInstance(retrievedInstance);
    }

    @Test
    @Order(2)
    public void testHappyPath() throws OperateException {
        // Given: Start process with credible variable set to true
        var reviewId = UUID.randomUUID().toString();
        var instance = helperFunctions.startInstance(PROCESS_ID, Map.of("credible", true, "reviewId", reviewId));

        // When: Wait for process completion
        helperFunctions.publishMessage("credibilityResponse", reviewId, Map.of("Author credibility ", true));
        System.out.println("Message published. ");
        ProcessInstance completedInstance = helperFunctions.waitForProcessCompletion(instance.getProcessInstanceKey());

        // Then: Verify the process instance completed
        Assertions.assertEquals(ProcessInstanceState.COMPLETED, completedInstance.getState(),
                "Expected process instance to be in COMPLETED state.");
    }

    @Test
    @Order(3)
    public void testAlternativePath() throws Exception {
        // Given: Start process with credible variable set to false
        var reviewId = UUID.randomUUID().toString();
        var instance = helperFunctions.startInstance(PROCESS_ID, Map.of("credible", false, "reviewId", reviewId));

        long instanceKey = instance.getProcessInstanceKey();
        helperFunctions.publishMessage("credibilityResponse", reviewId, Map.of("Author credibility ", false));
        System.out.println("Message published. ");

        // When: Retrieve and complete the user task
        var taskList = helperFunctions.retrieveTask(instanceKey);
        helperFunctions.completeTask(taskList.get(0).getId(), Map.of());

        // Then: Verify the process instance is completed
        ProcessInstance completedInstance = helperFunctions.waitForProcessCompletion(instanceKey);
        Assertions.assertEquals(ProcessInstanceState.COMPLETED, completedInstance.getState(),
                "Expected process instance to be in COMPLETED state.");
    }
}