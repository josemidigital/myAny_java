/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author jmmurcia
 */
public class Mensaje implements Serializable {    
    final static String LOG = "log.txt";
    static PrintWriter pw;    
    final static String FECHA_CORTA = "dd/MM/yyyy HH:mm:ss";
    
    final static String HORA_CORTA = "HH:mm:ss";
    String user = ""; //determinar quien envia el mensaje
    String comando = "";
    String mensaje = "";
    String fecha = ""; 
    /**
     * Constructor de la clase
     */
    public Mensaje(){
        this.user = "desconocido";
        this.comando = "";
        this.mensaje = "";
    }

    public Mensaje(String name, String comando, String cadena) {
        this.user = name;
        this.comando = comando;
        
        this.mensaje = establecerMensaje(cadena,"1");
        
    }
   
    
    public String establecerMensaje(String msg, String tipoFecha){
               
        try{
            fecha = fechaFormateada(new Date().getTime(),tipoFecha);
            StringBuilder stb = new StringBuilder();
            stb.append("["+comando+"]: ");
            stb.append("<"+msg+">");
            this.mensaje = stb.toString();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
          //  pw.close();        
        }
        return mensaje;
    }
        
    public void guardarMensaje() throws IOException{
        pw = new PrintWriter(new FileWriter(LOG,true),true);
        pw.write(mensaje);
        pw.close();
    }
    
    /**
     * Metodo que recibe una fecha 
     * @return 
     */
    public static String fechaFormateada(Long fechaLong, String formato){
        
        String fechaForm = "";
        Long date = fechaLong;
        Date fecha = null;
        SimpleDateFormat sdf = null;
        try{
            
            if(date == null) {
                fecha = new Date();
            }
            else {
                switch (formato) {

                    case ("0"):
                       sdf = new SimpleDateFormat(FECHA_CORTA);
                         //se supone que recibo un Long               
                        fechaForm = sdf.format(new Date(date));
                        
                        break;

                    case ("1")://hora corta
                        sdf = new SimpleDateFormat(HORA_CORTA);
                         //se supone que recibo un Long               
                        fechaForm = sdf.format(new Date(date));
                        
                        break;
                        
                    default:
                        fechaFormateada(System.currentTimeMillis(),"1");
                        break;
                }               
            }            
        }
        catch(Exception ex){
            ex.getLocalizedMessage();
        }
        return fechaForm;
        
    }
    
   
     /**
     * Metodo sobrescrito que devuelve una cadena 
     * construida con un StringBuider
     * de formato [06/04/2016 21:03:15]:Jugador_1
     *
     * @return String str.toString(). Devuelve la cadena construida a String.
     */
    public String toString(String remite) {
        StringBuilder str = new StringBuilder();
        str.append("["+remite+": "+fecha+" --> ");
        str.append(user).append("]\n");
        
        str.append(mensaje);
        return str.toString();
    }

    
}//final de la clase
