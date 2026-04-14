import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PanelJuego extends JPanel implements ActionListener, KeyListener {

    private Taxi taxi;
    private Mapa mapa;
    private Cliente cliente;

    private ArrayList<SenalTransito> senales;
    private ArrayList<CarroEnemigo> enemigos;

    private Timer timer;

    public PanelJuego() {
        setFocusable(true);
        addKeyListener(this);

        taxi = new Taxi(200, 300, 5);
        mapa = new Mapa();
        cliente = new Cliente(mapa.getCarrilALetatorio(), 100);

        senales = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int tipo = (int) (Math.random() * 3);
            int subtipo = (int) (Math.random() * 5);

            int x = (int)(Math.random() * 400);
            int y = (int)(Math.random() * 400);

            senales.add(new SenalTransito(x, y, tipo, subtipo));
        }

        enemigos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            enemigos.add(new CarroEnemigo(mapa.getCarrilALetatorio(), 0, 3));
        }

        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    public void addNotify(){
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        mapa.dibujar(g, getWidth(), getHeight());
        cliente.dibujar(g);

        for (SenalTransito s : senales) {
            s.dibujar(g);
        }

        for (CarroEnemigo e : enemigos) {
            e.dibujar(g);
        }

        taxi.dibujar(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        int[] carriles = mapa.getCarriles();

        for (CarroEnemigo enemigo : enemigos) {
            enemigo.mover(getHeight(), carriles);

            if (taxi.getBounds().intersects(enemigo.getBounds())) {
                JOptionPane.showMessageDialog(this, "Choque!");
                reiniciarJuego();
                return;
            }
        }

        for (SenalTransito s : senales) {
            s.actualizar();
            s.aplicarReglas(taxi);
        }

        // Recoger cliente
        if (!cliente.fueRecogido() && taxi.getBounds().intersects(cliente.getBounds())) {
            cliente.recoger();
            JOptionPane.showMessageDialog(this, "Cliente recogido!");
        }
        taxi.limitarMovimiento(getHeight(), getWidth());

        repaint();
    }

    private void reiniciarJuego() {
        taxi = new Taxi(200, 300, 5);

        mapa.generarMapa();

        cliente = new Cliente(mapa.getCarrilALetatorio(), 100); // opcional pero recomendado

        enemigos.clear();
        for(int i = 0; i < 4; i++){
            enemigos.add(new CarroEnemigo(mapa.getCarrilALetatorio(),0,3));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int tecla  = e.getKeyCode();
        int v = taxi.getVelocidad();

        if (tecla == KeyEvent.VK_LEFT) {
            taxi.mover(-v, 0);
        }
        if (tecla == KeyEvent.VK_RIGHT){
            taxi.mover (v,0);
        }
        if (tecla == KeyEvent.VK_UP){
            taxi.mover (0,-v);
        }
        if (tecla == KeyEvent.VK_DOWN){
            taxi.mover (0,v);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}