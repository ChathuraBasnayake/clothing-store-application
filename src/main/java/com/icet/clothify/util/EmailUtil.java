package com.icet.clothify.util;

import com.icet.clothify.controller.MakeOrderController;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.concurrent.Task;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class EmailUtil {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SENDER_EMAIL = dotenv.get("SENDER_EMAIL");
    private static final String SENDER_PASSWORD = dotenv.get("SENDER_PASSWORD");

    public static Task<Void> createSendEmailTask(String recipientEmail, String subject, String body) {
        return new Task<>() {
            @Override
            protected Void call() throws MessagingException {
                // This part runs on the background thread
                updateMessage("Setting up email properties...");

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                    }
                });

                updateMessage("Constructing email...");
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject(subject);
                message.setContent(body, "text/html; charset=utf-8");

                updateMessage("Sending email...");
                Transport.send(message);

                updateMessage("Email sent successfully!");
                return null;
            }
        };
    }

    public static String generateOrderConfirmationEmailBody(String orderId, List<MakeOrderController.OrderItem> items, String customerEmail) {
        StringBuilder sb = new StringBuilder();
        double grandTotal = 0.0;

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "LK")); // For LKR currency

        sb.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        sb.append("<style>");
        sb.append("body {font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4;}");
        sb.append(".container {width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); overflow: hidden;}");
        sb.append(".header {background-color: #4A90E2; color: white; padding: 20px; text-align: center;}");
        sb.append(".header h1 {margin: 0; font-size: 24px;}");
        sb.append(".content {padding: 30px;}");
        sb.append(".content h2 {color: #333333;}");
        sb.append(".order-summary, .items-table {width: 100%; border-collapse: collapse; margin-bottom: 20px;}");
        sb.append(".order-summary td {padding: 8px 0; border-bottom: 1px solid #eeeeee;}");
        sb.append(".items-table th, .items-table td {padding: 12px; border-bottom: 1px solid #eeeeee; text-align: left;}");
        sb.append(".items-table th {background-color: #f9f9f9; color: #555555; font-weight: bold;}");
        sb.append(".items-table .text-right {text-align: right;}");
        sb.append(".total-row td {font-weight: bold; font-size: 18px; border-top: 2px solid #333333; padding-top: 15px;}");
        sb.append(".footer {background-color: #333333; color: #bbbbbb; text-align: center; padding: 20px; font-size: 12px;}");
        sb.append(".footer a {color: #4A90E2; text-decoration: none;}");
        sb.append("</style></head><body>");


        sb.append("<div class=\"container\">");


        sb.append("<div class=\"header\"><h1>Clothify</h1></div>");

        sb.append("<div class=\"content\">");
        sb.append("<h2>Thank You For Your Order!</h2>");
        sb.append("<p>Hello,</p><p>We've received your order and it is now being processed. Here are the details:</p>");


        sb.append("<table class=\"order-summary\">");
        sb.append("<tr><td><strong>Order ID:</strong></td><td class=\"text-right\">").append(orderId).append("</td></tr>");
        sb.append("<tr><td><strong>Order Date:</strong></td><td class=\"text-right\">").append(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).append("</td></tr>");
        sb.append("<tr><td><strong>Email:</strong></td><td class=\"text-right\">").append(customerEmail).append("</td></tr>");
        sb.append("</table>");

        sb.append("<h3>Order Details</h3>");
        sb.append("<table class=\"items-table\">");
        sb.append("<thead><tr><th>Item</th><th>Quantity</th><th class=\"text-right\">Unit Price</th><th class=\"text-right\">Total</th></tr></thead>");
        sb.append("<tbody>");

        for (MakeOrderController.OrderItem item : items) {
            sb.append("<tr>");
            sb.append("<td>").append(item.getName()).append("</td>");
            sb.append("<td>").append(item.getQty()).append("</td>");
            sb.append("<td class=\"text-right\">").append(currencyFormatter.format(item.getPrice())).append("</td>");
            sb.append("<td class=\"text-right\">").append(currencyFormatter.format(item.getTotal())).append("</td>");
            sb.append("</tr>");
            grandTotal += item.getTotal();
        }

        sb.append("</tbody>");
        sb.append("<tfoot>");

        sb.append("<tr class=\"total-row\"><td colspan=\"3\">GRAND TOTAL</td><td class=\"text-right\">").append(currencyFormatter.format(grandTotal)).append("</td></tr>");
        sb.append("</tfoot></table>");

        sb.append("<p>Thank you for shopping with us. We hope to see you again soon!</p>");
        sb.append("</div>");


        sb.append("<div class=\"footer\">");
        sb.append("<p>&copy; ").append(LocalDate.now().getYear()).append(" Clothify Inc. All Rights Reserved.</p>");
        sb.append("<p>123 Fashion Ave, Colombo, Sri Lanka</p>");
        sb.append("<p><a href=\"#\">Visit Our Store</a> | <a href=\"#\">Contact Us</a></p>");
        sb.append("</div>");

        sb.append("</div></body></html>");

        return sb.toString();
    }

}