/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;

/**
 *
 * @author joseMiguelMurcia
 */
public class ArrayAdapter extends AbstractListModel {
    
    ArrayList<String> lista = new ArrayList<>();
    
    ImageIcon icono = new ImageIcon(this.getClass().getResource("/img/images.png"));
    
   
    @Override
    public int getSize() {
        return lista.size();
    }

    
    @Override
    public Object getElementAt(int index) {
        String user = lista.get(index);
        return user;
    }
    
    public void addNotificacion(String user){
        lista.add(user);        
        this.fireIntervalAdded(this, this.getSize(), getSize()+1);
    }
    
    
   
//
//    @Override
//    public void valueChanged(ListSelectionEvent e) {
//        //To change body of generated methods, choose Tools | Templates.
//        JOptionPane.showMessageDialog(null, null, "Mensaje: "+e.getSource(), JOptionPane.INFORMATION_MESSAGE, icono);
//    }

   

   

    
}//..
