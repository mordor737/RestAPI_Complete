package com.base.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.base.shared.dto.UserDto;

public class AmazonSES {
    //address must be verified with Amazon SES.
    String FROM = "procomtech737@gmail.com";

    //subject line for the email.
    String SUBJECT = "One last step to complete your registration with PhotoApp";
    //HTML body for the email.
    String HTMLBODY = "<h1>Please verify your email address</h1? <p> Thank you for registering with our mobile app. To complete registration process and be able to log in click on the following link: <a href='http://localhost:8080/verifiaction-service/email-verification.html?token=$tokenValue'> Final step to complete your registration </a><br/><br/>Thank you| And we are waiting for you inside!";
    private String htmlBodyWithToken;

    public void verifyEmail(UserDto userDto) {
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

        String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
        //String textBodyWithToken=TEXTBODY.replace("$tokenValue",userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))).withSubject(new Content().withCharset("UTF-8").withData(SUBJECT))).withSource(FROM);

        client.sendEmail(request);
        System.out.println("Email Sent");
    }
}
