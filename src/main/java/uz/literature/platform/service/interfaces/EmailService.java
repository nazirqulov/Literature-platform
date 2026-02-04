package uz.literature.platform.service.interfaces;

public interface EmailService {

    void sendEmail(String to, String code, String subject, String text);
}
