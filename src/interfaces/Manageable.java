package interfaces;

public interface Manageable {
    void create(Object obj);
    Object read(String id);
    void update(Object obj);
    void delete(String id);
}
