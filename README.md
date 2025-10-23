# Taller RMI - Sistema de Calculadora Remota en Azure

## 📋 Descripción del Proyecto

Implementación de un servicio de calculadora remota utilizando **Java RMI (Remote Method Invocation)** desplegado en una máquina virtual de **Microsoft Azure**. El sistema permite realizar operaciones matemáticas básicas de forma remota y mantiene un contador persistente de todas las operaciones ejecutadas.

## 🎯 Objetivos Cumplidos

- ✅ Implementar un servicio RMI en Java
- ✅ Desplegar el servidor en la nube (Azure VM)
- ✅ Configurar comunicación remota entre cliente y servidor
- ✅ Implementar persistencia de estado (contador de operaciones)
- ✅ Configurar servicio systemd para ejecución continua

## 🏗️ Arquitectura del Sistema

El sistema está compuesto por dos componentes principales:

### Servidor RMI (Cloud)
- **Ubicación**: Máquina Virtual en Azure
- **Sistema Operativo**: Ubuntu Server 22.04 LTS
- **Java Version**: OpenJDK 17
- **Puertos Expuestos**:
  - `1099`: RMI Registry
  - `1098`: Servidor RMI

### Cliente RMI (Local)
- **Ejecución**: Máquina local del usuario
- **Conexión**: Remota vía Internet usando IP pública de Azure
- **Interfaz**: Consola interactiva con menú de opciones

## 🔧 Componentes del Código

### Servidor (RMI Cloud)

#### `CalculatorService.java`
Interfaz remota que define el contrato de servicios disponibles.

**Métodos disponibles:**
- `int add(int a, int b)`: Suma dos números enteros
- `int subtract(int a, int b)`: Resta dos números enteros
- `int getOperationCount()`: Retorna el número total de operaciones realizadas

#### `CalculatorServiceImpl.java`
Implementación del servicio remoto que extiende `UnicastRemoteObject`.

**Características principales:**
- Implementa la lógica de las operaciones matemáticas
- Mantiene un contador de operaciones
- Guarda el estado del contador en archivo para persistencia
- Se exporta en el puerto `1098` para comunicación RMI

#### `RMIServer.java`
Clase principal del servidor que inicializa el sistema RMI.

**Funcionalidades:**
- Crea el RMI Registry en el puerto `1099`
- Instancia el servicio de calculadora
- Registra el objeto remoto con el nombre `CalculatorService`
- Mantiene el servidor en ejecución continua

### Cliente (RMI Local)

#### `CalculatorService.java`
Copia idéntica de la interfaz del servidor (requerido por RMI).

#### `RMIClient.java`
Aplicación cliente que consume el servicio remoto.

**Funcionalidades:**
- Solicita la IP del servidor al usuario
- Se conecta al servicio remoto usando `Naming.lookup()`
- Presenta un menú interactivo con opciones:
  1. Realizar suma
  2. Realizar resta
  3. Consultar contador de operaciones
  4. Salir del programa
- Invoca métodos remotos como si fueran locales

## 🚀 Configuración en Azure

### Requisitos de la VM

- **Tipo**: Standard_B1s (o superior)
- **Imagen**: Ubuntu Server 22.04 LTS
- **Red**: IP pública estática o dinámica
- **Software**: JDK 17 instalado

### Reglas de Seguridad (NSG)

| Nombre | Puerto | Protocolo | Descripción |
|--------|--------|-----------|-------------|
| Allow-SSH | 22 | TCP | Acceso SSH a la VM |
| Allow-RMI-Registry | 1099 | TCP | RMI Registry |
| Allow-RMI-Server | 1098 | TCP | Objeto remoto RMI |

### Servicio Systemd

El servidor RMI está configurado como servicio systemd con las siguientes características:

- **Auto-inicio**: Se levanta automáticamente al iniciar la VM
- **Auto-reinicio**: Si el proceso falla, se reinicia automáticamente
- **Logs**: Registra toda la actividad en archivo de log
- **Gestión**: Se puede controlar con comandos `systemctl`

## 📝 Instrucciones de Uso

### Para el Servidor (Ya configurado en Azure)

El servidor ya está desplegado y corriendo como servicio. No requiere intervención manual.

**Comandos útiles de administración:**
```bash
# Ver estado del servicio
sudo systemctl status rmi-calculator

# Ver logs en tiempo real
sudo journalctl -u rmi-calculator -f

# Reiniciar el servicio
sudo systemctl restart rmi-calculator

# Detener el servicio
sudo systemctl stop rmi-calculator

# Iniciar el servicio
sudo systemctl start rmi-calculator
```

### Para el Cliente (Ejecución Local)

#### Paso 1: Compilar el código
```bash
cd RMI_local
javac *.java
```

#### Paso 2: Ejecutar el cliente
```bash
java RMIClient
```

#### Paso 3: Conectarse al servidor

Cuando el programa solicite la IP del servidor, ingresar la **IP pública de la VM Azure**.

**Ejemplo:**
```
=== Cliente Calculadora RMI ===
Ingresa la IP del servidor RMI: **IP pública de la VM Azure**
Conectando a: rmi://**IP pública de la VM Azure**/CalculatorService
✓ Conectado exitosamente al servidor RMI
```

