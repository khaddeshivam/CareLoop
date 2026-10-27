package com.example.hospitalManagement.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Receives and verifies webhook events sent by Stripe. */
@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final String webhookSecret;

    public StripeWebhookController(@Value("${stripe.webhook.secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String signature) {

        if (!StringUtils.hasText(webhookSecret) || !StringUtils.hasText(signature)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Event event = Webhook.constructEvent(payload, signature, webhookSecret);
            log.info("Received verified Stripe event: id={}, type={}", event.getId(), event.getType());

            // Add domain-specific handling for event.getType() here.
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException exception) {
            log.warn("Rejected Stripe webhook with an invalid signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
