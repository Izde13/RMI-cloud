import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            // Obtener hostname/IP de la VM
            String hostname = System.getProperty("java.rmi.server.hostname");
            System.out.println("Iniciando servidor RMI...");
            System.out.println("Hostname configurado: " + hostname);
            
            // Crear el RMI Registry en el puerto 1099
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry creado en puerto 1099");
            
            // Crear e instanciar el objeto remoto
            CalculatorServiceImpl calculator = new CalculatorServiceImpl();
            
            // Registrar el objeto en el registry
            Naming.rebind("//localhost:1099/CalculatorService", calculator);
            
            System.out.println("===========================================");
            System.out.println("Servidor RMI listo y escuchando...");
            System.out.println("URL: rmi://" + hostname + ":1099/CalculatorService");
            System.out.println("===========================================");
            
            // Mantener el servidor corriendo
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}