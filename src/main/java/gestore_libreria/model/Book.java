package gestore_libreria.model;

public class Book {
    //dichiaro le variabili del libro
    private int id;
    //obbligatori
    private final String title;
    private final String author;

    //facoltativi
    private final String isbn;
    private final String genre;
    private final int rating;
    private final String readingState;
    private final String coverPath;     //per le immagini di copertina

    /**
     * Costruisce un'istanza di Book utilizzando un oggetto Builder.
     *
     * @param builder Il Builder contenente i dati per costruire il libro.
     * @pre builder non deve essere null.
     * @pre builder.title non deve essere null o vuoto.
     * @pre builder.author non deve essere null o vuoto.
     * @pre builder.rating deve essere un valore tra 0 e 5 (inclusi).
     * @pre builder.readingState deve essere uno stato di lettura valido ("letto", "in lettura", "da leggere").
     * @post Viene creata una nuova istanza di Book con i valori specificati dal Builder.
     * @post Tutti i campi 'final' del libro sono inizializzati e non modificabili successivamente.
     */
    private Book(Builder builder){
        this.title = builder.title;
        this.author = builder.author;
        this.isbn = builder.isbn;
        this.genre = builder.genre;
        this.rating = builder.rating;
        this.readingState = builder.readingState;
        this.coverPath = builder.coverPath;
        this.id = builder.id;
    }

    //getter per la lettura

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getGenre() {
        return genre;
    }

    public int getRating() {
        return rating;
    }

    public String getReadingState() {
        return readingState;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public int getId() {
        return id;
    }

    /**
     * Imposta l'ID del libro. Questo metodo è generalmente usato solo dal database
     * dopo che il libro è stato salvato e gli è stato assegnato un ID.
     *
     * @param id L'ID univoco da assegnare al libro.
     * @pre id deve essere un intero
     * @post L'ID del libro è impostato al valore fornito.
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", genre='" + genre + '\'' +
                ", rating=" + rating +
                ", readingState='" + readingState + '\'' +
                ", coverPath='" + coverPath + '\'' +
                '}';
    }

    //builder per la costruzione dell'oggetto libro
    public static class Builder{


        //obbligatori
        private final String title;
        private final String author;

        //facoltativi
        private String isbn = "";
        private String genre = "";
        private int rating = 0;
        private String readingState = "da leggere";
        private String coverPath = "";        //per le immagini di copertina
        private int id = 0;

        /**
         * Costruisce un nuovo Builder per l'oggetto Book.
         *
         * @param title Il titolo obbligatorio del libro.
         * @param author L'autore obbligatorio del libro.
         * @pre title non deve essere null o vuoto.
         * @pre author non deve essere null o vuoto.
         * @post Il Builder è inizializzato con il titolo e l'autore specificati.
         * @post I campi facoltativi sono inizializzati con i loro valori di default.
         * @throws IllegalArgumentException se il titolo o l'autore non rispettano le pre-condizioni.
         */
        public Builder(String title, String author) {
            if(title == null || title.isBlank() || author == null || author.isEmpty()){
                throw new IllegalArgumentException("Titolo e autore sono obbligatori");
            }
            this.title = title;
            this.author = author;
        }

        //restituisco un oggetto builder per ogni campo inserito
        public Builder isbn(String isbn){
            this.isbn = isbn;
            return this;
        }

        public Builder genre(String genre){
            this.genre = genre;
            return this;
        }

        public Builder rating(int rating){
            if(rating < 0 || rating > 5){
                throw new IllegalArgumentException("Valore non valido, rating compreso tra 0 e 5");
            }
            this.rating = rating;
            return this;
        }

        public Builder readingState(String state) {
            if (!state.matches("letto|in lettura|da leggere"))
                this.readingState = "da leggere";
                //throw new IllegalArgumentException("Stato non valido");
            this.readingState = state;
            return this;
        }

        public Builder coverPath(String path) {
            this.coverPath = path;
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Book build() {
            return new Book(this);
        }
    }
}
