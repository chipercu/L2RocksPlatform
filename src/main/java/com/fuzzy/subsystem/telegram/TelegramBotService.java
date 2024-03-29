package com.fuzzy.subsystem.telegram;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;

import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.ssl.SSLContexts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class TelegramBotService
//        extends TelegramLongPollingBot implements CallBackConst
{
//    private CloseableHttpClient telegramClient;
//    private TelegramBot telegramBot;
//    private final JavaMailSender emailSender;
//    private static final int defaultInfoMessageTime = 2000;
//    private final Map<Long, Integer> lastMessageMap = new HashMap<>();

    public TelegramBotService() {
//        telegramClient = init();
//        List<BotCommand> listofCommands = new ArrayList<>();
//        listofCommands.add(new BotCommand("/help", "Помощь по навигации и настройке бота"));
//        try {
//            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
//        } catch (TelegramApiException e) {
//            System.out.println(e.getMessage());
//        }
//        this.emailSender = new JavaMailSenderImpl();
    }

    private CloseableHttpClient client(){
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        SSLConnectionSocketFactory sslConnection;
        try {
            sslConnection = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(SSLContexts.custom()
                            .loadTrustMaterial(null, new TrustAllStrategy())
                            .build())
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
        httpClientBuilder.setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslConnection)
                .build());
        return httpClientBuilder.build();
    }

    public void sendMessage(String charId, String message) {
        if (TelegramBotConfig.isEnabled){
            HttpPost httpPost = new HttpPost("https://api.telegram.org/bot" + TelegramBotConfig.getToken() + "/sendMessage");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("chat_id", charId));
            nameValuePairs.add(new BasicNameValuePair("text", message));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
            try (CloseableHttpClient httpClient = client()) {
                String response = httpClient.execute(httpPost, new BasicHttpClientResponseHandler());
                if(response == null) throw new RuntimeException("Message response is null");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(List<String> charsIds, String message) {
        for (String charId: charsIds){
            sendMessage(charId, message);
        }
    }




//    @Override
//    public String getBotUsername() {
//        return TelegramBotConfig.getBotName();
//    }
//
//    @Override
//    public String getBotToken() {
//        return TelegramBotConfig.getToken();
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//
//        if (update.hasCallbackQuery()) {
//            long chatId = update.getCallbackQuery().getMessage().getChatId();
//            CallbackQuery callbackQuery = update.getCallbackQuery();
//            final String data = callbackQuery.getData();
//            final long eventId = Long.parseLong(data.split(":")[1]);
//            final TGUtil tgUtil = new TGUtil();
//            if (data.startsWith(SEE_EVENT)) {
////                getEvent(callbackQuery, eventsService.getEventById(eventId), chatId);
//            } else if (data.startsWith(MENU)) {
//                TGMenu.showMenu(callbackQuery);
//            }
//
//            else if (data.startsWith(REGISTER)) {
////                final User newUser = new User(chatId, false);
////                userCreateMap.put(callbackQuery.getMessage().getChatId(), newUser);
//                lastMessageMap.put(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
//            } else if (data.startsWith(CANCEL_REGISTRY)) {
//                lastMessageMap.remove(chatId);
//            } else if (data.startsWith(CONFIRM_REGISTRY)) {
//            } else if (data.startsWith(DELETE_USER)) {
////                UserSettings.deleteAccount(callbackQuery);
//            } else if (data.startsWith(CONFIRM_DELETE_USER)) {
////                UserSettings.deleteUser(callbackQuery);
//            }
//
//            else if (data.startsWith(SHOW_ALL_EVENT)) {
//            } else if (data.startsWith(REG_TO_EVENT)) {
//            } else if (data.startsWith(UNREG_TO_EVENT)) {
//            } else if (data.startsWith(REFRESH_EVENT)) {
//                loading(chatId);
////                getEvent(callbackQuery, eventsService.getEventById(eventId), chatId);
//            }
//            else if (data.startsWith(USER_SETTINGS)) {
//                UserSettings.showUserSettings(callbackQuery);
//            }
//            else if (data.startsWith(MAIL)) {
////                Executors.newSingleThreadScheduledExecutor().schedule(() -> {
////                    SimpleMailMessage message = new SimpleMailMessage();
////                    message.setFrom("chipercualexandru@mail.ru");
////                    message.setTo("a.kiperku@infomaximum.com");
////                    message.setSubject("subject");
////                    message.setText("Вы получили данное сообщение от сервиса IM.EVENTS \n при  регистрации аккаунта \n Ваш код подтверждения : 545454");
////                    emailSender.send(message);
////                }, 100, TimeUnit.MILLISECONDS);
//
//            }
//        }
//
//
//        if (update.hasMessage() && update.getMessage().hasText()) {
//
//            final Message updateMessage = update.getMessage();
//            long chatId = update.getMessage().getChatId();
//            switch (updateMessage.getText()) {
//                case "/help":
//                case "/start":
////                    userCreateMap.remove(chatId);
//                    TGMenu.showMenu(chatId);
//                    break;
//            }
//        }
//    }
//
//    public void infoMessage(long chatId, String text) {
//        try {
//            SendMessage message = new SendMessage();
//            message.setChatId(chatId);
//            message.setText(text);
//            Message sentOutMessage = execute(message);
//            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
//                try {
//                    DeleteMessage deleteMessage = new DeleteMessage();
//                    deleteMessage.setChatId(sentOutMessage.getChatId());
//                    deleteMessage.setMessageId(sentOutMessage.getMessageId());
//                    execute(deleteMessage);
//                } catch (TelegramApiException e) {
//                    throw new RuntimeException(e);
//                }
//            }, defaultInfoMessageTime, TimeUnit.MILLISECONDS);
//
//        } catch (TelegramApiException ignored) {
//
//        }
//    }
//
//    public void loading(long chatId) {
//        try {
//            final String[] text = {" *"};
//            SendMessage message = new SendMessage();
//            message.setChatId(chatId);
//            message.setText(text[0]);
//            Message sentOutMessage = execute(message);
//
//            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//                EditMessageText editMessageText = new EditMessageText();
//                editMessageText.setChatId(chatId);
//                editMessageText.setMessageId(sentOutMessage.getMessageId());
//                text[0] = text[0] + " *";
//                editMessageText.setText(text[0]);
//                executeMessage(editMessageText);
//            }, 0, 100, TimeUnit.MILLISECONDS);
//
//            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
//                DeleteMessage deleteMessage = new DeleteMessage();
//                deleteMessage.setChatId(sentOutMessage.getChatId());
//                deleteMessage.setMessageId(sentOutMessage.getMessageId());
//                try {
//                    execute(deleteMessage);
//                } catch (TelegramApiException e) {
//                    throw new RuntimeException(e);
//                }
//            }, 3000, TimeUnit.MILLISECONDS);
//
//        } catch (TelegramApiException ignored) {
//
//        }
//    }
//
//    public void executeMessage(CallbackQuery callbackQuery, EditMessageText message) {
//        try {
//            execute(message);
//        } catch (TelegramApiException ignored) {
//        }
//    }
//    public void executeMessage(EditMessageText message) {
//        try {
//            execute(message);
//        } catch (TelegramApiException ignored) {
//        }
//    }
//
//    public void executeMessage(SendMessage message) {
//        try {
//            execute(message);
//        } catch (TelegramApiException ignored) {
//        }
//    }
//
//    public void sendMessage(Long chatId, String textToSend) {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(String.valueOf(chatId));
//        sendMessage.setText(textToSend);
//        try {
//            execute(sendMessage);
//        } catch (TelegramApiException ignored) {
//        }
//    }
}
