import java.rmi.Naming;
import java.util.Scanner;

public class RMIClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.println("=== Cliente Calculadora RMI ===");
            System.out.print("Ingresa la IP del servidor RMI: ");
            String serverIP = scanner.nextLine();
            
            // Conectar al servicio remoto
            String url = "rmi://" + serverIP + ":1099/CalculatorService";
            System.out.println("Conectando a: " + url);
            
            CalculatorService calculator = (CalculatorService) Naming.lookup(url);
            System.out.println("✓ Conectado exitosamente al servidor RMI\n");
            
            boolean continuar = true;
            
            while (continuar) {
                System.out.println("\n--- Menú ---");
                System.out.println("1. Sumar");
                System.out.println("2. Restar");
                System.out.println("3. Ver contador de operaciones");
                System.out.println("4. Salir");
                System.out.print("Selecciona una opción: ");
                
                int opcion = scanner.nextInt();
                
                switch (opcion) {
                    case 1:
                        System.out.print("Primer número: ");
                        int a1 = scanner.nextInt();
                        System.out.print("Segundo número: ");
                        int b1 = scanner.nextInt();
                        int suma = calculator.add(a1, b1);
                        System.out.println("✓ Resultado: " + a1 + " + " + b1 + " = " + suma);
                        break;
                        
                    case 2:
                        System.out.print("Primer número: ");
                        int a2 = scanner.nextInt();
                        System.out.print("Segundo número: ");
                        int b2 = scanner.nextInt();
                        int resta = calculator.subtract(a2, b2);
                        System.out.println("✓ Resultado: " + a2 + " - " + b2 + " = " + resta);
                        break;
                        
                    case 3:
                        int contador = calculator.getOperationCount();
                        System.out.println("✓ Total de operaciones realizadas: " + contador);
                        break;
                        
                    case 4:
                        continuar = false;
                        System.out.println("¡Hasta luego!");
                        break;
                        
                    default:
                        System.out.println("✗ Opción inválida");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}