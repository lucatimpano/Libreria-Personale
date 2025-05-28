package gestore_libreria.ui;

import gestore_libreria.model.Book;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;


public class BooksPanelUI extends JPanel {
    private JPanel bookListPanel;
    private Consumer<Book> onBookClickListener;     //click per i dettagli del libro
    private Consumer<Book> onDeleteBookListener;    //click destro per eliminare un libro
    // Colori per lo stato di lettura
    private static final Color READING_STATE_COLOR = new Color(252, 202, 70);
    private static final Color UNREAD_STATE_COLOR = new Color(90, 93, 95);
    private static final Color READ_STATE_COLOR = new Color(112, 224, 0);

    private JPopupMenu popupMenu;

    public BooksPanelUI() {
        setLayout(new BorderLayout());
        //setBorder(BorderFactory.createTitledBorder("Lista Libri"));

        bookListPanel = new JPanel();
        bookListPanel.setLayout(new BoxLayout(bookListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(bookListPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        inizializzaPopupMenu();
    }

    public void setOnDeleteBookListener(Consumer<Book> onDeleteBookListener) {
        this.onDeleteBookListener = onDeleteBookListener;
    }

    public void setOnBookClickListener(Consumer<Book> onBookClickListener) {
        this.onBookClickListener = onBookClickListener;
    }

    private void inizializzaPopupMenu() {
        popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Elimina");
        deleteItem.setForeground(Color.RED);
        deleteItem.addActionListener(e -> {});      //TODO
        popupMenu.add(deleteItem);
    }

    private void showPopupMenu(MouseEvent e, Book book) {
        if (e.isPopupTrigger()) { // isPopupTrigger() verifica se è un evento di click destro (o equivalente su altri OS)
            // Collega l'azione "Elimina" al libro specifico
            // Rimuovi eventuali listener precedenti per evitare duplicati se il popup è riusato
            for (int i = 0; i < popupMenu.getComponentCount(); i++) {
                Component comp = popupMenu.getComponent(i);
                if (comp instanceof JMenuItem menuItem) {
                    if (menuItem.getText().equals("Elimina")) {
                        // Rimuove tutti gli ActionListener esistenti per "Elimina"
                        for (java.awt.event.ActionListener al : menuItem.getActionListeners()) {
                            menuItem.removeActionListener(al);
                        }
                        // Aggiunge il listener per l'eliminazione specifica di questo libro
                        menuItem.addActionListener(actionEvent -> {
                            if (onDeleteBookListener != null) {
                                onDeleteBookListener.accept(book); // Notifica il listener esterno con il libro da eliminare
                            }
                        });
                        break; // Trovato e configurato l'elemento "Elimina"
                    }
                }
            }
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }


    //metodo che verrà richiamato per aggiornare la lista dei libri con l'observer
    public void displayBooks(List<Book> books) {
        bookListPanel.removeAll();
        if (books.isEmpty()) {
            JLabel noBooksLabel = new JLabel("Nessun libro presente nella libreria.");
            noBooksLabel.setForeground(Color.WHITE);
            noBooksLabel.setHorizontalAlignment(SwingConstants.CENTER);
            bookListPanel.add(noBooksLabel);
        } else {
            for (Book book : books) {
                JPanel bookPanel = new JPanel(new BorderLayout());

                int coverHeight = 100;
                int verticalPadding = 16;
                bookPanel.setPreferredSize(new Dimension(400, coverHeight + verticalPadding));
                bookPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, coverHeight + verticalPadding));
                bookPanel.setMinimumSize(new Dimension(400, coverHeight + verticalPadding));

                bookPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
                bookPanel.setForeground(Color.WHITE); // Colore del testo per i pannelli dei libri

                //Mouse Listener
                bookPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(SwingUtilities.isLeftMouseButton(e)) {
                            if (onBookClickListener != null) {
                                onBookClickListener.accept(book); // Notifica il listener con il libro cliccato
                            }
                        }

                    }

                    // Effetti visivi al passaggio del mouse
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        bookPanel.setBackground(new Color(78, 78, 78)); // Colore più chiaro al passaggio
                        bookPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursore a mano
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        bookPanel.setBackground(new Color(30,30,30)); // Torna al colore originale
                        bookPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // Cursore normale
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        showPopupMenu(e, book);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        showPopupMenu(e, book);
                    }
                });



                // Copertina
                JLabel coverLabel = new JLabel();
                coverLabel.setPreferredSize(new Dimension(70, 100));
                coverLabel.setOpaque(true);
                coverLabel.setBackground(Color.LIGHT_GRAY); // Colore di sfondo per l'area della copertina

                //caricamento immagine
                try {
                    ImageIcon icon = new ImageIcon(book.getCoverPath());
                    // Verifica se l'immagine è stata caricata correttamente (larghezza > -1)
                    if (icon.getIconWidth() == -1) {
                        System.err.println("Impossibile caricare l'immagine da: " + book.getCoverPath() + ". File non valido o corrotto.");
                        coverLabel.setText("Err Img"); // Testo placeholder se l'immagine non è valida
                        coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        coverLabel.setForeground(Color.RED); // Per evidenziare l'errore
                        coverLabel.setBackground(Color.DARK_GRAY); // Sfondo per il testo di errore
                    } else {
                        Image img = icon.getImage().getScaledInstance(70, 100, Image.SCALE_SMOOTH);
                        coverLabel.setIcon(new ImageIcon(img));
                        coverLabel.setBackground(null); // Rimuovi il background grigio se c'è un'immagine valida
                    }
                } catch (Exception e) {
                    System.err.println("Errore durante il caricamento immagine: " + book.getCoverPath() + " - " + e.getMessage());
                    coverLabel.setText("Err Img"); // Testo placeholder in caso di errore
                    coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    coverLabel.setForeground(Color.RED);
                    coverLabel.setBackground(Color.DARK_GRAY);
                }
                bookPanel.add(coverLabel, BorderLayout.WEST);
                buildSingleElement(book, bookPanel);

                // spacing
                bookListPanel.add(bookPanel);
                //bookListPanel.add(Box.createRigidArea(new Dimension(0, 6)));

            }
        }
        bookListPanel.revalidate();
        bookListPanel.repaint();
    }

    private void buildSingleElement(Book book, JPanel row) {
        // container verticale
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        center.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Riga 1
        JPanel line1 = new JPanel(new BorderLayout());
        line1.setOpaque(false);
        line1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        line1.add(titleLabel, BorderLayout.WEST);

        JPanel stars = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        stars.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            JLabel star = new JLabel("★");
            star.setForeground(i < book.getRating()
                    ? new Color(255, 215, 0)
                    : Color.DARK_GRAY);
            stars.add(star);
        }
        line1.add(stars, BorderLayout.EAST);

        center.add(line1);

        // Riga 2
        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        line2.setOpaque(false);
        line2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel authorLabel = new JLabel(book.getAuthor());
        authorLabel.setForeground(Color.LIGHT_GRAY);
        line2.add(authorLabel);

        center.add(line2);

        // Riga 3
        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        line3.setOpaque(false);
        line3.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel stateLabel = new JLabel(book.getReadingState().toUpperCase());
        stateLabel.setOpaque(true);
        stateLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));
        stateLabel.setForeground(new Color(30,30,30));
        switch (book.getReadingState().toUpperCase()) {
            case "IN LETTURA" -> stateLabel.setBackground(READING_STATE_COLOR);
            case "DA LEGGERE" -> stateLabel.setBackground(UNREAD_STATE_COLOR);
            case "LETTO" -> stateLabel.setBackground(READ_STATE_COLOR);
            default -> stateLabel.setBackground(Color.GRAY);
        }
        line3.add(stateLabel);

        center.add(line3);

        // aggiungo center al row
        row.add(center, BorderLayout.CENTER);


    }
}