#### Paso 4: Usar el menú interactivo
```
--- Menú ---
1. Sumar
2. Restar
3. Ver contador de operaciones
4. Salir
Selecciona una opción: 1

Primer número: 10
Segundo número: 5
✓ Resultado: 10 + 5 = 15
```

## 🔍 Ejemplo de Flujo Completo

### Escenario de Prueba

**Usuario 1 - Primera sesión:**
1. Conecta al servidor remoto
2. Realiza suma: `5 + 3 = 8` (Contador: 1)
3. Realiza resta: `10 - 4 = 6` (Contador: 2)
4. Consulta contador: `2 operaciones`
5. Cierra el cliente

**Usuario 2 - Segunda sesión (desde otra máquina):**
1. Conecta al mismo servidor remoto
2. Consulta contador: `2 operaciones` (Persistió el estado)
3. Realiza suma: `7 + 2 = 9` (Contador: 3)
4. Consulta contador: `3 operaciones`

**Resultado:** El contador se mantiene entre diferentes clientes y sesiones, demostrando la persistencia del estado en el servidor remoto.

## 📊 Información de Conexión

### URL de Conexión RMI
```
rmi://[IP_PUBLICA_VM]:1099/CalculatorService
```

### Verificación de Conectividad

Para verificar que el servidor está accesible:
```bash
# Verificar puerto 1099 (RMI Registry)
telnet [IP_PUBLICA_VM] 1099

# Verificar puerto 1098 (RMI Server)
telnet [IP_PUBLICA_VM] 1098
```

Si ambos puertos responden, el servidor está configurado correctamente.

## 🛠️ Solución de Problemas

### Error: "Connection refused"

**Causa:** El servidor RMI no está corriendo o los puertos están bloqueados.

**Solución:**
1. Verificar que el servicio systemd está activo: `sudo systemctl status rmi-calculator`
2. Verificar reglas de NSG en Azure Portal
3. Verificar firewall de la VM: `sudo ufw status`

### Error: "NotBoundException"

**Causa:** El objeto remoto no está registrado en el RMI Registry.

**Solución:**
1. Verificar logs del servidor: `sudo journalctl -u rmi-calculator -f`
2. Reiniciar el servicio: `sudo systemctl restart rmi-calculator`
3. Verificar que el nombre del binding es correcto: `CalculatorService`

### Error: "UnknownHostException"

**Causa:** La IP del servidor no es correcta o no es accesible.

**Solución:**
1. Verificar la IP pública de la VM en Azure Portal
2. Hacer ping a la IP: `ping [IP_PUBLICA_VM]`
3. Verificar conexión a Internet del cliente

## 📚 Conceptos Técnicos Implementados

### Java RMI (Remote Method Invocation)

RMI es una API de Java que permite invocar métodos en objetos que residen en diferentes máquinas virtuales Java, posiblemente en hosts diferentes.

**Componentes principales:**
- **Registry**: Servicio de nombres que mapea nombres lógicos a referencias de objetos remotos
- **Stub**: Proxy del lado del cliente que representa al objeto remoto
- **Skeleton**: Receptor del lado del servidor que recibe las llamadas y las despacha al objeto real

### Persistencia de Estado

El sistema implementa persistencia básica mediante almacenamiento en archivo, permitiendo que:
- El contador sobreviva reinicios del servidor
- Múltiples clientes vean el estado compartido
- Las operaciones se registren de forma permanente

### Deployment en Cloud

El despliegue en Azure proporciona:
- **Disponibilidad**: Servidor accesible 24/7 desde cualquier ubicación
- **Escalabilidad**: Capacidad de aumentar recursos según demanda
- **IP Pública**: Acceso desde Internet sin necesidad de configuración de red local
- **Persistencia**: VM mantiene el estado incluso después de desconexiones

## 🎓 Conclusiones

Este proyecto demuestra exitosamente:

1. **Comunicación Distribuida**: Cliente y servidor en diferentes ubicaciones físicas comunicándose transparentemente
2. **Abstracción de Red**: RMI oculta la complejidad de la comunicación de red
3. **Cloud Computing**: Aprovechamiento de infraestructura en la nube para servicios distribuidos
4. **Persistencia**: Mantenimiento de estado entre sesiones y reinicios
5. **DevOps Básico**: Configuración de servicios systemd para producción

## 👨‍🎓 Autor

**Universidad Católica de Colombia**  
Facultad de Ingeniería  
Ingeniería de Sistemas y Computación  
Taller de Sistemas Distribuidos Computacionales

---

## 📌 Notas Importantes

- El servidor debe estar corriendo antes de intentar conectarse desde el cliente
- La IP pública de Azure puede cambiar si la VM se detiene y reinicia (usar IP estática para producción)
- El cliente requiere visibilidad de red a los puertos 1099 y 1098 del servidor
- Java RMI utiliza serialización de objetos, por lo que las versiones de Java deben ser compatibles

## 🔗 Referencias

- [Java RMI Documentation](https://docs.oracle.com/javase/tutorial/rmi/)
- [Azure Virtual Machines](https://azure.microsoft.com/en-us/services/virtual-machines/)
- [Systemd Service Management](https://www.freedesktop.org/software/systemd/man/systemd.service.html)