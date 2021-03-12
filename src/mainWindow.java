import javax.swing.*;

public class mainWindow {
     public static void main(String args[]) {
         JFrame frame = new JFrame("Mi primera GUI");
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setSize(300, 300);
         JButton button = new JButton("Presionar");
         frame.getContentPane().add(button); // Agrega el botón al panel de contenido del marco
         frame.setVisible(true);
     }

}
