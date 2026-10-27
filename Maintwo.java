import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

abstract class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private String category;
    private int id;
    private String title;

    public Item(int id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }

    public abstract void displayInfo();
}

class Book extends Item {
    private static final long serialVersionUID = 1L;

    private String author;
    private boolean borrowed = false;

    public Book(int id, String title, String author, String category) {
        super(id, title, category);
        this.author = author;
    }

    public String getAuthor() { return author; }
    public boolean isBorrowed() { return borrowed; }
    public void borrowBook() { borrowed = true; }
    public void returnBook() { borrowed = false; }

    @Override
    public void displayInfo() {
        System.out.println("[BOOK] ID: " + getId() + " | Title: " + getTitle() + " | Status: " + (borrowed ? "Borrowed" : "Available"));
    }
}

class PrintedBook extends Book {
    private int pages;
    public PrintedBook(int id, String title, String author, int pages) {
        super(id, title, author, "Drama");
        this.pages = pages;
    }
}

class Ebook extends Book {
    private String fileSize;
    public Ebook(int id, String title, String author, String fileSize) {
        super(id, title, author, "Drama");
        this.fileSize = fileSize;
    }
}

class BookSearchTask implements Runnable {
    private int bookId;
    private ArrayList<Book> collection;
    private JLabel statusLabel;

    public BookSearchTask(int bookId, ArrayList<Book> collection, JLabel statusLabel) {
        this.bookId = bookId;
        this.collection = collection;
        this.statusLabel = statusLabel;
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> statusLabel.setText("Status: Searching for Book ID " + bookId + "..."));

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            SwingUtilities.invokeLater(() -> statusLabel.setText("Status: Search interrupted."));
            return;
        }

        Book found = null;
        for (Book b : collection) {
            if (b.getId() == bookId) {
                found = b;
                break;
            }
        }

        final Book result = found;
        SwingUtilities.invokeLater(() -> {
            if (result != null) {
                statusLabel.setText("Found: " + result.getTitle() + " (" + (result.isBorrowed() ? "Borrowed" : "Available") + ")");
            } else {
                statusLabel.setText("Status: Book ID " + bookId + " not found.");
            }
        });
    }
}

public class Maintwo {
    public static void main(String[] args) {
        ArrayList<Book> bookCollection = new ArrayList<>();
        bookCollection.add(new PrintedBook(1, "Money Game", "Baltazar", 250));
        bookCollection.add(new PrintedBook(2, "Street Code", "Gabo", 300));
        bookCollection.add(new Ebook(3, "Mindset", "Gabgab", "10MB"));

        JFrame frame = new JFrame("Library Management System");
        frame.setSize(450, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        
        JLabel statusLabel = new JLabel("Status: Ready. Enter Book ID to search.");
        JTextField idField = new JTextField(10);
        JButton searchButton = new JButton("Search Book");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(idField.getText().trim());
                    
                    BookSearchTask task = new BookSearchTask(id, bookCollection, statusLabel);
                    Thread workerThread = new Thread(task);
                    
                    workerThread.start();
                    
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Error: Please enter a valid numeric ID.");
                }
            }
        });

        frame.add(new JLabel("Book ID:"));
        frame.add(idField);
        frame.add(searchButton);
        frame.add(statusLabel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}