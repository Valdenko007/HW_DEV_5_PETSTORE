package ua.goit.api.command.model;

public class Tag {
    private long id;
    private String name;

    public Tag() {
    }

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString (){
        return String.format("Id: %d \n Name: %s \n", id, name);
    }
}
