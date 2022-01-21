package com.serverless.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@DynamoDBTable(tableName = "PLACEHOLDER_COVIDSCREENING_TABLE_NAME")
public class CovidScreening {

    // get the table name from env. var. set in serverless.yml
    private static final String COVIDSCREENING_TABLE_NAME = System.getenv("COVIDSCREENING_TABLE_NAME");

    private static DynamoDBAdapter db_adapter;
    private final AmazonDynamoDB client;
    private final DynamoDBMapper mapper;

    private final Logger logger = LogManager.getLogger(this.getClass());

    private String id;
    private String name;
    private String phone;
    private String date;
    private String email;
    private List<QuestionAndAnswer> questionAndAnswers;
    private boolean consentAccepted;
    private String addressPk;
    private String screeningResult;
    private IsedWorkLocations isedWorkLocations;
    private long ttl;

    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBRangeKey(attributeName = "name")
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @DynamoDBAttribute(attributeName = "date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @DynamoDBAttribute(attributeName = "questionAndAnswers")
    public List<QuestionAndAnswer> getQuestionAndAnswers() {
        return questionAndAnswers;
    }

    public void setQuestionAndAnswers(List<QuestionAndAnswer> questionAndAnswers) {
        this.questionAndAnswers = questionAndAnswers;
    }

    @DynamoDBAttribute(attributeName = "consentAccepted")
    public boolean isConsentAccepted() {
        return consentAccepted;
    }

    public void setConsentAccepted(boolean consentAccepted) {
        this.consentAccepted = consentAccepted;
    }

    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "addressPk")
    public String getAddressPk() {
        return addressPk;
    }

    public void setAddressPk(String addressPk) {
        this.addressPk = addressPk;
    }

    @DynamoDBAttribute(attributeName = "screeningResult")
    public String getScreeningResult() {
        return screeningResult;
    }

    public void setScreeningResult(String screeningResult) {
        this.screeningResult = screeningResult;
    }

    @DynamoDBAttribute(attributeName = "isedWorkLocations")
    public IsedWorkLocations getIsedWorkLocations() {
        return isedWorkLocations;
    }

    public void setIsedWorkLocations(IsedWorkLocations isedWorkLocations) {
        this.isedWorkLocations = isedWorkLocations;
    }

    @DynamoDBAttribute(attributeName = "ttl")
    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public CovidScreening() {
        // build the mapper config
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(COVIDSCREENING_TABLE_NAME))
                .build();
        // get the db adapter
        this.db_adapter = DynamoDBAdapter.getInstance();
        this.client = this.db_adapter.getDbClient();
        // create the mapper with config
        this.mapper = this.db_adapter.createDbMapper(mapperConfig);
    }

    public String toString() {
        return String.format("CovidScreening [id=%s, name=%s, phone=%s, date=%s]", this.id, this.name, this.phone, this.date);
    }

    // methods
    public Boolean ifTableExists() {
        return this.client.describeTable(COVIDSCREENING_TABLE_NAME).getTable().getTableStatus().equals("ACTIVE");
    }

    public List<CovidScreening> list() throws IOException {
        DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
        List<CovidScreening> results = this.mapper.scan(CovidScreening.class, scanExp);
        for (CovidScreening p : results) {
            logger.info("CovidScreening - list(): " + p.toString());
        }
        return results;
    }

    public CovidScreening get(String id) throws IOException {
        CovidScreening covidScreening = null;

        HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
        av.put(":v1", new AttributeValue().withS(id));

        DynamoDBQueryExpression<CovidScreening> queryExp = new DynamoDBQueryExpression<CovidScreening>()
                .withKeyConditionExpression("id = :v1")
                .withExpressionAttributeValues(av);

        PaginatedQueryList<CovidScreening> result = this.mapper.query(CovidScreening.class, queryExp);
        if (result.size() > 0) {
            covidScreening = result.get(0);
            logger.info("CovidScreening - get(): covidScreening - " + covidScreening.toString());
        } else {
            logger.info("CovidScreening - get(): covidScreening - Not Found.");
        }
        return covidScreening;
    }

    public void save(CovidScreening covidScreening) throws IOException {
        logger.info("CovidScreening - save(): " + covidScreening.toString());
        this.mapper.save(covidScreening);
    }

    public Boolean delete(String id) throws IOException {
        CovidScreening covidScreening = null;

        // get covidScreening if exists
        covidScreening = get(id);
        if (covidScreening != null) {
            logger.info("CovidScreening - delete(): " + covidScreening.toString());
            this.mapper.delete(covidScreening);
        } else {
            logger.info("CovidScreening - delete(): covidScreening - does not exist.");
            return false;
        }
        return true;
    }

    @DynamoDBDocument
    public static class IsedWorkLocations {
        private String pk;
        private String addressEn;
        private String addressFr;
        private String city;
        private String province;

        @DynamoDBAttribute(attributeName = "pk")
        public String getPk() {
            return this.pk;
        }
        public void setPk(String pk) {
            this.pk = pk;
        }

        @DynamoDBAttribute(attributeName = "address-en")
        public String getAddressEn() {
            return addressEn;
        }

        public void setAddressEn(String addressEn) {
            this.addressEn = addressEn;
        }
        @DynamoDBAttribute(attributeName = "address-fr")
        public String getAddressFr() {
            return addressFr;
        }

        public void setAddressFr(String addressFr) {
            this.addressFr = addressFr;
        }
        @DynamoDBAttribute(attributeName = "city")
        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
        @DynamoDBAttribute(attributeName = "province")
        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }
    }
}
