package dev.draft.signapp.service;

import com.dropbox.sign.ApiException;
import com.dropbox.sign.Configuration;
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
            String dsTemplateId = env.getProperty("DS_TEMPLATE_ID");
            String signerName = env.getProperty("SIGNER_NAME");
            String signerEmail = env.getProperty("SIGNER_EMAIL");
            String signerRole = env.getProperty("SIGNER_ROLE");

            var apiClient = Configuration.getDefaultApiClient();

            // Configure HTTP basic authorization: api_key
            var apiKey = (HttpBasicAuth) apiClient
                    .getAuthentication("api_key");
            apiKey.setUsername(dsApiKey);

            var signatureRequestApi = new SignatureRequestApi(apiClient);

            var signer = new SubSignatureRequestTemplateSigner()
                    .role(signerRole)
                    .name(signerName)
                    .emailAddress(signerEmail);


            var data = new SignatureRequestCreateEmbeddedWithTemplateRequest()
                    .templateIds(List.of(dsTemplateId))
                    .signers(List.of(signer))
                    .testMode(true);


            try {
                SignatureRequestGetResponse result = signatureRequestApi.signatureRequestCreateEmbeddedWithTemplate(data);

                return result.getSignatureRequest().getSigningUrl();
            } catch (ApiException e) {
                System.err.println("Exception when calling AccountApi#accountCreate");
                System.err.println("Status code: " + e.getCode());
                System.err.println("Reason: " + e.getResponseBody());
                System.err.println("Response headers: " + e.getResponseHeaders());
                e.printStackTrace();

                return "Something went wrong";
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
}
