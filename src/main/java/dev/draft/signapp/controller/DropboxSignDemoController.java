package dev.draft.signapp.controller;

import com.dropbox.sign.EventCallbackHelper;
import com.dropbox.sign.model.EventCallbackRequest;
import com.dropbox.sign.model.EventCallbackRequestEvent;
import dev.draft.signapp.service.DropboxSignDemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DropboxSignDemoController {

    private final Logger logger = LoggerFactory.getLogger(DropboxSignDemoController.class);

    @Autowired
    private Environment env;

    @Autowired
    DropboxSignDemoService dropboxSignDemoService;

    @PostMapping("/sign/embeddedsign")
    public String embeddedSignatureRequest() {
        return dropboxSignDemoService.sendEmbeddedSignatureRequest();
    }

    @PostMapping(value = "/sign/webhook")
    public String webhook(@RequestParam String json) throws Exception {

        String dsApiKey = env.getProperty("DS_API_KEY");

        var callbackEvent = EventCallbackRequest.init(json);

        boolean validRequest = EventCallbackHelper.isValid(dsApiKey, callbackEvent);

        EventCallbackRequestEvent eventPayload = callbackEvent.getEvent();

        if (validRequest) {
            switch (eventPayload.getEventType().toString()) {
                case "callback_test":
                    logger.info("Callback test payload received");
                    logger.info(eventPayload.toString());
                    break;
                case "signature_request_sent":
                    logger.info("Signature request sent");
                    break;
                case "signature_request_viewed":
                    logger.info("The signature request was viewed");
                    break;
                case "signature_request_signed":
                    logger.info("The signature request was signed");
                case "signature_request_all_signed":
                    logger.info("The signature request has been signed by all parties");
                    break;
                case "signature_request_downloadable":
                    logger.info("The signed document can now be downloaded");
                    break;
                default:
                    logger.info("DS event occurred: "+ eventPayload.getEventType());
                    break;
            }
        }

        return "Hello API Event Received";
    }

}
