package gestore_libreria.model;

public class Book {
    //dichiaro le variabili del libro
    //obbligatori
    private final String title;
    private final String author;

    //facoltativi
    private final String isbn;
    private final String genre;
    private final int rating;
    private final String readingState;
    private final String coverPath;     //per le immagini di copertina

    //costruisco l'oggetto con il builder
    private Book(Builder builder){
        this.title = builder.title;
        this.author = builder.author;
        this.isbn = builder.isbn;
        this.genre = builder.genre;
        this.rating = builder.rating;
        this.readingState = builder.readingState;
        this.coverPath = builder.coverPath;
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

    //builder per la costruzione dell'oggetto libro
    public static class Builder{
        //obbligatori
        private final String title;
        private final String author;

        //facoltativi
        private String isbn = "";
        private String genre = "";
        private int rating = 0;
        private String readingState = "";
        private String coverPath = "";        //per le immagini di copertina

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
                throw new IllegalArgumentException("Stato non valido");
            this.readingState = state;
            return this;
        }

        public Builder coverPath(String path) {
            this.coverPath = path;
            return this;
        }

        public Book build() {
            return new Book(this);
        }


    }
}
