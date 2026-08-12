import java.util.Scanner;
class isprimethread extends Thread {
    public void primeornot(int n) {
        int count = 0;
        for (int i = 1; i <= n; i++) {
            if (n % i == 0) {
                count++;
            }
        }
        if (count == 2) {
            System.out.println("Prime");
        } else {
            System.out.println("Not Prime");
        }
    }
    public void run() {
        primeornot(8);
    }
}
class armstrongthread extends Thread {
    public void armstrongornot(int n) {
        int dig, sum = 0;
        int temp = n;
        while (n > 0) {
            dig = n % 10;
            sum = sum + (dig * dig * dig);
            n = n / 10;
        }
        if (sum == temp) {
            System.out.println("Armstrong");
        } else {
            System.out.println("Not Armstrong");
        }
    }
    public void run() {
        armstrongornot(151);
    }
}
class factorialthread extends Thread {
    public void factorial(int n) {
        int fact = 1;
        for (int i = 1; i <= n; i++) {
            fact = fact * i;
        }
        System.out.println("Factorial = " + fact);
    }
    public void run() {
        factorial(5);
    }
}
class Main {
    public static void main(String[] args) {
        isprimethread t1 = new isprimethread();
        t1.setName("First thread");
        armstrongthread t2 = new armstrongthread();
        t2.setName("Second thread");
        factorialthread t3 = new factorialthread();
        t3.setName("Third thread");
        t1.start();
        t2.start();
        t3.start();
        System.out.println("Main completed.");
    }
}
