package uz.literature.platform.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.literature.platform.payload.response.RecaptchaResponse;

/**
 * Created by: Barkamol
 * DateTime: 2/14/2026 12:49 PM
 */
@Service
public class RecaptchaService {
    @Value("${recaptcha.secret}")
    private String secret;

    @Value("${recaptcha.secret.url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isValid(String token, String ip) {
        String url = verifyUrl +
                "?secret=" + secret +
                "&response=" + token +
                "&remoteip=" + ip;

        RecaptchaResponse response =
                restTemplate.postForObject(url, null, RecaptchaResponse.class);

        return response != null
                && response.isSuccess()
                && response.getScore() >= 0.5; 
    }
}
