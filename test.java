import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        // declare
        Scanner scanner = new Scanner(System.in);
        int sum = 0;
        int[][] array = new int[2][4];

        // input
        System.out.println("Enter 8 element for 2x4 array:");

        // process
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print("Enter element: ");
                array[i][j] = scanner.nextInt();
                sum += array[i][j];
            }
        }

        // output
        System.out.println("\nThe 2D array:");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println("");
        }

        System.out.println("The result of sum up of 8 elements input is: " + sum);

        scanner.close();
    }
}
