import java.util.*;

// Singleton Pattern
class AccountManager {
    private static AccountManager instance;
    private Map<String, Account> accounts;

    private AccountManager() {
        accounts = new HashMap<>();
    }

    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

    public void registerAccount(Account account) {
        accounts.put(account.getId(), account);
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }
}

class Account {
    private String id;
    private String username;

    public Account(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}

// Factory Pattern
interface BillFactory {
    Bill createBill(double amount, List<Account> members);
}

class EvenBillFactory implements BillFactory {
    public Bill createBill(double amount, List<Account> members) {
        return new EvenBill(amount, members);
    }
}

class CustomBillFactory implements BillFactory {
    public Bill createBill(double amount, List<Account> members) {
        return new CustomBill(amount, members);
    }
}

// Observer Pattern
interface ChangeListener {
    void onUpdate();
}

class ExpenseChangeListener implements ChangeListener {
    public void onUpdate() {
        // Update logic for expenses...
        System.out.println("Expense data updated.");
    }
}

// Strategy Pattern
interface DivisionStrategy {
    Map<Account, Double> divideBill(double amount, List<Account> members);
}

class EvenSplitStrategy implements DivisionStrategy {
    public Map<Account, Double> divideBill(double amount, List<Account> members) {
        Map<Account, Double> allocations = new HashMap<>();
        double share = amount / members.size();
        for (Account member : members) {
            allocations.put(member, share);
        }
        return allocations;
    }
}

class WeightedSplitStrategy implements DivisionStrategy {
    public Map<Account, Double> divideBill(double amount, List<Account> members) {
        // Custom splitting logic based on user-defined weights
        return new EvenSplitStrategy().divideBill(amount, members); // Using even split for simplicity
    }
}

// Command Pattern
interface TransactionCommand {
    void execute();
}

class CreateExpenseCommand implements TransactionCommand {
    private Bill bill;

    public CreateExpenseCommand(Bill bill) {
        this.bill = bill;
    }

    public void execute() {
        // Execute add expense logic...
        bill.computeShares();
    }
}

// Facade Pattern
class ExpenseTracker {
    private AccountManager accountManager;
    private List<Bill> bills;
    private List<ChangeListener> listeners;

    public ExpenseTracker() {
        accountManager = AccountManager.getInstance();
        bills = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void registerListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void registerAccount(Account account) {
        accountManager.registerAccount(account);
    }

    public void createEvenExpense(double amount, List<Account> members) {
        BillFactory factory = new EvenBillFactory();
        Bill bill = factory.createBill(amount, members);
        bills.add(bill);

        notifyListeners();
    }

    private void notifyListeners() {
        for (ChangeListener listener : listeners) {
            listener.onUpdate();
        }
    }
}

// Expense Classes
abstract class Bill {
    protected double amount;
    protected List<Account> members;
    protected Map<Account, Double> allocations;
    protected DivisionStrategy strategy;

    public Bill(double amount, List<Account> members, DivisionStrategy strategy) {
        this.amount = amount;
        this.members = members;
        this.strategy = strategy;
    }

    public abstract void computeShares();
}

class EvenBill extends Bill {
    public EvenBill(double amount, List<Account> members) {
        super(amount, members, new EvenSplitStrategy());
    }

    public void computeShares() {
        allocations = strategy.divideBill(amount, members);
    }
}

class CustomBill extends Bill {
    public CustomBill(double amount, List<Account> members) {
        super(amount, members, new WeightedSplitStrategy());
    }

    public void computeShares() {
        allocations = strategy.divideBill(amount, members);
    }
}

// Main Class
public class SplitwiseApp {
    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();

        Account account1 = new Account("1", "Alice");
        Account account2 = new Account("2", "Bob");

        tracker.registerAccount(account1);
        tracker.registerAccount(account2);

        tracker.registerListener(new ExpenseChangeListener());

        tracker.createEvenExpense(150.0, Arrays.asList(account1, account2));
    }
}
