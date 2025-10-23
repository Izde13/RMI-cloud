import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CalculatorServiceImpl extends UnicastRemoteObject implements CalculatorService {
    
    private AtomicInteger counter;
    private static final String COUNTER_FILE = "counter.txt";
    
    public CalculatorServiceImpl() throws RemoteException {
        super(1098); // Puerto fijo para el objeto remoto
        loadCounter();
        System.out.println("CalculatorService inicializado. Contador actual: " + counter.get());
    }
    
    @Override
    public int add(int a, int b) throws RemoteException {
        int result = a + b;
        incrementAndSaveCounter();
        System.out.println("Operación #" + counter.get() + ": " + a + " + " + b + " = " + result);
        return result;
    }
    
    @Override
    public int subtract(int a, int b) throws RemoteException {
        int result = a - b;
        incrementAndSaveCounter();
        System.out.println("Operación #" + counter.get() + ": " + a + " - " + b + " = " + result);
        return result;
    }
    
    @Override
    public int getOperationCount() throws RemoteException {
        System.out.println("Consultando contador: " + counter.get() + " operaciones realizadas");
        return counter.get();
    }
    
    private void incrementAndSaveCounter() {
        counter.incrementAndGet();
        saveCounter();
    }
    
    private void loadCounter() {
        try {
            File file = new File(COUNTER_FILE);
            if (file.exists()) {
                String content = Files.readString(file.toPath()).trim();
                counter = new AtomicInteger(Integer.parseInt(content));
                System.out.println("Contador cargado desde archivo: " + counter.get());
            } else {
                counter = new AtomicInteger(0);
                System.out.println("Archivo de contador no existe. Iniciando en 0");
            }
        } catch (IOException e) {
            System.err.println("Error al cargar contador: " + e.getMessage());
            counter = new AtomicInteger(0);
        }
    }
    
    private void saveCounter() {
        try {
            Files.writeString(Path.of(COUNTER_FILE), String.valueOf(counter.get()));
        } catch (IOException e) {
            System.err.println("Error al guardar contador: " + e.getMessage());
        }
    }
}