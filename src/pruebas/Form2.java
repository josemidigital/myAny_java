package pruebas;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class Form2 extends JFrame {
 
    private JPanel panel;
    private JButton button1;
    private JButton button2;
     
    public Form2() {
        super("Table demo");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         
        setLayout(new BorderLayout());
         
        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 10, 10));
        button1 = new JButton("Button 1");
        button2 = new JButton("Button 2");
        panel.add(button1);
        panel.add(button2);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
         
        JTable table = new JTable(new MyModel());
        table.getColumnModel().getColumn(0).setCellRenderer(new MyRenderer());
        table.setRowHeight(40);
         
        add(new JScrollPane(table), BorderLayout.CENTER);
 
        pack();
        setLocationRelativeTo(null);
    }
 
    private static class MyModel extends AbstractTableModel {
 
        @Override
        public int getRowCount() {
            return 100;
        }
 
        @Override
        public int getColumnCount() {
            return 1;
        }
 
        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            return String.valueOf(rowIndex);
        }
    }
     
    private class MyRenderer extends DefaultTableCellRenderer {
 
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column)
        {
            button1.setText("Row " + value.toString());
            return panel;
        }
    }
     
    public static void main(String[] args) {
        new Form2().setVisible(true);
    }
}