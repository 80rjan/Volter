package skit_project.example_labs.lab2.submission.src.main.java.com.skit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public static String analyzeTemperatures(int[] temps) {
        boolean hasInvalid = false;
        int warmDays = 0;

        for (int i = 0; i < temps.length; i++) {
            int temp = temps[i];

            if (temp < -50 || temp > 60) {
                hasInvalid = true;
            } else if (temp >= 20) {
                warmDays++;
            }
        }

        if (hasInvalid) {
            return "Invalid temperatures detected.";
        } else if (warmDays == temps.length && temps.length > 0) {
            return "All days were warm.";
        } else if (warmDays == 0) {
            return "No warm days.";
        } else {
            return "Some days were warm.";
        }
    }
}
