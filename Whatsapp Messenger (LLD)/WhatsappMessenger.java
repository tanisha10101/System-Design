package WhatsappMessenger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// User class
class User {
    private String userId;
    private String username;
    private String password;

    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

// Message class
class Message {
    private String messageId;
    private User sender;
    private List<User> receivers;
    private String content;
    private LocalDateTime timestamp;
    private boolean isEncrypted;
    private boolean isDelivered;
    private boolean isRead;

    public Message(String messageId, User sender, List<User> receivers, String content) {
        this.messageId = messageId;
        this.sender = sender;
        this.receivers = receivers;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isEncrypted = false;
        this.isDelivered = false;
        this.isRead = false;
    }

    public String getMessageId() {
        return messageId;
    }

    public User getSender() {
        return sender;
    }

    public List<User> getReceivers() {
        return receivers;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}

// MessageManager class
class MessageManager {
    private List<Message> sentMessages;
    private List<Message> receivedMessages;

    public MessageManager() {
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
    }

    public void sendMessage(Message message) {
        sentMessages.add(message);
        message.setDelivered(true);
    }

    public void receiveMessage(Message message) {
        receivedMessages.add(message);
        System.out.println("Message received: " + message.getContent());
    }

    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }
}

// MessageSearchStrategy interface
interface MessageSearchStrategy {
    List<Message> searchMessages(List<Message> messages, String keyword);
}

// KeywordSearchStrategy class
class KeywordSearchStrategy implements MessageSearchStrategy {
    @Override
    public List<Message> searchMessages(List<Message> messages, String keyword) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message.getContent().contains(keyword)) {
                result.add(message);
            }
        }
        return result;
    }
}

// SearchManager class
class SearchManager {
    private MessageSearchStrategy searchStrategy;

    public void setSearchStrategy(MessageSearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public List<Message> searchMessages(List<Message> messages, String keyword) {
        if (searchStrategy == null) {
            throw new IllegalStateException("Search strategy is not set.");
        }
        return searchStrategy.searchMessages(messages, keyword);
    }
}


// MessageEncryptionDecorator class
class MessageEncryptionDecorator extends Message {
    private Message message;

    public MessageEncryptionDecorator(Message message) {
        super(message.getMessageId(), message.getSender(), message.getReceivers(), message.getContent());
        this.message = message;
    }

    @Override
    public String getContent() {
        return encrypt(message.getContent());
    }

    private String encrypt(String content) {
        // Simple encryption logic for demonstration (reverse the string)
        return new StringBuilder(content).reverse().toString();
    }

    @Override
    public boolean isEncrypted() {
        return true;
    }
}

// Main class
public class WhatsappMessenger {
    public static void main(String[] args) {
        // Create users
        User user1 = new User("user1", "john.doe", "password1");
        User user2 = new User("user2", "alice.smith", "password2");

        // Create message objects
        Message message1 = new Message("msg1", user1, List.of(user2), "Hi Alice, how are you?");
        Message message2 = new Message("msg2", user2, List.of(user1), "Hi John, I'm doing well.");

        // Create message manager
        MessageManager messageManager = new MessageManager();

        // Receive messages
        messageManager.receiveMessage(message1);
        messageManager.receiveMessage(message2);

        // Search messages
        SearchManager searchManager = new SearchManager();
        searchManager.setSearchStrategy(new KeywordSearchStrategy());
        List<Message> searchResults = searchManager.searchMessages(messageManager.getReceivedMessages(), "John");

        // Print search results
        System.out.println("Search Results:");
        for (Message message : searchResults) {
            System.out.println(message.getContent());
        }

        // Add message encryption using decorators
        Message encryptedMessage = new MessageEncryptionDecorator(message2);
        System.out.println("Encrypted content: " + encryptedMessage.getContent());
    }
}
