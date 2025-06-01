package gestore_libreria.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookTest {
    private Book testBook;

    @Before
    public void setUp() throws Exception {
        testBook = new Book.Builder("Il Signore degli Anelli", "J.R.R. Tolkien")
                .isbn("978-8804668236")
                .genre("Fantasy")
                .rating(5)
                .readingState("letto")
                .coverPath("/immagini/signore-degli-anelli.jpg")
                .id(1)
                .build();
    }

    @Test
    public void testGetters(){
        assertEquals("Il Signore degli Anelli", testBook.getTitle());
        assertEquals("J.R.R. Tolkien", testBook.getAuthor());
        assertEquals("Fantasy", testBook.getGenre());
        assertEquals(5, testBook.getRating());
        assertEquals("letto", testBook.getReadingState());
        assertEquals("/immagini/signore-degli-anelli.jpg", testBook.getCoverPath());
        assertEquals(1, testBook.getId());
    }

    @Test
    public void testSetId(){
        testBook.setId(2);
        assertEquals(2, testBook.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBuilderNullTitle(){
        new Book.Builder(null, "J.R.R. Tolkien");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBuilderNullAuthor(){
        new Book.Builder("Il Signore degli Anelli", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidRating(){
        new Book.Builder("Il Signore degli Anelli", "J.R.R.")
                .rating(6);
    }
}