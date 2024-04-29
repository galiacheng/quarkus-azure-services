package io.quarkiverse.azure.keyvault.secret.it;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
@DisabledIfSystemProperty(named = "azure.test", matches = "true")
class KeyVaultSecretResourceTest {

    @Test
    public void testSecretClient() {
        RestAssured.when().get("/keyvault/sync").then().body(is("Quarkus Azure Key Vault Extension is awsome"));
    }

    @Test
    public void testSecretAsyncClient() {
        RestAssured.when().get("/keyvault/async").then().body(is("Quarkus Azure Key Vault Extension is awsome"));
    }
}
