class Student {
    String name;
    int age;
    int id;
    int marks;
    Student(String name, int age, int id, int marks) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.marks = marks;
    }
    void display() {
        System.out.println("Name  : " + name);
        System.out.println("Age   : " + age);
        System.out.println("ID    : " + id);
        System.out.println("Marks : " + marks);

    }
}


