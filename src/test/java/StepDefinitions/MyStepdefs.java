package StepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.bs.A;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.hu.Ha;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MyStepdefs {

    RequestSpecification requestSpecification;
    Response response;
    ObjectMapper objectMapper=new ObjectMapper();
    File file = new File(getClass().getClassLoader().getResource("consignment.json").getFile());
    static HashMap<String,Object> consignment=new HashMap<>();
    static String draftNumber;
    static List<Map<String,Object>> productList;
    @Given("the user access to {string}")
    public void theUserAccessTo(String uri) {
        requestSpecification = RestAssured.given()
                .baseUri(uri)
                .contentType(ContentType.JSON)
                .header("Header-Authenticator","subash28122k3@gmail.com");

        System.out.println(draftNumber);
    }


    @When("the user hits the api with get method with endpoint {string} with parameters {string} and {string}")
    public void theUserHitsTheApiWithGetMethodWithEndpointWithParametersAnd(String endPoint, String businessPartnerCode, String itemSku) throws IOException {
        response = requestSpecification
                .queryParam("businessPartnerCode",businessPartnerCode)
                .queryParam("itemSku",itemSku)
                .when()
                .get(endPoint)
                .then()
                .body(matchesJsonSchemaInClasspath("productSchema.json"))
                .statusCode(200)
                .statusLine("HTTP/1.1 200 ")
//                .log().all()
                .extract().response();

        JsonPath jsonPath=response.jsonPath();
        productList=jsonPath.getList("data");
        System.out.println(productList);
//
//        Map<String,Object> map=jsonPath.getMap("data[0]");

//        DataItem data = objectMapper.readValue(jsonPath.getMap("data[0]").toString(), DataItem.class);
//        System.out.println(jsonPath.get("data").toString());
//        response.body().asPrettyString();
//        dataCollection.add(data);

    }

    @Then("the user should get response {int}")
    public void theUserShouldSeeResponse(int statusCode) {
        assertThat(response.getStatusCode(), equalTo(statusCode));
        assertThat(response.getStatusLine(),equalTo("HTTP/1.1 "+statusCode+" "));
    }

    @When("the user hits the api with post method with endpoint {string}")
    public void theUserHitsTheApiWithPostMethodWithEndpoint(String endPoint) throws IOException {


//        PostRequest data = objectMapper.readValue(file, PostRequest.class);
//        System.out.println(data.getMerchantName());


        consignment.put("addedProducts",new ArrayList<>());
        consignment.put("currentWarehouseTab",0);
        consignment.put("deliveryNotes",null);
        consignment.put("draftNumber",null);
        consignment.put("draftStatus","SELECT_SHIPMENT");
        consignment.put("merchantCode","BLF-60033");
        consignment.put("merchantName","BLF-60033");
        consignment.put("notAllocatedProducts",new ArrayList<>());
        consignment.put("notEligibleInAnyWarehouse",new ArrayList<>());
        consignment.put("newNotEligibleInAnyWarehouse",new ArrayList<>());
        consignment.put("potentialInboundFee",null);
        consignment.put("regionCode","BAN-001");
        consignment.put("regionName","West Java");
        consignment.put("removedProducts",new ArrayList<>());
        Map<String,Map<String,Object>> productInfo=new HashMap<>();
        for(Map<String,Object> product:productList){
            Map<String,Object> details=new HashMap<>();
            details.put("blibliSku",product.get("blibliSku"));
            details.put("categoryName",product.get("categoryName"));
            details.put("confirmedQuantity",product.get("confirmedQuantity"));
            details.put("itemCode",product.get("itemCode"));
            details.put("itemName",product.get("itemName"));
            details.put("packageWeight",product.get("packageWeight"));
            details.put("productItemHeight",product.get("productItemHeight"));
            details.put("productItemLength",product.get("productItemLength"));
            details.put("productItemWidth",product.get("productItemWidth"));
            details.put("proposedQuantity",product.get("proposedQuantity"));
            details.put("recommendedQuantity",product.get("recommendedQuantity"));
            details.put("recommendedQuantityErrorCode",product.get("recommendedQuantityErrorCode"));
            details.put("categoryHierarchy",product.get("categoryHierarchy"));
            productInfo.put(product.get("itemCode").toString(),details);
        }
        Map<String,Object> shipping=new HashMap<>();
        shipping.put("emergencyContactEmail","sakthi@gmail.com");
        shipping.put("emergencyContactNumber","4998158156");
        shipping.put("pickupPointAddress","");
        shipping.put("pickupPointCode","");
        shipping.put("pickupPointName","");
        shipping.put("preferredTimeSchedules",new ArrayList<>());
        shipping.put("sellerPicName","sakthi.samyuktha@gdn-commerce.com");
        shipping.put("sellerPicPhoneNumber","9012732163");
        shipping.put("shippingType","SHIPPED_BY_MERCHANT");
        shipping.put("warehouseCode","HUB-0000000001");
        shipping.put("warehouseName","BDG E - Bandung Kiaracondong");
        shipping.put("warehouseAddress","Jl. Ibrahim Adjie No.338, Kelurahan Binong, Kecamatan Batununggal, Kota Bandung Kel. Cisaranten Wetan, Kec. Cinambo, Kota Bandung");

        List<Map<String,Object>> totalShipping=new ArrayList<>();
        consignment.put("selectedProducts",productInfo);
        consignment.put("shippingDetails",totalShipping);
        consignment.put("userType","INTERNAL");

        List<Map<String,Object>> warehouseList=new ArrayList<>();
        Map<String,Object> warehouseMap=new HashMap<>();
        List<String> productList=new ArrayList<>();
        productList.add("BLF-60033-00001-00001");
        warehouseMap.put("selectedProducts",productList);
        warehouseMap.put("warehouseAddress","Jl. Ibrahim Adjie No.338, Kelurahan Binong, Kecamatan Batununggal, Kota Bandung Kel. Cisaranten Wetan, Kec. Cinambo, Kota Bandung");
        warehouseMap.put("warehouseCode","HUB-0000000001");
        warehouseMap.put("warehouseName","BDG E - Bandung Kiaracondong");
        warehouseMap.put("warehouseRank",1);
        warehouseList.add(warehouseMap);
        consignment.put("warehouseAllocations",warehouseList);

        String MapToJson=objectMapper.writeValueAsString(consignment);
        response = requestSpecification.body(MapToJson)
                .when()
                .post(endPoint)
                .then()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 ")
                .log().all()
                .extract().response();

        JsonPath jsonPath=response.jsonPath();
        draftNumber=jsonPath.get("data");
    }

    @Then("the user should not get any errors in error field")
    public void theUserShouldGetAnyErrorsInErrorField() {
        JsonPath jsonPath=response.jsonPath();
        Assert.assertEquals(jsonPath.getMap("errors").size(),0,"There is some error in error field");

    }

    @When("the user hits the api with get method with endpoint {string} with parameter {string}")
    public void theUserHitsTheApiWithGetMethodWithEndpointWithParameters(String endPoint, String businessPartnerCode) {
        System.out.println(draftNumber);
        response = requestSpecification
                .queryParam("businessPartnerCode",businessPartnerCode)
                .queryParam("draftNumber",draftNumber)
                .when()
                .get(endPoint)
                .then()
                .log().all()
                .extract().response();

    }

    @When("the user hits the api with delete method with endpoint {string} with parameter {string}")
    public void theUserHitesTheApiWithDeleteMethodWithEndpointWithParameter(String endPoint, String businessPartnerCode) {
        response = requestSpecification
                .queryParam("businessPartnerCode",businessPartnerCode)
                .queryParam("draftNumber",draftNumber)
                .when()
                .delete(endPoint)
                .then()
//                .body(matchesJsonSchemaInClasspath("productSchema.json"))
                .statusCode(200)
                .statusLine("HTTP/1.1 200 ")
//                .log().all()
                .extract().response();
    }

    @Then("the user should get the correct details")
    public void theUserShouldGetTheCorrectDetails() {
        JsonPath jsonPath=response.jsonPath();
        Assert.assertEquals(jsonPath.get("data.draftNumber"),draftNumber,"DraftNumber mismatch");
        Assert.assertEquals(jsonPath.get("data.merchantCode"),consignment.get("merchantCode").toString());
        Assert.assertEquals(jsonPath.get("data.merchantName"),consignment.get("merchantName").toString());
   }

    @Then("the user should get no data with error message {string}")
    public void theUserShouldGetNoDataWithErrorMessage(String message) {
        Assert.assertTrue(response.jsonPath().get("data").toString().contains(message));
    }
}
