package com.example;

// Import the necessary concurrency classes
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public class App implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final gemini_generation gemini = new gemini_generation();

    // 1. Create an ExecutorService.
    // A cached thread pool is a good choice here: it creates new threads as needed
    // but will reuse previously constructed threads when they are available.
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public App(String botToken) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

// In your main method inside App.java

    public static void main(String[] args) {
        // Read the bot token from an environment variable
        String botToken = System.getenv("TELEGRAM_BOT_TOKEN");

        if (botToken == null) {
            System.err.println("Error: TELEGRAM_BOT_TOKEN environment variable not set.");
            System.exit(1); // Exit if the token isn't found
        }

        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(botToken, new App(botToken));
            System.out.println("Bot is running...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Get user info on the main listener thread. This is fast.
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "We are handling your request...😊");
            // 2. Submit the slow task to our thread pool.
            // The code inside the lambda () -> { ... } will run on a separate worker thread.
            executor.submit(() -> {
                try {
                    Message sentMessage = telegramClient.execute(sendMessage);

                    // 2. Get the ID from that Message object
                    Integer message_Id = sentMessage.getMessageId();
                    // This is the slow part. It no longer blocks the main listener.
                    String responseText = gemini.Generate_content(messageText, chatId);
                    EditMessageText newMessage = EditMessageText.builder().chatId(chatId).messageId(message_Id).text(responseText).build();
                    // Create and send the message from the worker thread.
                    telegramClient.execute(newMessage);
                } catch (Exception e) {
                    // It's good practice to catch exceptions within the thread's task.
                    System.err.println("An error occurred in a worker thread.");
                    SendMessage send_error_Message = new SendMessage(String.valueOf(chatId), e.toString());
                    
                    e.printStackTrace();
                }
            });
        }
    }
}