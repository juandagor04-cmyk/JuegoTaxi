import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class PanelJuego extends JPanel implements ActionListener, KeyListener, MouseListener {

    private Taxi taxi;
    private Mapa mapa;
    private Cliente cliente;
    private String nombreJugador; // Para guardar el récord

    private enum Estado { JUGANDO, PAUSADO, GAMEOVER }
    private Estado estadoActual = Estado.JUGANDO;

    private ArrayList<SenalTransito> senales;
    private ArrayList<CarroEnemigo> enemigos;

    private Timer timer;
    private Random random;

    private boolean izquierda = false, derecha = false, adelante = false, atras = false;
    private int anchoPantalla = 800;
    private int altoPantalla = 600;

    // Colores TaxiGo
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
        taxi = new Taxi(anchoPantalla / 2, altoPantalla / 2, 5);
        generarMundo(); // Aquí se inicializan las listas
        estadoActual = Estado.JUGANDO;
    }

    private void generarMundo() {
        // --- SOLUCIÓN AL NULL POINTER: Inicializar las listas antes de usarlas ---
        senales = new ArrayList<>();
        enemigos = new ArrayList<>();

        generarClienteEnAnden();

        int tipoMapa = mapa.getTipoMapa();
        for (int i = 0; i < 6; i++) {
            int tipo = random.nextInt(3);
            int x, y;
            if (tipoMapa == 0) {
                x = 100 + random.nextInt(anchoPantalla - 200);
                y = (random.nextBoolean()) ? mapa.getCalleLimiteSuperior() - 25 : mapa.getCalleLimiteInferior() + 5;
            } else if (tipoMapa == 1) {
                x = (random.nextBoolean()) ? mapa.getCalleLimiteIzquierdo() - 25 : mapa.getCalleLimiteDerecho() + 5;
                y = 100 + random.nextInt(altoPantalla - 200);
            } else {
                x = (random.nextBoolean()) ? 400 - 80 : 400 + 80;
                y = (random.nextBoolean()) ? 300 - 80 : 300 + 80;
            }
            senales.add(new SenalTransito(x, y, tipo, random.nextInt(5)));
        }
        generarEnemigos();
    }

    private void generarClienteEnAnden() {
        int tipoMapa = mapa.getTipoMapa();
        int x, y;
        if (tipoMapa == 0) {
            y = random.nextBoolean() ? mapa.getCalleLimiteSuperior() - 35 : mapa.getCalleLimiteInferior() + 10;
            x = 150 + random.nextInt(anchoPantalla - 300);
        } else if (tipoMapa == 1) {
            x = random.nextBoolean() ? mapa.getCalleLimiteIzquierdo() - 35 : mapa.getCalleLimiteDerecho() + 10;
            y = 150 + random.nextInt(altoPantalla - 300);
        } else {
            x = random.nextBoolean() ? 400 - 110 : 400 + 90;
            y = random.nextBoolean() ? 300 - 110 : 300 + 90;
        }
        cliente = new Cliente(x, y);
    }

    private void generarEnemigos() {
        enemigos.clear();
        int[] carriles = mapa.getCarriles();
        int tipoMapa = mapa.getTipoMapa();

        for (int i = 0; i < 4; i++) {
            int carrilIndex = random.nextInt(carriles.length);
            int carrilCoord = carriles[carrilIndex];
            int vel = 3 + random.nextInt(3);
            int dir, spawnX, spawnY;

            if (tipoMapa == 0) {
                if (carrilIndex < carriles.length / 2) { dir = 2; spawnX = -100; }
                else { dir = 3; spawnX = anchoPantalla + 100; }
                spawnY = carrilCoord - 15;
            } else {
                if (carrilIndex < carriles.length / 2) { dir = 0; spawnY = -100; }
                else { dir = 1; spawnY = altoPantalla + 100; }
                spawnX = carrilCoord - 15;
            }
            enemigos.add(new CarroEnemigo(spawnX, spawnY, vel, dir, carrilIndex, carriles));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        mapa.dibujar(g, getWidth(), getHeight());
        if (cliente != null && !cliente.fueRecogido()) cliente.dibujar(g);

        // Ahora senales no es null, no habrá error
        for (SenalTransito s : senales) s.dibujar(g);
        for (CarroEnemigo e : enemigos) e.dibujar(g);

        taxi.dibujar(g);
        dibujarInterfaz(g);

        if (estadoActual == Estado.PAUSADO) dibujarMenuPausa(g2d);
        if (estadoActual == Estado.GAMEOVER) dibujarMenuChoque(g2d);
    }

    private void dibujarInterfaz(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(10, 10, 220, 90);
        g.setColor(AMARILLO_TAXI);
        g.drawRect(10, 10, 220, 90);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("CONDUCTOR: " + nombreJugador, 20, 30);
        g.setColor(Color.WHITE);
        g.drawString("PUNTOS: " + taxi.getPuntos(), 20, 55);
        g.drawString("VELOCIDAD: " + taxi.getVelocidad() + " km/h", 20, 80);
    }

    private void dibujarMenuChoque(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(AMARILLO_TAXI);
        g2d.setFont(new Font("Impact", Font.PLAIN, 50));
        g2d.drawString("¡SERVICIO TERMINADO!", getWidth()/2 - 230, getHeight()/2 - 100);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Puntos totales: " + taxi.getPuntos(), getWidth()/2 - 80, getHeight()/2 - 50);

        dibujarBoton(g2d, "REINTENTAR", getHeight()/2 + 20, Color.DARK_GRAY);
        dibujarBoton(g2d, "SALIR A LA CENTRAL", getHeight()/2 + 90, new Color(150, 0, 0));
    }

    private void dibujarMenuPausa(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(AMARILLO_TAXI);
        g2d.setFont(new Font("Impact", Font.PLAIN, 50));
        g2d.drawString("TURNO PAUSADO", getWidth()/2 - 160, getHeight()/2 - 80);
        dibujarBoton(g2d, "CONTINUAR", getHeight()/2, Color.DARK_GRAY);
    }

    private void dibujarBoton(Graphics2D g2d, String texto, int y, Color color) {
        int anchoB = 280;
        int altoB = 50;
        int x = getWidth()/2 - anchoB/2;
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, anchoB, altoB, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(x, y, anchoB, altoB, 15, 15);
        g2d.drawString(texto, x + 40, y + 32);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (estadoActual == Estado.JUGANDO) {
            actualizarLogica();
        }
        repaint();
    }

    private void actualizarLogica() {
        if (adelante) taxi.moverAdelante();
        if (atras) taxi.moverAtras();
        if (izquierda) taxi.girarIzquierda();
        if (derecha) taxi.girarDerecha();

        for (CarroEnemigo enemigo : enemigos) {
            enemigo.mover(mapa, getWidth(), getHeight());
            if (taxi.getBounds().intersects(enemigo.getBounds())) {
                estadoActual = Estado.GAMEOVER;
                // Guardamos los puntos en el Menu al morir
                Menu.guardarPuntuacion(nombreJugador, taxi.getPuntos());
            }
            for (SenalTransito s : senales){
                s.actualizar();
                s.aplicarReglas(taxi);
            }
            if (cliente != null && !cliente.fueRecogido()){
                if (taxi.getBounds().intersects(cliente.getBounds())){
                    cliente.recoger();
                    taxi.recogerCliente();
                    generarClienteEnAnden();
                }
            }
        }
        // ... (resto de colisiones con señales y clientes igual)
        manejarCambioEscenario();
    }

    private void manejarCambioEscenario() {
        int totalAncho = getWidth();
        int totalAlto = getHeight();
        boolean cambio = false;
        int lado = -1;

        if (taxi.getX() > totalAncho) { taxi.setX(0); cambio = true; lado = 2; }
        else if (taxi.getX() < 0) { taxi.setX(totalAncho); cambio = true; lado = 3; }
        else if (taxi.getY() > totalAlto) { taxi.setY(0); cambio = true; lado = 1; }
        else if (taxi.getY() < 0) { taxi.setY(totalAlto); cambio = true; lado = 0; }

        if (cambio) {
            mapa.generarMapa(lado);
            generarMundo();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (estadoActual == Estado.GAMEOVER || estadoActual == Estado.PAUSADO) {
            int my = e.getY();
            // Lógica simple para detectar si hizo clic en el botón de reintentar o salir
            if (my > getHeight()/2 + 20 && my < getHeight()/2 + 70) {
                reiniciarJuego();
            } else if (my > getHeight()/2 + 90 && my < getHeight()/2 + 140) {
                // Volver al menú
                Window win = SwingUtilities.getWindowAncestor(this);
                win.dispose();
                new Menu().setVisible(true);
            }
        }
    }

    // --- Métodos de teclado ---
    @Override
    public void keyPressed(KeyEvent e) {
        int t = e.getKeyCode();
        if (t == KeyEvent.VK_W || t == KeyEvent.VK_UP) adelante = true;
        if (t == KeyEvent.VK_S || t == KeyEvent.VK_DOWN) atras = true;
        if (t == KeyEvent.VK_A || t == KeyEvent.VK_LEFT) izquierda = true;
        if (t == KeyEvent.VK_D || t == KeyEvent.VK_RIGHT) derecha = true;
        if (t == KeyEvent.VK_P) estadoActual = (estadoActual == Estado.JUGANDO) ? Estado.PAUSADO : Estado.JUGANDO;
    }
    @Override public void keyReleased(KeyEvent e) {
        int t = e.getKeyCode();
        if (t == KeyEvent.VK_W || t == KeyEvent.VK_UP) adelante = false;
        if (t == KeyEvent.VK_S || t == KeyEvent.VK_DOWN) atras = false;
        if (t == KeyEvent.VK_A || t == KeyEvent.VK_LEFT) izquierda = false;
        if (t == KeyEvent.VK_D || t == KeyEvent.VK_RIGHT) derecha = false;
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}