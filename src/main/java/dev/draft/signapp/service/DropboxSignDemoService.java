package dev.draft.sign_app.service;

import com.dropbox.sign.Configuration;
import com.dropbox.sign.api.EmbeddedApi;
import com.dropbox.sign.api.SignatureRequestApi;
import com.dropbox.sign.auth.HttpBasicAuth;
import com.dropbox.sign.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DropboxSignDemoService {
    @Autowired
    private Environment env;

    public String sendEmbeddedSignatureRequest() {
        try {

            String dsApiKey = env.getProperty("DS_API_KEY");
            String dsClientId = env.getProperty("DS_CLIENT_ID");
            String dsTemplateId = env.getProperty("DS_TEMPLATE_ID");
            String signerName = env.getProperty("SIGNER_NAME");
            String signerEmail = env.getProperty("SIGNER_EMAIL");
            String signerRole = env.getProperty("SIGNER_ROLE");

            var apiClient = Configuration.getDefaultApiClient();

            var apiKey = (HttpBasicAuth) apiClient
                    .getAuthentication("api_key");
            apiKey.setUsername(dsApiKey);

            var signatureRequestApi = new SignatureRequestApi(apiClient);

            var signer = new SubSignatureRequestTemplateSigner()
                    .role(signerRole)
                    .name(signerName)
                    .emailAddress(signerEmail);

            var subSigningOptions = new SubSigningOptions()
                    .draw(true)
                    .type(true)
                    .upload(true)
                    .phone(false)
                    .defaultType(SubSigningOptions.DefaultTypeEnum.DRAW);


            var data = new SignatureRequestCreateEmbeddedWithTemplateRequest()
                    .clientId(dsClientId)
                    .templateIds(List.of(dsTemplateId))
                    .signers(List.of(signer))
                    .signingOptions(subSigningOptions)
                    .testMode(true);

            SignatureRequestGetResponse result = signatureRequestApi.signatureRequestCreateEmbeddedWithTemplate(data);

            var embeddedApi = new EmbeddedApi(apiClient);

            EmbeddedSignUrlResponse response = embeddedApi.embeddedSignUrl(result.getSignatureRequest().getSignatures().get(0).getSignatureId());

            return response.getEmbedded().getSignUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
