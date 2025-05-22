package gestore_libreria.db;

public class ConcreteBookManager extends BookManager{

    //classe concreta per utilizzare il bookManager

    public ConcreteBookManager(BookRepositoryImplementor repository) {
        super(repository);
    }
}
