package examples;
import java.util.Random;

public class Student {
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private int age;

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    private int grade;

    @Override
    public String toString() {
        return "Student{" +
                "age=" + age +
                ", grade=" + grade +
                '}';
    }

    public void display() {
        System.out.println(this.toString());
    }
    public void learn() throws Exception{
//        long start = System.currentTimeMillis();
        Random rand = new Random();
        int time =  rand.nextInt(1000)+3000;
        Thread.sleep(time);
//        long end = System.currentTimeMillis();
//        System.out.println(end-start);
    }
    public static void main(String[] args) {
        Student s  = new Student();
        s.display();
        try{
            s.learn();
        }
        catch (Exception e){

        }

    }
}