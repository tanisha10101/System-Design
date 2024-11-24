import java.util.*;

// Observer Pattern - Interface for Listening to File Changes
interface FileObserver {
    void onFileChange(String fileName);
}

// Observer Pattern - Editor Class Acting as a File Observer
class TextEditor implements FileObserver {
    private String text;

    public TextEditor(String text) {
        this.text = text;
    }

    @Override
    public void onFileChange(String fileName) {
        System.out.println("Notification: \"" + fileName + "\" has been updated.");
    }

    public void modifyText(String updatedText) {
        this.text = updatedText;
        System.out.println("Text content has been modified.");
    }

    public String getText() {
        return text;
    }
}

// Observer Pattern - File Manager for Handling Files and Observers
class FileController {
    private List<FileObserver> observers = new ArrayList<>();
    private Map<String, TextEditor> fileRecords = new HashMap<>();

    public void registerObserver(FileObserver observer) {
        observers.add(observer);
    }

    public void createFile(String fileName, String content) {
        TextEditor editor = new TextEditor(content);
        fileRecords.put(fileName, editor);
        notifyObservers(fileName);
    }

    public void updateFile(String fileName, String newContent) {
        TextEditor editor = fileRecords.get(fileName);
        if (editor != null) {
            editor.modifyText(newContent);
            notifyObservers(fileName);
        } else {
            System.out.println("Error: File \"" + fileName + "\" does not exist.");
        }
    }

    private void notifyObservers(String fileName) {
        for (FileObserver observer : observers) {
            observer.onFileChange(fileName);
        }
    }

    public TextEditor getEditor(String fileName) {
        return fileRecords.get(fileName);
    }
}

// Command Pattern - Interface for Executing and Reversing Actions
interface Action {
    void execute();
    void reverse();
}

// Command Pattern - Undo Implementation
class UndoAction implements Action {
    private TextEditor editor;
    private String previousState;

    public UndoAction(TextEditor editor) {
        this.editor = editor;
        this.previousState = editor.getText();
    }

    @Override
    public void execute() {
        System.out.println("Undo action performed.");
        editor.modifyText(previousState);
    }

    @Override
    public void reverse() {
        System.out.println("Redo action performed.");
        // For simplicity, reverse reverts to the same saved state
        editor.modifyText(previousState);
    }
}

// Strategy Pattern - Interface for Code Highlighting
interface CodeHighlighter {
    void applyHighlight(String code);
}

// Strategy Pattern - Python Code Highlighter
class PythonCodeHighlighter implements CodeHighlighter {
    @Override
    public void applyHighlight(String code) {
        System.out.println("Highlighting Python code: " + code);
    }
}

// Strategy Pattern - Java Code Highlighter
class JavaCodeHighlighter implements CodeHighlighter {
    @Override
    public void applyHighlight(String code) {
        System.out.println("Highlighting Java code: " + code);
    }
}

// Factory Pattern - Factory to Create Syntax Highlighter
class HighlighterFactory {
    public static CodeHighlighter getHighlighter(String language) {
        switch (language.toLowerCase()) {
            case "python":
                return new PythonCodeHighlighter();
            case "java":
                return new JavaCodeHighlighter();
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }
}

// Decorator Pattern - Abstract Class for Extending Editor Features
abstract class EditorFeature {
    public abstract void showFeatures();
}

// Decorator Pattern - Basic Editor
class BasicTextEditor extends EditorFeature {
    @Override
    public void showFeatures() {
        System.out.println("Basic editor functionality loaded.");
    }
}

// Decorator Pattern - Adding Auto-Complete Feature to Editor
class AutoCompleteDecorator extends EditorFeature {
    private EditorFeature baseEditor;

    public AutoCompleteDecorator(EditorFeature baseEditor) {
        this.baseEditor = baseEditor;
    }

    @Override
    public void showFeatures() {
        baseEditor.showFeatures();
        System.out.println("Auto-complete feature enabled.");
    }
}

// Memento Pattern - Snapshot of Editor State
class TextSnapshot {
    private final String state;

    public TextSnapshot(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}

// Memento Pattern - History Manager for Undo/Redo
class StateHistory {
    private Stack<TextSnapshot> history = new Stack<>();

    public void saveState(TextSnapshot snapshot) {
        history.push(snapshot);
    }

    public TextSnapshot restoreState() {
        return history.isEmpty() ? null : history.pop();
    }
}

// Singleton Pattern - Theme Manager
class ThemeManager {
    private static ThemeManager instance;

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void setTheme(String themeName) {
        System.out.println("Theme set to: " + themeName);
    }
}

public class IDEApplication {
    public static void main(String[] args) {
        // Observer Pattern
        FileController fileController = new FileController();
        TextEditor observer = new TextEditor("Initial content of example.java");
        fileController.registerObserver(observer);
        fileController.createFile("example.java", "Initial content of example.java");
        fileController.updateFile("example.java", "Updated content of example.java");

        // Command Pattern
        UndoAction undo = new UndoAction(fileController.getEditor("example.java"));
        undo.execute();

        // Strategy Pattern
        CodeHighlighter pythonHighlighter = HighlighterFactory.getHighlighter("python");
        pythonHighlighter.applyHighlight("def greet():");

        CodeHighlighter javaHighlighter = HighlighterFactory.getHighlighter("java");
        javaHighlighter.applyHighlight("public static void main(String[] args) {}");

        // Decorator Pattern
        EditorFeature basicEditor = new BasicTextEditor();
        EditorFeature enhancedEditor = new AutoCompleteDecorator(basicEditor);
        enhancedEditor.showFeatures();

        // Memento Pattern
        StateHistory history = new StateHistory();
        history.saveState(new TextSnapshot(observer.getText()));
        fileController.updateFile("example.java", "Another update.");
        history.saveState(new TextSnapshot(observer.getText()));

        // Undo last change
        TextSnapshot previousSnapshot = history.restoreState();
        if (previousSnapshot != null) {
            fileController.getEditor("example.java").modifyText(previousSnapshot.getState());
            System.out.println("Restored content: " + fileController.getEditor("example.java").getText());
        }

        // Singleton Pattern
        ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.setTheme("Dark Mode");

        // Final State
        System.out.println("Final content of example.java: " + fileController.getEditor("example.java").getText());
    }
}
