class Thread1 extends Thread {
    public void run() {
        if (Thread.currentThread().getName().equals("Prime")) {
            int n=8;
            int count=0;
            for (int i =1;i <= n;i++) {
                if (n%i==0)
                    count++;
            }
            if (count==2)
                System.out.println("Prime");
            else
                System.out.println("Not Prime");
        }
        else if (Thread.currentThread().getName().equals("Armstrong")) {
            int n=153;
            int temp=n;
            int sum=0;
            while (n>0) {
                int digit=n%10;
                sum=sum+digit*digit*digit;
                n=n/10;
            }
            if (sum==temp)
                System.out.println("Armstrong");
            else
                System.out.println("Not Armstrong");
        }
        else if (Thread.currentThread().getName().equals("Factorial")) {
            int n=5;
            int fact=1;
            for (int i=1; i<=n;i++) {
                fact=fact*i;
            }
            System.out.println("Factorial="+fact);
        }
    }
}
class Main {
    public static void main(String[] args) {
        Thread1 t1 = new Thread1();
        Thread1 t2 = new Thread1();
        Thread1 t3 = new Thread1();
        t1.setName("Prime");
        t2.setName("Armstrong");
        t3.setName("Factorial");
        t1.start();
        t2.start();
        t3.start();
        System.out.println("Main completed.");
    }
}
