package org.camunda.academy.handler;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import org.camunda.academy.service.CreditCardService;

import java.util.HashMap;
import java.util.Map;

public class CreditCardServiceHandler implements JobHandler {

    CreditCardService cardService = new CreditCardService();

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {

        final Map<String , Object> inputVariables = job.getVariablesAsMap();
        final String reference = (String) inputVariables.get("reference");
        final Double amount = (Double) inputVariables.get("amount");
        final String cardNumber = (String) inputVariables.get("cardNumber");
        final String cardExpiryDate = (String) inputVariables.get("cardExpiryDate");
        final String cardCVV = (String) inputVariables.get("cardCVV");

        final String confirmation = cardService.chargeCreditCard(reference, amount, cardNumber, cardExpiryDate, cardCVV);
        final Map<String, Object> outputVariables = new HashMap<String, Object>();
        outputVariables.put("Confirmation ", confirmation);


        //inform zeebe that the specified job is completed
        client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();

    }
}
