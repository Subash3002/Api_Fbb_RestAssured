Feature: Api Testing for Fbb
  Scenario: Getting product details
    Given the user access to "http://fbb-core.qa2-sg.cld/api"
    When the user hits the api with get method with endpoint "/v3/products/revamp" with parameters "BLF-60033" and "BLF-60033-00001-00001"
    Then the user should get response 200
    And the user should see the product details

  Scenario:Adding a new consignment
    Given the user access to "http://fbb-core.qa2-sg.cld/api"
    When the user hits the api with post method with endpoint "/v1/draft/consignment/save-draft"
    Then the user should get response 200
    And the user should not get any errors in error field

  Scenario:Getting the consignment
    Given the user access to "http://fbb-core.qa2-sg.cld/api"
    When the user hits the api with get method with endpoint "/v1/draft/consignment" with parameter "BLF-60033"
    Then the user should get response 200
    And the user should get the correct details


  Scenario: Deleting a existing consignment
    Given the user access to "http://fbb-core.qa2-sg.cld/api"
    When the user hits the api with delete method with endpoint "v1/draft/consignment/delete" with parameter "BLF-60033"
    Then the user should get response 200
    When the user hits the api with get method with endpoint "/v1/draft/consignment" with parameter "BLF-60033"
    Then the user should get response 400
    And the user should get no data with error message "No draft found for merchant code"



