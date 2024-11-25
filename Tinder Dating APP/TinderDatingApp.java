package TinderDatingApp;
import java.time.LocalDateTime;
import java.util.*;

// User class
class User {
    private String userId;
    private String username;
    private String password;
    private String bio;
    private List<String> pictures;
    private List<Message> receivedMessages;

    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.pictures = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void receiveMessage(Message message) {
        receivedMessages.add(message);
    }
}

// Match class
class Match {
    private User user1;
    private User user2;
    private LocalDateTime matchTime;

    public Match(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.matchTime = LocalDateTime.now();
    }

    public LocalDateTime getMatchTime() {
        return matchTime;
    }
}

// Message class
class Message {
    private String messageId;
    private User sender;
    private User receiver;
    private String content;
    private LocalDateTime timestamp;

    public Message(String messageId, User sender, User receiver, String content) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public User getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}

// Location class
class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

// PresenceManager class (Singleton)
class PresenceManager {
    private static PresenceManager instance;
    private Map<User, Boolean> presenceMap;
    private List<PresenceObserver> observers;

    private PresenceManager() {
        this.presenceMap = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    public static synchronized PresenceManager getInstance() {
        if (instance == null) {
            instance = new PresenceManager();
        }
        return instance;
    }

    public void setPresence(User user, boolean online) {
        presenceMap.put(user, online);
        notifyObservers(user, online);
    }

    public void addObserver(PresenceObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PresenceObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(User user, boolean online) {
        for (PresenceObserver observer : observers) {
            observer.onPresenceChange(user, online);
        }
    }
}

// PresenceObserver interface
interface PresenceObserver {
    void onPresenceChange(User user, boolean online);
}

// MessagingSystem class (Publish-Subscribe)
class MessagingSystem {
    private Map<String, List<Message>> messageHistory;
    private Map<User, List<String>> userChannels;

    public MessagingSystem() {
        this.messageHistory = new HashMap<>();
        this.userChannels = new HashMap<>();
    }

    public void subscribe(User user, String channel) {
        List<String> channels = userChannels.computeIfAbsent(user, k -> new ArrayList<>());
        channels.add(channel);
    }

    public void unsubscribe(User user, String channel) {
        List<String> channels = userChannels.get(user);
        if (channels != null) {
            channels.remove(channel);
        }
    }

    public void publishMessage(Message message, String channel) {
        List<Message> channelMessages = messageHistory.computeIfAbsent(channel, k -> new ArrayList<>());
        channelMessages.add(message);
        sendMessageToSubscribers(message, channel);
    }

    private void sendMessageToSubscribers(Message message, String channel) {
        for (Map.Entry<User, List<String>> entry : userChannels.entrySet()) {
            User user = entry.getKey();
            List<String> channels = entry.getValue();
            if (channels.contains(channel) && !user.equals(message.getSender())) {
                user.receiveMessage(message);
            }
        }
    }
}

// MatchingAlgorithm interface (Strategy)
interface MatchingAlgorithm {
    List<User> findPotentialMatches(User user, List<User> users);
}

// SimpleMatchingAlgorithm class (Strategy)
class SimpleMatchingAlgorithm implements MatchingAlgorithm {
    @Override
    public List<User> findPotentialMatches(User user, List<User> users) {
        // Simplified matching logic
        return users;
    }
}

// SwipeState interface (State)
interface SwipeState {
    void handleSwipe(User user, User profile, boolean isSwipeRight);
}

// BrowsingState class (State)
class BrowsingState implements SwipeState {
    @Override
    public void handleSwipe(User user, User profile, boolean isSwipeRight) {
        if (isSwipeRight) {
            System.out.println(user.getUsername() + " swiped right on " + profile.getUsername());
        } else {
            System.out.println(user.getUsername() + " swiped left on " + profile.getUsername());
        }
    }
}

// SwipeManager class
class SwipeManager {
    private SwipeState currentState;

    public SwipeManager() {
        this.currentState = new BrowsingState();
    }

    public void handleSwipe(User user, User profile, boolean isSwipeRight) {
        currentState.handleSwipe(user, profile, isSwipeRight);
    }
}

// SubscriptionPlan class
class SubscriptionPlan {
    private String planId;
    private String name;
    private double price;

    public SubscriptionPlan(String planId, String name, double price) {
        this.planId = planId;
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

// SubscriptionManager class
class SubscriptionManager {
    private List<SubscriptionPlan> subscriptionPlans;

    public SubscriptionManager() {
        this.subscriptionPlans = new ArrayList<>();
    }

    public void addSubscriptionPlan(SubscriptionPlan plan) {
        subscriptionPlans.add(plan);
    }

    public List<SubscriptionPlan> getSubscriptionPlans() {
        return Collections.unmodifiableList(subscriptionPlans);
    }
}

// Main Class
public class TinderDatingApp {
    public static void main(String[] args) {
        // Create users
        User user1 = new User("user1", "John", "password1");
        User user2 = new User("user2", "Alice", "password2");

        // Set user profiles
        user1.setBio("Hi, I'm John!");
        user2.setBio("Hello, I'm Alice!");
        user1.getPictures().add("john_pic.jpg");
        user2.getPictures().add("alice_pic.jpg");

        // Matching algorithm
        MatchingAlgorithm matchingAlgorithm = new SimpleMatchingAlgorithm();
        List<User> potentialMatches = matchingAlgorithm.findPotentialMatches(user1, List.of(user2));

        System.out.println("Potential Matches for " + user1.getUsername() + ":");
        for (User match : potentialMatches) {
            System.out.println("- " + match.getUsername());
        }

        // Swipe manager
        SwipeManager swipeManager = new SwipeManager();
        swipeManager.handleSwipe(user1, user2, true);

        // Messaging system
        MessagingSystem messagingSystem = new MessagingSystem();
        messagingSystem.subscribe(user1, "channel1");
        messagingSystem.publishMessage(new Message("msg1", user1, user2, "Hi Alice!"), "channel1");

        // Subscription manager
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        subscriptionManager.addSubscriptionPlan(new SubscriptionPlan("plan1", "Gold Plan", 29.99));
        subscriptionManager.addSubscriptionPlan(new SubscriptionPlan("plan2", "Platinum Plan", 49.99));
        for (SubscriptionPlan plan : subscriptionManager.getSubscriptionPlans()) {
            System.out.println("Subscription Plan: " + plan.getName() + " - $" + plan.getPrice());
        }
    }
}
