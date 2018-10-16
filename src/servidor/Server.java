/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import beans.Login;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import datos.Mensaje;

/**
 *
 * @author jmmurcia
 */
public class Server {
    static Server instancia;
    ServerSocket srv = null;
    static volatile HashSet<HiloServidor> hilos; //las hebras del servidor que van a estar
    //realizando peticiones.
    static boolean iniciarServidor = true;
    boolean autenticado = false;
    volatile int peticiones = 0;

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        String command = "";

        if (args.length == 2) {
            command = args[1];

            switch (command) {

                case ("-start"):

                    if (instancia == null) {
                        instancia = new Server();
                    }
                    instancia.publicarServidor();

                    break;

                default:
                      
                    break;
            }

        }
            
        
        
    }//..main()
    
    
    /**
     * 
     * @return si la instancia está iniciada y el servidor iniciado
     * para que no lo inicie de nuevo.
     */

    public static boolean isRunning(){
        return iniciarServidor && instancia!=null;
    }
    
    /**
     * 
     */
    void publicarServidor() {

        try {
            System.out.println("El servidor esta publicado.");
            int id = 1;
            srv = new ServerSocket(1800);
            hilos = new HashSet<>();
            while (iniciarServidor) {
                System.out.println("El servidor está esparando conexiones de usuarios.");

                Socket cliente = srv.accept();
                HiloServidor hilo = new HiloServidor(cliente, id);
                hilo.setName("ID_" + id);

                id++;
                hilo.start();
                System.out.println("Hay una peticion entrante 'ID_" + id
                        + " en el puerto " + cliente.getPort());

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            iniciarServidor = false;
        }

    }//..publicarServidor()

    /**
     * Clase interior que maneja los clientes que se conecten.
     */
    class HiloServidor extends Thread {

        /**
         * variables de la clase
         */
        Socket socket = null;
        DataInputStream entrada = null;
        DataOutputStream salida = null;
        int idNumber = 0;
        String peticion = ""; //variable para gestionar las peticiones de los clientes.
        Login login;
        long conecta, desconecta;

        /**
         * Constructor
         *
         * @param s
         */
        HiloServidor(Socket s, int id) throws IOException {
            this.socket = s;
            this.entrada = new DataInputStream(socket.getInputStream());
            this.salida = new DataOutputStream(socket.getOutputStream());
            autenticado = false;
            this.idNumber = id;
        }

        public DataOutputStream getSalida() {
            return this.salida;
        }

        public int getID() {
            return this.idNumber;
        }

        /**
         * Metodo que calcula el tiempo en segundos que está conectado
         *
         * @return
         */
        private String tiempoConectado() {
            String cadena = "El usuario lleva conectado ";
            long segundos = (desconecta - conecta) / 1000;

            return cadena + segundos + " segundos.";
        }

        /**
         * lo que hace el hilo constantemente
         */
        @Override
        public void run() {
            boolean esperando = false;
            /**
             * abrimos el ciclo de trabajo del hilo servidor.
             */
            while (comprobarConexion()) {
                String solicitud = "";
                if (isAlive()) {
                    System.out.println("Existen " + hilos.size() + " usuarios conectad@s.");
                    System.out.println("[" + this.getName().toUpperCase() + "] se encuentra en ejecucion");
                    try {

                        do {//mientras que no esté autenticado voy a mandar el mensaje                            
                            //con los comandos posibles.                            
                            if (!esperando) {
                                notificarMensajeAUnUsuario(servidorComandos("/help"));
                            }
                            solicitud = recibirPeticiones(entrada); //permanecemos esperando
                            esperando = true; //para salir del bucle
                        } while (!esperando && !autenticado);

                        if (peticion != null) {//si hay una peticion hay que enviarla

                            if (autenticado && comprobarConexion()) {
                                servidorResponde(peticion);

                            } //..if(autenticado)
                            else {//si no estoy autenticado 
                                notificarMensajeAUnUsuario("No está usted autenticado '" + this.getName().toLowerCase() + "'.");
                            }
                        }

                    } catch (NullPointerException npe) {
                        System.out.println("No hay ningun usuario conectado: " + npe.getMessage());
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.getLocalizedMessage());
                        cerrarConexion();
                    } finally {
                        try {
                            System.out.println("\nCompletada con exito la peticion.\nAtendidas " + peticiones++);
                            //guardo las peticiones de este cliente
                            login.setPeticionesAtendidas();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }//..isAlive()
            }//..while()
        }//..run()

        /**
         * método que va a devolver si la conexion está o no cerrada Retorna
         * true si no es cierto que es closed el socket.
         *
         * @return
         */
        private boolean comprobarConexion() {
            return !this.socket.isClosed();
        }

        //este método va a mandar cada vez que se añada un usuario una
        // cadena separada por espacios con los nombres de cada usuario
        //para que el cliente tenga la lista y pueda elegir el destinatario
        //de sus mensajes.
        private synchronized boolean agregarUsuario(String nombre) {
            try {
                if (!existeLogin(nombre)) {
                    hilos.add(this);//agrego la hebra al hashset de hilos

                    //ahora agrego el usuario
                    autenticado = true;
                    return autenticado;
                }
            } catch (NullPointerException npe) {

            } catch (Exception ex) {
                ex.printStackTrace();
                this.cerrarConexion();
            }
            return false;
        }

        /**
         * cerramos la conexion
         */
        private void cerrarConexion() {
            //To change body of generated methods, choose Tools | Templates.
            try {
                borrarCliente(this);
                System.out.println("\nSe cerrará la conexion con el cliente '" + this.getName().toUpperCase() + "'.");
                try {
                    Thread.currentThread().sleep(3000);//esperamos 3 segundos.
                    this.entrada.close();
                    this.salida.close();
                    this.socket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (IOException ioe) {
                //log.escribirError("Error en " + ioe.getLocalizedMessage());
            }
        }

        /**
         * eliminamos de la lista de login y del servidor el usuario
         *
         * @param cliente
         * @throws IOException
         */
        private synchronized void borrarCliente(HiloServidor cliente) throws IOException {
            //recorermos el HashSet en busca del 
            //hilo en concreto
            for (HiloServidor h : hilos) {
                if (h == cliente) {
                    hilos.remove(h);
                    System.out.print("\nEl cliente '" + h.getName() + "' ha sido eliminado del servidor.");
                    login.eliminarUsuario(h.getName());
                    return;
                }

            }

        }//..borrarCliente();

        /**
         * ********************************************************************
         * //////////////////////////////////////////////////////////////////////
         * REVISAR SI ES NECESARIO QUE SEA SINCRONIZADO
         *
         * @param data
         * @return
         * @throws IOException
         */
        private String servidorComandos(String data) throws IOException, InterruptedException {

            String[] args;
            args = data.split("]");//utilizamos el carácter
            StringBuilder mensaje = null;
            System.out.println("[" + args[0].toUpperCase() + "]\tPeticion recibida a las " + datos.Mensaje.fechaFormateada(new Date().getTime(), "1"));
            Mensaje msg = null;
            switch (args[0].toLowerCase()) {
                case ("/on"):
                    //queremos ver cuantos clientes hay online

                    //la respuesta la dará el servidor
                    //por tanto quiero crear una respuesta de tipo servidor
                    msg = new Mensaje(this.getName(), args[0], "Existen " + hilos.size() + " usuarios conectados.");
                    peticion = msg.toString("SERVER");
                    break;

                case ("/dir"):
                    //queremos la lista de ficheros disponibles en el servidor

                    break;

                /**
                 * caso que solicitemos la lista de comandos disponibles.
                 */
                case ("/help"):
                    mensaje = new StringBuilder();
                    mensaje.append("\nComandos disponibles:\n");
                    mensaje.append("/conectar] nombre de usuario] (logarse con el nombre de usuario).\n");
                    mensaje.append("/dir] (mostrar contenido del directorio)\n");
                    mensaje.append("/type] [nombre de fichero][mandar el contenido del fichero]\n");
                    mensaje.append("/who [muestra el nombre de las personas conectadas]\n");
                    mensaje.append("/on [numero de personas conectadas]\n");
                    mensaje.append("/send [nombrecliente] [mensaje][mandar un mensaje al cliente especificado"
                            + "]\n");
                    mensaje.append("/exit [salir normalmente del servidor]\n");
                    mensaje.append("/help [muestra este menú de ayuda]\n");
                    peticion = (mensaje.toString());
                    break;

                case ("/who"):
                    //obtener la lista de usuarios conectados
                    peticion = listarUsuarios();

                    break;

                case ("/send"):
                    //enviar mensajes

                    break;

                case ("/conectar"):
                    //conectarse al servidor
                    mensaje = new StringBuilder();
                    String nombre = "";
                    if (args.length >= 1) {
                        nombre = args[1];
                    }
                    if (agregarUsuario(nombre)) {
                        this.setName(nombre.toUpperCase());
                        mensaje.append("El login '" + this.getName().toUpperCase()
                                + "' se ha agregado al servidor con el ID: " + this.getID() + ".");
                        //establecemos el tiempo de conexion
                        conecta = new Date().getTime();
                        if (autenticado) {
                            login = new Login(this.getName(), this.getID(), conecta);
                            msg = new Mensaje(this.getName(), args[0].toUpperCase(), "Se ha agregado un nuevo jugador al Servidor "
                                    + "'" + this.getName() + "'.");
                            notificarMensajeAUnUsuario("\nBienvenido de nuevo '" + this.getName().toUpperCase() + "'\n" + mensaje
                                    + "\n");
                            notificarMensajeATodos(msg.toString("SERVER"));

                            //ahora podría agregar para llenar la etiqueta con el numero de usuarios conectados
                            peticion = null;//se pone a null para que no envie el mensaje el métod run()
                        }

                    }

                    break;

                case ("/get"):

                    break;

                case ("/time"):
                    //caso querer indicar el tiempo en segundos
                    peticion = this.tiempoConectado();

                    break;

                default:
                    peticion = "Servidor: Comando no reconocido";
                    break;

            }

            return peticion;
        }

        /**
         * Este método criba las peticiones por el comando que recibimos
         *
         * @param peticion
         * @param argumentos
         * @return
         * @throws IOException
         */
        private boolean existeLogin(String nombre) {
            boolean existe = false;

            for (HiloServidor login : hilos) {
                System.out.println("Usuario encontrado : '" + login.getName() + "' ID:" + login.getID());
                if (login.getName().equalsIgnoreCase(nombre)) {
                    System.out.println("El usuario " + this.getName() + " ya existe en el servidor.");
                    return true; //retorna true si existe el nombre 
                }
            }
            return existe;
            //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * Método que notifica a todos los usuarios conectados con algún tipo de
         * mensaje.
         *
         * @param msg
         * @throws IOException
         */
        private void notificarMensajeATodos(String msg) throws IOException {
            //String msg = "El usuario '"+this.getName()+ "' se ha unido al servidor.";
            //Mensaje mensaje = new Mensaje();           
            //StringBuilder stb = new StringBuilder();
            //stb.append("server:").append(msg);

            //mensaje.establecerMensaje("server:", mensaje.toString(), "1");
            if (hilos.size() > 1) {
                for (HiloServidor usuario : hilos) {
                    //para que no mande el mensaje a si mismo
                    if (!usuario.getName().equalsIgnoreCase(this.getName())) {
                        usuario.notificarMensajeAUnUsuario(msg);
                    }
                }
            }
        }

        /**
         * Metodo que manda el mensaje al usuario recibido
         *
         * @param user
         * @param msg
         * @throws IOException
         */
        private void notificarMensajeAUnUsuario(String msg) throws IOException {
            if (!msg.isEmpty()) {
                servidorResponde(msg);
            }
        }

        /**
         * Este método devuelve el objeto cuyo nombre coincide con el nombre que
         * recibe per parámetro.
         *
         * @param user
         * @return
         */
        private HiloServidor buscarNombreUsuario(String user) {

            for (HiloServidor h : hilos) {
                //System.out.println(h.getNombre());
                if (h.getName().equalsIgnoreCase(user)) {
                    return h;
                }
            }

            return null;

        }

        private String crearListaUsuarios() throws IOException {
            StringBuilder stb = new StringBuilder();
            for (HiloServidor h : hilos) {
                stb.append(h.getName().toUpperCase());
                // System.out.println(h.getNombre());
                stb.append(" ");
            }

            return stb.toString();
        }

        /**
         * método que recibe el array y devuelve la suma de todos separados por
         * espacios.
         */
        public String toString(String[] array) {
            StringBuilder cadena = new StringBuilder();

            for (String valor : array) {
                cadena.append(valor + " ");
            }
            return cadena.toString();
        }

        public void servidorResponde(String mensaje) throws IOException {

            String msg = "[SERVER]-->[" + this.getName() + "]:" + mensaje;//crea un objeto mensaje con uno de los parámetros _Servidor_
            System.out.println(msg);
            salida.writeUTF(msg);//se graba al flujo
        }

        /**
         * Método que va a filtrar las peticiones
         *
         * @param entrada
         * @return response: String que contiene la peticion de los clientes.
         */
        private String recibirPeticiones(DataInputStream entrada) {
            String datos = "";
            String comando = "";
            try {
                String[] args;
                datos = entrada.readUTF();//leemos del flujo y paramos el hilo
                args = datos.split("]");//vamos a usar el separador de comandos ]
                comando = args[0];
                //filtramos el comando
                if (!comando.equalsIgnoreCase("/exit")) {//si no es salir
                    String peticion = filtrarPeticion(datos);//le paso los argumentos
                    servidorComandos(peticion);
                    //los mensajes obtenidos se toman como la respuesta a notificar al cliente

                } else {//si quiere salir

                    //y le cerramos
                    desconecta = new Date().getTime();
                    Mensaje msg = new Mensaje(this.getName(), comando, this.tiempoConectado());

                    notificarMensajeAUnUsuario("Cliente: " + socket.getInetAddress()
                            + " solicita abandonar el servidor\n"
                            + msg.toString("server"));
                    //notificarMensajeATodos(msg.toString());
                    cerrarConexion();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return datos;
        }

        private String filtrarPeticion(String datos) throws IOException, InterruptedException {
            StringBuilder str = new StringBuilder();
            String args[] = datos.split("]");
            String comando = args[0].toLowerCase();

            if (!comando.isEmpty()) {

                switch (comando.toLowerCase()) {

                    case ("/conectar"):

                        if (args.length > 1 && !existeLogin(args[1])) {
                            comando = str.append(comando).append("]").append(args[1]).toString();
                        }
                        break;

                    default:
                        comando = datos;
                        break;

                }

            }
            return comando;
        }

        private String listarUsuarios() {
            StringBuilder cadena = new StringBuilder();
            for (HiloServidor hilo : hilos) {
                cadena.append(hilo.getName().toUpperCase() + "\n");

            }
            return cadena.toString();
        }

    }//..clase interior HiloServidor

}//final de la clase Servidor
