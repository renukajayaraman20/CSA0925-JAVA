import java.util.ArrayList;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        ArrayList<Student> AL = new ArrayList<>();
        AL.add(new Student("Reena", 15, 102, 89));
        AL.add(new Student("Karthik", 17, 104, 60));
        AL.add(new Student("Diya", 14, 110, 99));
        AL.add(new Student("Kiran", 14, 105, 50));
        AL.add(new Student("Geetha", 16, 107, 55));
        System.out.println("Student Details:");
        for (Student s : AL) {
            s.display();
        }
        Student min = AL.get(0);
        for (Student s : AL) {
            if (s.marks < min.marks) {
                min = s;
            }
        }
        System.out.println("Student with Minimum Marks");
        System.out.println("Name  : " + min.name);
        System.out.println("Marks : " + min.marks);
    }
}

