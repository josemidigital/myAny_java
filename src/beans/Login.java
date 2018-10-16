/*
 * Clase que vamos a utilizar para almacenar 
   los datos de usuarios registrados.
 */
package beans;

import datos.Mensaje;
import java.util.ArrayList;

/**
 *
 * @author jmmurcia
 */
public class Login {
    
    /**
     * propiedades de la clase
     */
    private String nombre;   
    private int id;
    private String foto;
    private String fechaAlta;
    private int peticionesAtendidas ;    
    private static ArrayList<Login> usuarios;
    
    /**
     * constructor vacio
     */
    Login(){
        this.nombre = "sin nombre";
        this.foto = "";
        //la fecha de alta será fecha corta formato dia mes año y hora minutos segundos.
        setFechaAlta(System.currentTimeMillis());
        agregarUsuarios(this);        
    }
    
     /**
     * constructor mínimo
     */
    
    public Login(String name, int id, long fecha){
       if(!name.isEmpty()) {
           setNombre(name);
           this.setId(id);
           this.setFechaAlta(fecha);
           //lo agrego al array
           agregarUsuarios(this);
       }
    }

    
    /**
     * getters and setters
     */
    
     public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarNombre(nombre);
    }

      public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(long fecha) {
        this.fechaAlta = Mensaje.fechaFormateada(fecha, "0");
    }

    public int getPeticionesAtendidas() {
        return peticionesAtendidas;
    }

    /**
     * este método va a incrementar el numero de peticiones de este usuario
     */
    public void setPeticionesAtendidas() {
        this.peticionesAtendidas++;
    }

    public ArrayList getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList usuarios) {
        this.usuarios = usuarios;
    }
    
   
    /**
     * metodo para validar si el nombre recibido por parámetro
     * es valido y es posible añadir el usuario a la lista.
     * @param nombre
     * @return 
     */
    private String validarNombre(String nombre) {
       String name = "";
       
       if(!nombre.trim().toLowerCase().isEmpty()){
           
           name = nombre;
       }
       
       return name;
    }

    /**
     * método que va agregando al array los usuarios
     * @param aThis 
     */
    private void agregarUsuarios(Login unUsuario) {
        
        if(usuarios == null){
            usuarios = new ArrayList();
            usuarios.add(unUsuario);
        }
        else if(usuarios != null){
            usuarios.add(unUsuario);
        }
    }
    
    /**
     * método que va a eliminar del array el usuario que
     * coincida en el nombre
     * @param aThis 
     */
    public void eliminarUsuario(String nombre) {
        
        Login tmp = null;
        
        if (usuarios != null) {
            
            for (Login user : usuarios) {
                if (user.getNombre().equalsIgnoreCase(nombre)) {
                    //igualo el usuario al temporal
                    tmp = user;
                    System.out.println("\nEncontrado el usuario '"+nombre+"'.");
                    break;
                }
            }
           if(tmp != null){
               usuarios.remove(tmp);
               System.out.println("Existen "+usuarios.size()+" usuarios.");
           } 
           
        }

    }
    
    /**
     * metodos
     */
    
    
    
}//..Login
