package com.demo.backend.auth.saml;

import com.demo.backend.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.*;
import org.springframework.stereotype.Service;

@Service
public class SamlTokenService {

    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();


    public String createSamlResponse(User user) throws JsonProcessingException {
        Assertion assertion = buildAssertion(user);
        Response response = buildResponse(user, assertion);

        module.addSerializer(Response.class, new ResponseSerializer());
        objectMapper.registerModule(module);

        String jsonString = objectMapper.writeValueAsString(response);
        return jsonString;
    }

    private Assertion buildAssertion(User user) {
        Assertion assertion = new AssertionBuilder().buildObject();
        assertion.setID("user");

        Issuer issuerAssertion = buildIssuer(user.getEmail());
        assertion.setIssuer(issuerAssertion);

        assertion.setIssueInstant(new DateTime());

        Subject subject = buildSubject(user.getEmail());
        AuthnStatement authnStatement = buildAuthnStatement();

        assertion.setSubject(subject);
        assertion.getAuthnStatements().add(authnStatement);

        return assertion;
    }

    private Issuer buildIssuer(String value) {
        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setValue(value);
        return issuer;
    }

    private Subject buildSubject(String value) {
        Subject subject = new SubjectBuilder().buildObject();
        NameID nameID = buildNameID(value);
        subject.setNameID(nameID);
        return subject;
    }

    private NameID buildNameID(String value) {
        NameID nameID = new NameIDBuilder().buildObject();
        nameID.setValue(value);
        return nameID;
    }

    private AuthnStatement buildAuthnStatement() {
        AuthnStatement authnStatement = new AuthnStatementBuilder().buildObject();
        authnStatement.setAuthnInstant(new DateTime());
        return authnStatement;
    }

    private Response buildResponse(User user, Assertion assertion) {
        Response response = new ResponseBuilder().buildObject();
        response.setID("user");

        Issuer issuerResponse = buildIssuer(user.getEmail());
        response.setIssuer(issuerResponse);

        response.setStatus(buildStatus());
        response.getAssertions().add(assertion);

        return response;
    }

    private Status buildStatus() {
        Status status = new StatusBuilder().buildObject();
        StatusCode statusCode = buildStatusCode();
        status.setStatusCode(statusCode);
        return status;
    }

    private StatusCode buildStatusCode() {
        StatusCode statusCode = new StatusCodeBuilder().buildObject();
        statusCode.setValue(StatusCode.SUCCESS);
        return statusCode;
    }
}