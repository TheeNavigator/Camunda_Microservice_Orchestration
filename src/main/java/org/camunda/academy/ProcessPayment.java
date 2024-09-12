package org.camunda.academy;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import org.camunda.academy.handler.CreditCardServiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ProcessPayment {

    //Zeebe client credentials
    private static final String ZEEBE_ADDRESS = "51798061-7559-4ee9-8608-8f499e80c5c8.dsm-1.zeebe.camunda.io:443";
    private static final String ZEEBE_CLIENT_ID = "167TaOk9cZMhnrUX4n8ZUhP-z9gibSlD";
    private static final String ZEEBE_CLIENT_SECRET = "2DEDjPdOt-uUlACQwrL3J681mfA1uUYK4oLPtkxXspVH_kLJXl2j3qKVKX3pfy_C";
    private static final String ZEEBE_AUTHORIZATION_SERVER_URL = "https://login.cloud.camunda.io/oauth/token";
    private static final String ZEEBE_TOKEN_AUDIENCE = "zeebe.camunda.io";

    //Payment application details
    private static final int WORKER_TIMEOUT = 10;
    private static final int WORKER_TIME_TO_LIVE = 60;

    //Process definition details
    private static final String CREDIT_CARD_JOB_TYPE = "chargeCreditCard";

    //Logger
    private static final Logger logger = LoggerFactory.getLogger(ProcessPayment.class);

    public static void main(String[] args) {

        //authorization credential provider so the client can authenticate with the cluster
        final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
                .authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL)
                .audience(ZEEBE_TOKEN_AUDIENCE)
                .clientId(ZEEBE_CLIENT_ID)
                .clientSecret(ZEEBE_CLIENT_SECRET)
                .build();

        //creating a zeebe client to connect to the cluster
        try (ZeebeClient client = ZeebeClient.newClientBuilder()
                .gatewayAddress(ZEEBE_ADDRESS)
                .credentialsProvider(credentialsProvider)
                .build()) {

            //VARIABLES TO BE USED
            final Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("reference", "C-67558");
            variables.put("amount", 100.00);
            variables.put("cardNumber", "122344456767456");
            variables.put("cardExpiryDate", "12/2025");
            variables.put("cardCVV", "100");

            //CREATE AND START AN INSTANCE OF A SPECIFIED PROCESS
            client.newCreateInstanceCommand()
                    .bpmnProcessId("PaymentProcess")
                    .latestVersion()
                    .variables(variables)
                    .send().join();

            //register a new job worker for Jobs of given type
            final JobWorker creditCardWorker = client.newWorker()
                    .jobType("chargeCreditCard")
                    .handler(new CreditCardServiceHandler())
                    .timeout(Duration.ofSeconds(10).toMillis())
                    .open();
            Thread.sleep(10000);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}