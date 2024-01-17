package com.demo.backend.auth.saml;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.opensaml.saml.saml2.core.Response;

import java.io.IOException;

public class ResponseSerializer extends JsonSerializer<Response> {

    @Override
    public void serialize(Response response, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", response.getID());
        jsonGenerator.writeEndObject();
    }
}