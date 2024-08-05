package io.quarkiverse.azure.keyvault.secret.runtime.config;

import com.azure.security.keyvault.secrets.models.KeyVaultSecretIdentifier;

public class KeyVaultSecretConfigUtil {
    private static final String AZURE_KEYVAULT_PREFIX = "kv//";
    private static final String AZURE_KEYVAULT_ENDPOINT_PREFIX = "https://";
    private static final String AZURE_VAULT_URL_FORMAT="https://%s.vault.azure.net/secrets/%s/%s";

    private KeyVaultSecretConfigUtil() {
    }

    static KeyVaultSecretIdentifier getSecretIdentifier(String input, String defaultEndpoint) {

        if (!input.startsWith(AZURE_KEYVAULT_PREFIX)) {
            return null;
        }

        String resourcePath = input.substring(AZURE_KEYVAULT_PREFIX.length());
        String[] tokens = resourcePath.split("/");

        String kvName = null;
        String secretName = null;
        String version = "latest";

        if (tokens.length == 1) {
            // property is form "kv//<secret-name>"
            kvName = getAzureKeyVaultName(defaultEndpoint);
            secretName = tokens[0];
        } else if (tokens.length == 2) {
            // property is form "kv//<kv-name>/<secret-name>"
            kvName = tokens[0];
            secretName = tokens[1];
        } else if (tokens.length == 3
                && tokens[1].equals("secrets")) {
            // property is form "kv//<kv-name>/secrets/<secret-name>"
            kvName = tokens[0];
            secretName = tokens[2];
        } else if (tokens.length == 3
                && tokens[1].equals("versions")) {
            // property is form "kv//<secret-name>}/versions/<version>"
            kvName = getAzureKeyVaultName(defaultEndpoint);
            secretName = tokens[0];
            version = tokens[2];
        } else if (tokens.length == 4
                && tokens[1].equals("secrets")) {
            // property is form "kv//<kv-name>/secrets/<secret-name>/<version>"
            kvName = tokens[0];
            secretName = tokens[2];
            version = tokens[3];
        } else if (tokens.length == 5
                && tokens[1].equals("secrets")
                && tokens[3].equals("versions")) {
            // property is form "kv//<kv-name>/secrets/<secret-name>/versions/<version>"
            kvName = tokens[0];
            secretName = tokens[2];
            version = tokens[4];
        } else {
            throw new IllegalArgumentException(
                    "Unrecognized format for specifying an Azure Key Vault secret: " + input);
        }

        if (kvName.isEmpty() || secretName.isEmpty() || version.isEmpty()) {
            throw new IllegalArgumentException("The provided Key Vault secret URI is invalid: " + input);
        }

        return new KeyVaultSecretIdentifier(
                String.format(AZURE_VAULT_URL_FORMAT, kvName, secretName, version));
    }

    static String getAzureKeyVaultName(String endpoint) {
        assert !endpoint.isEmpty() : "The endpoint of Azure Key Vault should be set.";
        assert endpoint.startsWith(AZURE_KEYVAULT_ENDPOINT_PREFIX)
                : "The endpoint of Azure Key Vault should start with https://.";
        return endpoint.substring(AZURE_KEYVAULT_ENDPOINT_PREFIX.length()).split("\\.")[0];
    }
}