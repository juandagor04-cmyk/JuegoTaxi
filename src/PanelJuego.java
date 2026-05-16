import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

public class PanelJuego extends JPanel implements ActionListener, KeyListener, MouseListener {

    private Taxi taxi;
    private Mapa mapa;
    private Cliente cliente;
    private Rectangle destinoPasajero;
    private boolean tienePasajero = false;
    private String nombreJugador;

    private enum Estado { JUGANDO, PAUSADO, GAMEOVER }
    private Estado estadoActual = Estado.JUGANDO;

    private ArrayList<SenalTransito> senales;
    private ArrayList<CarroEnemigo> enemigos;

    private Timer timer;
    private Random random;

    private boolean izquierda = false, derecha = false, adelante = false, atras = false;
    private int anchoPantalla = 800;
    private int altoPantalla = 600;

    private long tiempoInicioPare = 0;
    private boolean evaluandoPare = false;
    private Rectangle zonaPareActual = null;

    private final Color AMARILLO_TAXI = new Color(253, 216, 53);

    public PanelJuego(String nombre) {
        this.nombreJugador = nombre;
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        setBackground(new Color(30,30,35));
        random = new Random();

        mapa = new Mapa();
        reiniciarJuego();

        timer = new Timer(40, this);
        timer.start();
    }

    private void reiniciarJuego() {
        taxi = new Taxi(anchoPantalla / 2, altoPantalla / 2, 0);
        tienePasajero = false;
        destinoPasajero = null;
        generarMundo();
        estadoActual = Estado.JUGANDO;
    }

    private void generarMundo() {
        senales = mapa.generarSenalesSeguras(6);
        enemigos = mapa.generarEnemigosSeguros(4);
        if (!tienePasajero) {
            cliente = mapa.generarClienteSeguro();
        }
    }

