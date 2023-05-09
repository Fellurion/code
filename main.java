import java.util.Scanner;

class test {

    public static void main(String[] arg) {

        text_line("Willkommen,Geben sie ihren verdienst ein:");

        try (Scanner score1 = new Scanner(System.in)) {
            double score2 = score1.nextDouble();

            if (score2 >= 40000 && score2 < 60000) {

                double score3 = score2 * 19.6 / 100;

                System.out.println("Sie müssen " + score3 + "Euro Steuern Zahlen 19,6%");

            } else if (score2 < 40000) {

                double score4 = score2 * 0.5 / 100;

                System.out.println("Sie müssen " + score4 + "Euro Steuern Zahlen, 0,5%");

            } else if (score2 > 60000 && score2 < 100000) {

                double score4 = score2 * 25.4 / 100;
                System.out.println("Sie müssen " + score4 + "Euro Steuern Zahlen, 25%");

            } else {
                double score4 = score2 * 33.8 / 100;
                System.out.println("Sie müssen " + score4 + "Euro Steuern Zahlen 33,8%");

            }
        }

    }

    public boolean msg1(String msg) { // wenn ein string eingegeben wird

        return false;
    }

    static void text_line(String start) {

        System.out.println(start);

    }

}
