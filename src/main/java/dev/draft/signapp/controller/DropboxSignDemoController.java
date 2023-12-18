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

        String hsApiKey = env.getProperty("HS_API_KEY");

        var callbackEvent = EventCallbackRequest.init(json);

        boolean validRequest = EventCallbackHelper.isValid(hsApiKey, callbackEvent);

        EventCallbackRequestEvent eventPayload = callbackEvent.getEvent();

        if (validRequest) {
            switch (EventCallbackHelper.getCallbackType(callbackEvent)) {
                case "account_callback":
                    logger.info("Account Callback called " + eventPayload);
                    break;
                case "signature_request_sent":
                    logger.info("Signature request sent " + eventPayload);
                    break;
                case "signature_request_all_signed":
                        logger.info("Signature request signed " + eventPayload);
                        break;
                default:
                    logger.info("DS event occured " + EventCallbackHelper.getCallbackType(callbackEvent));
                    break;
            }
        }

        return "Dropbox Sign API Event Received";
    }

}
