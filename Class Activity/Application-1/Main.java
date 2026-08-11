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


//OUTPUT//
Student Details:
Name  : Reena
Age   : 15
ID    : 102
Marks : 89
Name  : Karthik
Age   : 17
ID    : 104
Marks : 60
Name  : Diya
Age   : 14
ID    : 110
Marks : 99
Name  : Kiran
Age   : 14
ID    : 105
Marks : 50
Name  : Geetha
Age   : 16
ID    : 107
Marks : 55
Student with Minimum Marks
Name  : Kiran
Marks : 50

Process finished with exit code 0


