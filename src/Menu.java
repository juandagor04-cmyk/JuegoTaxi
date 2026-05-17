import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.border.EmptyBorder;
import java.util.HashMap;
import java.util.Map;

public class Menu extends JFrame implements ActionListener {

    private JTextField txtNombre;
    private JComboBox<String> comboDificultad;
    private JButton btnIniciar, btnEstadisticas, btnSalir;

    // El registro es estático para que persista aunque se cierren y abran menús
    private static Map<String, Integer> registroPuntos = new HashMap<>();
    private final Color AMARILLO_TAXI = new Color(253, 216, 53);
    private final Color NEGRO_URBANO = new Color(33, 33, 33);

    public Menu() {
        setTitle("TaxiGo - Menu Principal");
        setSize(450, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AMARILLO_TAXI);
        setLayout(new BorderLayout());

        // Encabezado
        JPanel panelNorte = new JPanel();
        panelNorte.setBackground(NEGRO_URBANO);
        panelNorte.setPreferredSize(new Dimension(450, 80));

        JLabel lblTitulo = new JLabel("TaxiGo", JLabel.CENTER);
        lblTitulo.setForeground(AMARILLO_TAXI);
        lblTitulo.setFont(new Font("Impact", Font.ITALIC, 45));
        panelNorte.add(lblTitulo);
        add(panelNorte, BorderLayout.NORTH);

        // Panel Central
        JPanel panelCentro = new JPanel();
        panelCentro.setOpaque(false);
        panelCentro.setLayout(new GridLayout(5, 1, 10, 10));
        panelCentro.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel lblNombre = new JLabel("Nombre del Conductor:", JLabel.LEFT);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
        panelCentro.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        txtNombre.setBorder(BorderFactory.createLineBorder(NEGRO_URBANO, 2));
        panelCentro.add(txtNombre);

        JLabel lblDificultad = new JLabel("Dificultad del Tráfico:", JLabel.LEFT);
        lblDificultad.setFont(new Font("Arial", Font.BOLD, 14));
        panelCentro.add(lblDificultad);

        comboDificultad = new JComboBox<>(new String[]{"Fácil (Pueblo)", "Medio (Ciudad)", "Difícil (Hora Pico)"});
        comboDificultad.setBackground(Color.WHITE);
        panelCentro.add(comboDificultad);

        add(panelCentro, BorderLayout.CENTER);

        // --- BOTONES (SUR) ---
        JPanel panelSur = new JPanel();
        panelSur.setOpaque(false);
        panelSur.setLayout(new GridLayout(3, 1, 5, 5));
        panelSur.setBorder(new EmptyBorder(0, 50, 30, 50));

        btnIniciar = crearBotonEstilizado("INICIAR VIAJE", NEGRO_URBANO, AMARILLO_TAXI);
        btnEstadisticas = crearBotonEstilizado("ESTADÍSTICAS", Color.WHITE, NEGRO_URBANO);
        btnSalir = crearBotonEstilizado("ABANDONAR JUEGO", new Color(180, 0, 0), Color.WHITE);

        panelSur.add(btnIniciar);
        panelSur.add(btnEstadisticas);
        panelSur.add(btnSalir);

        add(panelSur, BorderLayout.SOUTH);
    }

    private JButton crearBotonEstilizado(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(NEGRO_URBANO, 1));
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSalir) {
            System.exit(0);
        }

        if (e.getSource() == btnEstadisticas) {
            mostrarEstadisticas();
        }

        if (e.getSource() == btnIniciar) {
            String nombre = txtNombre.getText().trim().toUpperCase();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "¡Oye! El taxi no arranca sin nombre.");
            } else {
                // Iniciar juego
                JFrame ventanaJuego = new JFrame("TaxiGo - En Servicio");
                PanelJuego panelJuego = new PanelJuego(nombre);

                ventanaJuego.add(panelJuego);
                ventanaJuego.setSize(800, 600);
                ventanaJuego.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventanaJuego.setLocationRelativeTo(null);

                // --- LÓGICA DE RETORNO AL MENÚ ---
                ventanaJuego.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        // Al cerrar el juego, creamos un nuevo menú para ver los puntos actualizados
                        new Menu().setVisible(true);
                    }
                });

                ventanaJuego.setVisible(true);

                // Cerramos el menú actual
                this.dispose();
            }
        }
    }

    private void mostrarEstadisticas() {
        StringBuilder sb = new StringBuilder("--- RANKING DE CONDUCTORES ---\n\n");
        if (registroPuntos.isEmpty()) {
            sb.append("Aún no hay registros en la central.");
        } else {
            // Ordenar por puntaje si quieres (opcional)
            registroPuntos.forEach((name, score) -> {
                sb.append("🚕 ").append(name).append(": ").append(score).append(" pts\n");
            });
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Estadísticas TaxiGo", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método que llamará PanelJuego al morir o terminar
    public static void guardarPuntuacion(String nombre, int puntos) {
        // Guardamos o actualizamos si el nuevo puntaje es mayor
        if (!registroPuntos.containsKey(nombre) || puntos > registroPuntos.get(nombre)) {
            registroPuntos.put(nombre, puntos);
        }
    }

    public static void main(String[] args) {
        // Datos de prueba iniciales
        registroPuntos.put("PEPE", 1500);
        registroPuntos.put("LUCHO", 2300);

        SwingUtilities.invokeLater(() -> new Menu().setVisible(true));
    }
}