    private void generarDestino() {
        int x = random.nextBoolean() ? 150 : anchoPantalla - 150;
        int y = random.nextBoolean() ? 150 : altoPantalla - 150;
        destinoPasajero = new Rectangle(x, y, 50, 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        mapa.dibujar(g, getWidth(), getHeight());

        if (cliente != null && !tienePasajero) cliente.dibujar(g);

        if (tienePasajero && destinoPasajero != null) {
            g2d.setColor(new Color(0, 255, 0, 100));
            g2d.fill(destinoPasajero);
            g2d.setColor(Color.GREEN);
            g2d.draw(destinoPasajero);
        }

        for (SenalTransito s : senales) s.dibujar(g);
        for (CarroEnemigo e : enemigos) e.dibujar(g);

        taxi.dibujar(g);
        dibujarFlechaDestino(g2d);
        dibujarInterfaz(g);

        // Capas de Menú
        if (estadoActual == Estado.PAUSADO) dibujarMenuPausa(g2d);
        if (estadoActual == Estado.GAMEOVER) dibujarMenuChoque(g2d);
    }

    private void dibujarFlechaDestino(Graphics2D g2d) {
        int targetX = 0, targetY = 0;
        if (!tienePasajero && cliente != null) {
            targetX = cliente.getBounds().x; targetY = cliente.getBounds().y;
        } else if (tienePasajero && destinoPasajero != null) {
            targetX = destinoPasajero.x; targetY = destinoPasajero.y;
        } else return;

        int tx = taxi.getX(); int ty = taxi.getY();
        double angulo = Math.atan2(targetY - ty, targetX - tx);

        AffineTransform old = g2d.getTransform();
        g2d.translate(tx + (int)(Math.cos(angulo) * 60), ty + (int)(Math.sin(angulo) * 60));
        g2d.rotate(angulo);
        g2d.setColor(Color.GREEN);
        g2d.fillPolygon(new int[]{0, -15, -15}, new int[]{0, -10, 10}, 3);
        g2d.setTransform(old);
    }

    private void dibujarInterfaz(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(10, 10, 220, 110);
        g.setColor(AMARILLO_TAXI);
        g.drawRect(10, 10, 220, 110);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("CONDUCTOR: " + nombreJugador, 20, 30);
        g.setColor(Color.WHITE);
        g.drawString("PUNTOS: " + taxi.getPuntos(), 20, 55);
        g.drawString("VELOCIDAD: " + taxi.getVelocidad() + " km/h", 20, 80);
        g.setColor(tienePasajero ? Color.GREEN : AMARILLO_TAXI);
        g.drawString(tienePasajero ? "¡LLEVA AL PASAJERO!" : "BUSCANDO SERVICIO...", 20, 105);
    }

    private void dibujarMenuPausa(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(AMARILLO_TAXI);
        g2d.setFont(new Font("Impact", Font.PLAIN, 50));
        g2d.drawString("TURNO PAUSADO", getWidth()/2 - 160, getHeight()/2 - 120);

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        dibujarBoton(g2d, "CONTINUAR", getHeight()/2 - 50, Color.DARK_GRAY);
        dibujarBoton(g2d, "REINICIAR TURNO", getHeight()/2 + 20, new Color(50, 50, 150));
        dibujarBoton(g2d, "SALIR A LA CENTRAL", getHeight()/2 + 90, new Color(150, 0, 0));
    }

    private void dibujarMenuChoque(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(new Color(255, 50, 50));
        g2d.setFont(new Font("Impact", Font.PLAIN, 50));
        g2d.drawString("¡SERVICIO TERMINADO!", getWidth()/2 - 230, getHeight()/2 - 80);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        dibujarBoton(g2d, "REINTENTAR", getHeight()/2, Color.DARK_GRAY);
        dibujarBoton(g2d, "SALIR A LA CENTRAL", getHeight()/2 + 70, new Color(150, 0, 0));
    }

    private void dibujarBoton(Graphics2D g2d, String texto, int y, Color color) {
        int anchoB = 280; int altoB = 50; int x = getWidth()/2 - anchoB/2;
        g2d.setColor(color); g2d.fillRoundRect(x, y, anchoB, altoB, 15, 15);
        g2d.setColor(Color.WHITE); g2d.drawRoundRect(x, y, anchoB, altoB, 15, 15);
        g2d.drawString(texto, x + 40, y + 32);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (estadoActual == Estado.JUGANDO) actualizarLogica();
        repaint();
    }

    private void actualizarLogica() {
        if (adelante) taxi.acelerar(); else if (atras) taxi.frenar(); else taxi.aplicarFriccion();
        if (izquierda) taxi.girarIzquierda();
        if (derecha) taxi.girarDerecha();

        taxi.moverAdelante(); // Asegura el movimiento físico

        if (mapa.chocaConEstructura(taxi.getBounds())) {
            estadoActual = Estado.GAMEOVER;
            return;
        }

        for (CarroEnemigo enemigo : enemigos) {
            enemigo.mover(mapa, getWidth(), getHeight());
            if (taxi.getBounds().intersects(enemigo.getBounds())) {
                estadoActual = Estado.GAMEOVER;
                return;
            }
        }

        verificarSenales();
        verificarPasajero();
        manejarCambioEscenario();
    }

    private void verificarSenales() {
        for (SenalTransito s : senales) {
            Rectangle zonaInfluencia = new Rectangle(s.getX() - 40, s.getY() - 40, 80, 80);
            if (taxi.getBounds().intersects(zonaInfluencia)) {
                if (taxi.getVelocidad() > 5 && !evaluandoPare) {
                    evaluandoPare = true;
                    tiempoInicioPare = System.currentTimeMillis();
                    zonaPareActual = zonaInfluencia;
                }
            }
        }

        if (evaluandoPare) {
            if (taxi.getVelocidad() <= 5) {
                evaluandoPare = false;
            } else if (zonaPareActual != null && !zonaPareActual.intersects(taxi.getBounds())) {
                if (System.currentTimeMillis() - tiempoInicioPare < 4000) {
                    taxi.restarPuntos(50);
                }
                evaluandoPare = false;
            }
        }
    }

    private void verificarPasajero() {
        if (!tienePasajero && cliente != null && taxi.getBounds().intersects(cliente.getBounds()) && taxi.getVelocidad() < 10) {
            cliente.recoger();
            tienePasajero = true;
            generarDestino();
        }
        if (tienePasajero && destinoPasajero != null && taxi.getBounds().intersects(destinoPasajero) && taxi.getVelocidad() < 10) {
            taxi.sumarPuntos(100);
            tienePasajero = false;
            destinoPasajero = null;
            cliente = mapa.generarClienteSeguro();
        }
    }

    private void manejarCambioEscenario() {
        boolean cambio = false; int lado = -1;
        if (taxi.getX() > getWidth()) { taxi.setX(0); cambio = true; lado = 2; }
        else if (taxi.getX() < 0) { taxi.setX(getWidth()); cambio = true; lado = 3; }
        else if (taxi.getY() > getHeight()) { taxi.setY(0); cambio = true; lado = 1; }
        else if (taxi.getY() < 0) { taxi.setY(getHeight()); cambio = true; lado = 0; }

        if (cambio) {
            mapa.generarMapa(lado);
            generarMundo();
            if (tienePasajero) generarDestino();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        int centroX = getWidth() / 2;

        if (estadoActual == Estado.PAUSADO) {
            if (mx > centroX - 140 && mx < centroX + 140) {
                if (my > getHeight()/2 - 50 && my < getHeight()/2) estadoActual = Estado.JUGANDO; // Continuar
                else if (my > getHeight()/2 + 20 && my < getHeight()/2 + 70) reiniciarJuego(); // Reiniciar
                else if (my > getHeight()/2 + 90 && my < getHeight()/2 + 140) salir(); // Salir
            }
        } else if (estadoActual == Estado.GAMEOVER) {
            if (mx > centroX - 140 && mx < centroX + 140) {
                if (my > getHeight()/2 && my < getHeight()/2 + 50) reiniciarJuego();
                else if (my > getHeight()/2 + 70 && my < getHeight()/2 + 120) salir();
            }
        }
    }

    private void salir() {
        Window win = SwingUtilities.getWindowAncestor(this);
        if (win != null) {
            win.dispose();
            new Menu().setVisible(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int t = e.getKeyCode();
        if (t == KeyEvent.VK_ESCAPE) { // Tecla ESC para pausar
            if (estadoActual == Estado.JUGANDO) estadoActual = Estado.PAUSADO;
            else if (estadoActual == Estado.PAUSADO) estadoActual = Estado.JUGANDO;
        }
        if (t == KeyEvent.VK_W || t == KeyEvent.VK_UP) adelante = true;
        if (t == KeyEvent.VK_S || t == KeyEvent.VK_DOWN) atras = true;
        if (t == KeyEvent.VK_A || t == KeyEvent.VK_LEFT) izquierda = true;
        if (t == KeyEvent.VK_D || t == KeyEvent.VK_RIGHT) derecha = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int t = e.getKeyCode();
        if (t == KeyEvent.VK_W || t == KeyEvent.VK_UP) adelante = false;
        if (t == KeyEvent.VK_S || t == KeyEvent.VK_DOWN) atras = false;
        if (t == KeyEvent.VK_A || t == KeyEvent.VK_LEFT) izquierda = false;
        if (t == KeyEvent.VK_D || t == KeyEvent.VK_RIGHT) derecha = false;
    }

    // Métodos obligatorios vacíos
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}