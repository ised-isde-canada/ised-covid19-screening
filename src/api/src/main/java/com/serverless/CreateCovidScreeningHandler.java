package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.common.RequestResponseHandler;
import com.serverless.dynamodb.CovidScreening;
import com.serverless.dynamodb.IsedWorkLocations;
import com.serverless.dynamodb.QuestionAndAnswer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateCovidScreeningHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {
            // get the 'body' from input
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

            // create the CovidScreening object for post
            CovidScreening covidScreening = new CovidScreening();
            // covidScreening.setId(body.get("id").asText());
            covidScreening.setName(body.get("name").asText());
            // covidScreening.setPhone(body.get("phone").asText());
            // covidScreening.setEmail(body.get("email").asText());
            covidScreening.setAddressPk(body.get("addressPk").asText());
            covidScreening.setIsedWorkLocations(getIsedWorkLocationByPK(body.get("addressPk").asText()));

            if (Objects.isNull(body.get("consentAccepted"))) {
                covidScreening.setConsentAccepted(false);
            } else {
                covidScreening.setConsentAccepted(body.get("consentAccepted").asBoolean());
            }
            covidScreening.setDate(getCurrentDate());
            covidScreening.setTtl(calculateTTL());

            logger.info("question and answer " + body.findValues("questionandAnswers"));
            ObjectMapper mapper = new ObjectMapper();
            String qandaString = mapper.writeValueAsString(body.get("questionandAnswers"));
            logger.debug("Q and A string value" + qandaString);
            List<QuestionAndAnswer> questionAndAnswerList = mapper.readValue(qandaString, new TypeReference<List<QuestionAndAnswer>>() {
            });
            covidScreening.setQuestionAndAnswers(questionAndAnswerList);
            covidScreening.setScreeningResult(decideScreeningResult(questionAndAnswerList));
            covidScreening.save(covidScreening);

            // send the response back
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(covidScreening)
                    .setHeaders(RequestResponseHandler.createHeaders())
                    .build();

        } catch (Exception ex) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            ex.printStackTrace(printWriter);
            String stackTraceAsString = stringWriter.toString();
            logger.error("Error in saving covidScreening4: " + stackTraceAsString);

            // send the error response back
            Response responseBody = new Response("Error in saving covidScreening4: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }

    /**
     * if any of the answer is true. That means he is not eligible to enter the ised work locations.
     *
     * @param questionAndAnswerList
     * @return screeningPassed
     */
    private String decideScreeningResult(List<QuestionAndAnswer> questionAndAnswerList) {
        String screeningResult = "PASS";
        for (QuestionAndAnswer questionAndAnswer : questionAndAnswerList) {
            if (Boolean.valueOf(questionAndAnswer.getAnswer()).booleanValue()) {
                screeningResult = "FAIL";
                break;
            }
        }
        return screeningResult;
    }


    private String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Rertuns current time + two months in epoch seconds.
     * This property is used for dynamodb time to live.
     * When the epoch seconds crosses this the dynamodb data will expire.
     *
     * @return timeToLive
     */
    private long calculateTTL() {
        long timeToLive = LocalDateTime.now().plusMonths(2).atZone(ZoneId.of("Canada/Central")).toInstant().toEpochMilli() / 1000;
        return timeToLive;
    }

    /**
     * Returns the ised work locations based on the addressPk.
     *
     * @param addressPk
     * @return
     * @throws IOException
     */
    public CovidScreening.IsedWorkLocations getIsedWorkLocationByPK(String addressPk) throws IOException {
        IsedWorkLocations isedWorkLocations = new IsedWorkLocations().get(addressPk);
        CovidScreening.IsedWorkLocations cIsedWorkLocations = new CovidScreening.IsedWorkLocations();
        cIsedWorkLocations.setPk(isedWorkLocations.getPk());
        cIsedWorkLocations.setAddressEn(isedWorkLocations.getAddressEn());
        cIsedWorkLocations.setAddressFr(isedWorkLocations.getAddressFr());
        cIsedWorkLocations.setCity(isedWorkLocations.getCity());
        cIsedWorkLocations.setProvince(isedWorkLocations.getProvince());
        return cIsedWorkLocations;
    }
}
