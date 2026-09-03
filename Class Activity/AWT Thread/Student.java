package Crud;

public class Student {
    private int id;
    private String name;
    private String department;
    private int mark;

    public Student() {
    }

    public Student(int id, String name, String department, int mark) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.mark = mark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
}