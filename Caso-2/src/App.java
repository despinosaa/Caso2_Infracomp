import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Escoge una opcion:");
        System.out.println("1. Option 1");
        System.out.println("2. Option 2");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        if (choice == 1) {
            System.out.println("Opcion 1");
        } else if (choice == 2) {
            System.out.println("Opcion 2");
        } else {
            System.out.println("Opcion invalida");
        }
        scanner.close();
    }
}
