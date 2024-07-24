package io.quarkiverse.azure.keyvault.secret.runtime.config;

import com.azure.core.util.ClientOptions;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecretIdentifier;
import io.quarkiverse.azure.core.util.AzureQuarkusIdentifier;
import io.quarkiverse.azure.keyvault.secret.runtime.KeyVaultSecretConfig;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.common.AbstractConfigSource;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@StaticInitSafe
public class KeyVaultSecretConfigSource extends AbstractConfigSource {
    /** The ordinal is set to < 100 (which is the default) so that this config source is retrieved from last. */
    private static final int KEYVAULT_SECRET_ORDINAL = 50;

    private static final String CONFIG_SOURCE_NAME = "io.quarkiverse.azure.keyvault.secret.runtime.config";

    private final KeyVaultSecretConfig kvConfig;

    public KeyVaultSecretConfigSource(final KeyVaultSecretConfig kvConfig) {
        super(CONFIG_SOURCE_NAME, KEYVAULT_SECRET_ORDINAL);
        this.kvConfig = kvConfig;
    }

    private static SecretClient createClient(String vaultUrl){
        return new SecretClientBuilder()
                .clientOptions(new ClientOptions().setApplicationId(AzureQuarkusIdentifier.AZURE_QUARKUS_KEY_VAULT_SYNC_CLIENT))
                .vaultUrl(vaultUrl)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getPropertyNames() {
        return Collections.emptySet();
    }

    @Override
    public String getValue(String propertyName) {
        KeyVaultSecretIdentifier secretIdentifier = KeyVaultSecretConfigUtil.getSecretIdentifier(propertyName, kvConfig);
        if (secretIdentifier == null) {
            // The propertyName is not in the form "${kv//...}" so return null.
            return null;
        }

        SecretClient client = createClient(secretIdentifier.getVaultUrl());
        return client.getSecret(secretIdentifier.getName(), secretIdentifier.getVersion()).getValue();
    }
}
