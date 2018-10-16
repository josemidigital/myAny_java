/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acciones;

import vistas.PanelCliente;
import java.awt.Component;
import java.awt.Container;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author jmmurcia
 */
public class Accion extends Thread {
    
    static Container contenedor;
    static JFrame ventana = null;    
    JLabel etiqueta;

    /**
     * constructor
     *
     * @param contenedor
     */
    public Accion(Container contenedor, JFrame ventana) {
        this.contenedor = contenedor;
        buscarComponente();//este método instancia la etiqueta        
        this.ventana = ventana;
        //this.ventana.setLocationRelativeTo(etiqueta);        
    }

    /**
     * Metodo que anima una ventana
     */
    public void animarVentana() {
        //lo que quiero que haga otro hilo aparte             
        //es ir pintando o repintando la nueva posicion del aviso.
        //ventana.setLocation(this.etiqueta.getX(),this.etiqueta.getY());
        ventana.setLocationRelativeTo(etiqueta);
        ventana.setVisible(true);

        int ancho = this.etiqueta.getWidth();
        //hay que posicionar la etiqueta
        System.out.println("_Ventana: " + ventana.getContentPane().getBounds());
        System.out.println("_Etiqueta: " + etiqueta.getBounds());
        System.out.println("_Panel: " + contenedor.getBounds());
        //el ancho de la ventana que mostramos.
        int anchoVentana = ventana.getWidth();
        //int altoVentana = ventana.getHeight();

        int distancia = (ancho - anchoVentana);

        int posX = contenedor.getX();//posicion X de la etiqueta
        int posY = ventana.getY();

        int dx = posX; //distancia que recorre y parte de 

        for (int x = 0; x < distancia; x++) {
            
            try {
                dx = (posX + x);
                sleep(250);
                //repinta cada cuarto de segundo
                ventana.setLocation(dx, posY);//, anchoVentana, altoVentana);
                System.out.println("Repintando..(dx) = " + dx + " de " + "" + distancia);
                // this.ventana.pack();
                this.ventana.validate();
                this.ventana.repaint();

            } catch (InterruptedException ex) {
                Logger.getLogger(PanelCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //y cierro la pantalla
        JOptionPane.showMessageDialog(ventana.getContentPane(), "Mensaje finalizando animar la vista.", "Cerrando el aviso...", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("Cierro el mensaje..");
        ventana.dispose();
    }//..animarVentana()

    /**
     * lo que hace el hilo
     */
    @Override
    public void run() {
        this.animarVentana();

    }//..animarVentana()

    /**
     * este método recorre los componentes del contenedor en busca de un JLabel
     *
     * @return
     */
    private void buscarComponente() {
       
        //y por último 
        for (Component componente : contenedor.getComponents()) {

            if (componente instanceof JLabel) {
                etiqueta = (JLabel) componente;
                System.out.println(etiqueta.getBounds());
                break;
            }
            
        }
        //return etiqueta;
    }//..buscarJLabel()

}//..final de la clase Accion
