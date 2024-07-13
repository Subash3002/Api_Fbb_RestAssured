package StepDefinitions;

import Pojo.Paging;
import Pojo.ProductList;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MyStepdefs {
    Properties properties=new Properties();
    RequestSpecification requestSpecification;
    Response response;
    ObjectMapper objectMapper;
    static HashMap<String,Object> consignment=new HashMap<>();
    static String draftNumber;
    static ProductList[] productList;
    static Paging paging;

    @Given("the user access to {string}")
    public void theUserAccessTo(String uri) throws IOException {
        properties.load(new FileInputStream("src/test/java/input.properties"));
        requestSpecification = RestAssured.given()
                .baseUri(uri)
                .contentType(ContentType.JSON)
                .header("Header-Authenticator","subash28122k3@gmail.com");
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
                .log().all()
                .extract().response();

        objectMapper=new ObjectMapper();
        String Datajson = objectMapper.writeValueAsString(response.jsonPath().get("data")); // Get JSON string
        productList = objectMapper.readValue(Datajson, ProductList[].class);
        String pageJson=objectMapper.writeValueAsString(response.jsonPath().get("paging"));
        paging=objectMapper.readValue(pageJson, Paging.class);

    }

    @Then("the user should get response {int}")
    public void theUserShouldSeeResponse(int statusCode) {
        assertThat(response.getStatusCode(), equalTo(statusCode));
        assertThat(response.getStatusLine(),equalTo("HTTP/1.1 "+statusCode+" "));
    }

    @When("the user hits the api with post method with endpoint {string}")
    public void theUserHitsTheApiWithPostMethodWithEndpoint(String endPoint) throws IOException {

        consignment.put("addedProducts",new ArrayList<>());
        consignment.put("currentWarehouseTab",properties.getProperty("currentWarehouseTab"));
        consignment.put("deliveryNotes",properties.getProperty("deliveryNotes"));
        consignment.put("draftNumber",null);
        consignment.put("draftStatus","SELECT_SHIPMENT");
        consignment.put("merchantCode",properties.getProperty("merchantCode"));
        consignment.put("merchantName",properties.getProperty("merchantName"));
        consignment.put("notAllocatedProducts",new ArrayList<>());
        consignment.put("notEligibleInAnyWarehouse",new ArrayList<>());
        consignment.put("newNotEligibleInAnyWarehouse",new ArrayList<>());
        consignment.put("potentialInboundFee",properties.getProperty("potentialInboundFee"));
        consignment.put("regionCode",properties.getProperty("regionCode"));
        consignment.put("regionName",properties.getProperty("regionName"));
        consignment.put("removedProducts",new ArrayList<>());

        Map<String,Map<String,Object>> productInfo=new HashMap<>();

        for(ProductList product:productList){
            Map<String,Object> details=new HashMap<>();
            details.put("blibliSku",product.getBlibliSku());
            details.put("categoryName",product.getCategoryName());
            details.put("confirmedQuantity",null);
            details.put("itemCode",product.getItemCode());
            details.put("itemName",product.getItemName());
            details.put("packageWeight",product.getPackageWeight());
            details.put("productItemHeight",product.getProductItemHeight());
            details.put("productItemLength",product.getProductItemLength());
            details.put("productItemWidth",product.getProductItemWidth());
            details.put("proposedQuantity",product.getProposedQuantity());
            details.put("recommendedQuantity",null);
            details.put("recommendedQuantityErrorCode",null);
            details.put("categoryHierarchy",product.getCategoryHierarchy());
            productInfo.put(product.getItemCode(),details);
        }

        Map<String,Object> shipping=new HashMap<>();

        shipping.put("emergencyContactEmail",properties.getProperty("emergencyContactEmail"));
        shipping.put("emergencyContactNumber",properties.getProperty("emergencyContactNumber"));
        shipping.put("pickupPointAddress",properties.getProperty("pickupPointAddress"));
        shipping.put("pickupPointCode",properties.getProperty("pickupPointCode"));
        shipping.put("pickupPointName",properties.getProperty("pickupPointName"));
        shipping.put("preferredTimeSchedules",new ArrayList<>());
        shipping.put("sellerPicName",properties.getProperty("sellerPicName"));
        shipping.put("sellerPicPhoneNumber",properties.getProperty("sellerPicPhoneNumber"));
        shipping.put("shippingType",properties.getProperty("shippingType"));
        shipping.put("warehouseCode",properties.getProperty("warehouseCode"));
        shipping.put("warehouseName",properties.getProperty("warehouseName"));
        shipping.put("warehouseAddress",properties.getProperty("warehouseAddress"));

        List<Map<String,Object>> totalShipping=new ArrayList<>();
        consignment.put("selectedProducts",productInfo);
        consignment.put("shippingDetails",totalShipping);
        consignment.put("userType",properties.getProperty("userType"));

        List<Map<String,Object>> warehouseList=new ArrayList<>();
        Map<String,Object> warehouseMap=new HashMap<>();
        List<String> productList=new ArrayList<>();

        productList.add("BLF-60033-00001-00001");
        warehouseMap.put("selectedProducts",productList);
        warehouseMap.put("warehouseAddress",properties.getProperty("warehouseAddress"));
        warehouseMap.put("warehouseCode",properties.getProperty("warehouseCode"));
        warehouseMap.put("warehouseName",properties.getProperty("warehouseName"));
        warehouseMap.put("warehouseRank",properties.getProperty("warehouseRank"));
        warehouseList.add(warehouseMap);
        consignment.put("warehouseAllocations",warehouseList);

        objectMapper=new ObjectMapper();
        String MapToJson=objectMapper.writeValueAsString(consignment);
        response = requestSpecification.body(MapToJson)
                .when()
                .post(endPoint)
                .then()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 ")
                .log().all()
                .extract().response();

        draftNumber=response.jsonPath().get("data");
    }

    @Then("the user should not get any errors in error field")
    public void theUserShouldGetAnyErrorsInErrorField() {
        Assert.assertEquals(response.jsonPath().getMap("errors").size(),0,"There is some error in error field");
    }

    @When("the user hits the api with get method with endpoint {string} with parameter {string}")
    public void theUserHitsTheApiWithGetMethodWithEndpointWithParameters(String endPoint, String businessPartnerCode) {
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
                .statusCode(200)
                .statusLine("HTTP/1.1 200 ")
                .log().all()
                .extract().response();
    }

    @Then("the user should get the correct details")
    public void theUserShouldGetTheCorrectDetails() {
        Assert.assertEquals(response.jsonPath().get("data.draftNumber"),draftNumber,"DraftNumber mismatch");
        Assert.assertEquals(response.jsonPath().get("data.merchantCode"),consignment.get("merchantCode").toString());
        Assert.assertEquals(response.jsonPath().get("data.merchantName"),consignment.get("merchantName").toString());
   }

    @Then("the user should get no data with error message {string}")
    public void theUserShouldGetNoDataWithErrorMessage(String message) {
        Assert.assertTrue(response.jsonPath().get("data").toString().contains(message));
    }

    @And("the user should see the product details")
    public void theUserShouldSeeTheProductDetails() {
        Assert.assertTrue(productList.length!=0,"Product details are not present in the response");
        Assert.assertTrue(!productList[0].getProductName().isEmpty(),"Product name is not present in response");
        Assert.assertTrue(!productList[0].getItemCode().isEmpty(),"Item code is not present in response");
        Assert.assertEquals(paging.getItem_per_page(),10,"Total items per page");
    }
}
