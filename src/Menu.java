import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame implements ActionListener{

    private JTextField txtNombre;
    private JComboBox<String>comboDificultad;
    private JButton btnIniciar;

    public Menu(){
        setTitle("Juego");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //Titulo
        JLabel lblTitulo = new JLabel("Bienbenido", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitulo,BorderLayout.NORTH);

        //Panel Central
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new GridLayout(3,2,10,10));

        //Nombre
        panelCentro.add(new JLabel("Nombre del jugador:"));
        txtNombre = new JTextField();
        panelCentro.add(txtNombre);

        //Dificultad
        panelCentro.add(new JLabel("Dificultad"));
        comboDificultad = new JComboBox<>();
        comboDificultad.addItem("Facil");
        comboDificultad.addItem("Medio");
        comboDificultad.addItem("Dificil");
        panelCentro.add(comboDificultad);

        add(panelCentro, BorderLayout.CENTER);

        //Boton iniciar
        btnIniciar = new JButton ("iniciar");
        btnIniciar.addActionListener(this);
        add(btnIniciar, BorderLayout.SOUTH);
    }
  @Override
    public void actionPerformed(ActionEvent e){
        String nombre = txtNombre.getText();
        String dificultad = (String) comboDificultad.getSelectedItem();
        if (nombre.isEmpty()){
            JOptionPane.showMessageDialog(this, "Ingresa tu nombre");
        }else{
            JOptionPane.showMessageDialog(this, "Jugador:" + nombre + "/nDificultad:" + dificultad);

            //Conexion del juego
        }
  }
  public static void main(String [] args){
            new Menu().setVisible(true);

  }
}

