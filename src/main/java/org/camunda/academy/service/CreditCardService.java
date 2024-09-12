package org.camunda.academy.service;

public class CreditCardService {

    public String chargeCreditCard(
            final String reference,
            final Double amount,
            final String cardNumber,
            final String cardExpiryDate,
            final String cardCVV){

        System.out.println("Starting transaction: " + reference);
        System.out.println("Card number: " + cardNumber);
        System.out.println("Card Expiry Date: " + cardExpiryDate);
        System.out.println("Card CVV: " + cardCVV);
        System.out.println("Amount: " + amount);

        final String confirmation = String.valueOf(System.currentTimeMillis());
        System.out.println("Successful Transaction: " + confirmation);
        return confirmation;
    }
}
