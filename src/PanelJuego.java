import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class PanelJuego extends JPanel implements ActionListener, KeyListener {

    private Taxi taxi;
    private Mapa mapa;
    private Cliente cliente;

    private ArrayList<SenalTransito> senales;
    private ArrayList<CarroEnemigo> enemigos;

    private Timer timer;
    private Random random;

    private boolean izquierda = false;
    private boolean derecha = false;
    private boolean adelante = false;
    private boolean atras = false;

    private int anchoPantalla = 800;
    private int altoPantalla = 600;

    public PanelJuego() {
        setFocusable(true);
        addKeyListener(this);
        setBackground(new Color(30,30,35));
        random = new Random();

        taxi = new Taxi(anchoPantalla / 2, altoPantalla / 2, 5);
        mapa = new Mapa();

        generarMundo();

        // El Timer corre cada 40ms (~25 frames por segundo)
        timer = new Timer(40, this);
        timer.start();
    }

    private void generarMundo() {
        generarClienteEnAnden();
        senales = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int tipo = random.nextInt(3);
            int subtipo = random.nextInt(5);
            int x = 50 + random.nextInt(anchoPantalla - 100);
            int y = 50 + random.nextInt(altoPantalla / 2 + 100);
            senales.add(new SenalTransito(x, y, tipo, subtipo));
        }
        enemigos = new ArrayList<>();
        generarEnemigos();
    }

    private void generarClienteEnAnden() {
        int[] carriles = mapa.getCarriles();
        int tipoMapa = mapa.getTipoMapa();
        int x, y;

        switch (tipoMapa) {
            case 0:
                int andenY = random.nextBoolean() ? mapa.getCalleLimiteSuperior() - 40 : mapa.getCalleLimiteInferior() + 10;
                int carrilX = carriles[random.nextInt(carriles.length)];
                x = carrilX - 12;
                y = andenY;
                break;
            case 1:
                int andenX = random.nextBoolean() ? mapa.getCalleLimiteIzquierdo() - 40 : mapa.getCalleLimiteDerecho() + 10;
                int carrilY = carriles[random.nextInt(carriles.length)];
                x = andenX;
                y = carrilY - 15;
                break;
            default:
                x = 100 + random.nextInt(anchoPantalla - 200);
                y = 100 + random.nextInt(altoPantalla / 2 + 100);
                break;
        }
        x = Math.max(20, Math.min(x, anchoPantalla - 40));
        y = Math.max(20, Math.min(y, altoPantalla / 2 + 160));
        cliente = new Cliente(x, y);
    }

    private void generarEnemigos() {
        enemigos.clear();
        int[] carriles = mapa.getCarriles();
        for (int i = 0; i < 4; i++) {
            int carril = carriles[random.nextInt(carriles.length)];
            int velocidad = 2 + random.nextInt(3);
            enemigos.add(new CarroEnemigo(carril - 20, -50 - random.nextInt(200), velocidad, 0, 0, carriles));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        mapa.dibujar(g, getWidth(), getHeight());
        if (cliente != null && !cliente.fueRecogido()) cliente.dibujar(g);
        for (SenalTransito s : senales) s.dibujar(g);
        for (CarroEnemigo e : enemigos) e.dibujar(g);
        taxi.dibujar(g);
        dibujarInterfaz(g);
    }

    private void dibujarInterfaz(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(10, 10, 200, 80);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("PUNTOS: " + taxi.getPuntos(), 20, 35);
        g.drawString("VELOCIDAD: " + taxi.getVelocidad(), 20, 55);
        g.drawString("MULTAS: " + (taxi.getPuntos() < 100 ? "⚠️" : "✓"), 20, 75);
    }

    // ==========================================
    // ESTA ES LA PARTE QUE CORREGIMOS
    // ==========================================
    @Override
    public void actionPerformed(ActionEvent e) {

        // 1. RECUPERACIÓN DE VELOCIDAD: Si el taxi está muy lento (por un choque o césped),
        // aumentamos su velocidad gradualmente hasta llegar a la normal (5).
        if (taxi.getVelocidad() < 5) {
            // Usamos un pequeño truco de tiempo para que no acelere de golpe
            if (System.currentTimeMillis() % 3 == 0) {
                taxi.setVelocidad(taxi.getVelocidad() + 1);
            }
        }

        // Aplicar movimiento según teclas
        if (adelante) taxi.moverAdelante();
        if (atras) taxi.moverAtras();
        if (izquierda) taxi.girarIzquierda();
        if (derecha) taxi.girarDerecha();

        // Colisiones con enemigos
        for (CarroEnemigo enemigo : enemigos) {
            enemigo.mover(mapa, getWidth(), getHeight());
            if (taxi.getBounds().intersects(enemigo.getBounds())) {
                taxi.setVelocidad(1); // Frenazo total por choque
                JOptionPane.showMessageDialog(this, " ¡CHOQUE! ");
            }
        }

        // Señales
        for (SenalTransito s : senales) {
            s.actualizar();
            s.aplicarReglas(taxi);
        }

        // Clientes
        if (cliente != null && !cliente.fueRecogido() && taxi.getBounds().intersects(cliente.getBounds())) {
            cliente.recoger();
            taxi.recogerCliente();
            JOptionPane.showMessageDialog(this, " ¡CLIENTE RECOGIDO! ");
            generarClienteEnAnden();
        }

        // Cambio de escenario (bordes de pantalla)
        int totalAncho = getWidth();
        int totalAlto = getHeight();
        boolean cambioEscenario = false;

        if (taxi.getX() > totalAncho) { taxi.setX(-taxi.getAncho()); cambioEscenario = true; }
        else if (taxi.getX() + taxi.getAncho() < 0) { taxi.setX(totalAncho); cambioEscenario = true; }
        else if (taxi.getY() > totalAlto) { taxi.setY(-taxi.getAlto()); cambioEscenario = true; }
        else if (taxi.getY() + taxi.getAlto() < 0) { taxi.setY(totalAlto); cambioEscenario = true; }

        if (cambioEscenario) {
            mapa.generarMapa();
            generarMundo();
        }

        // 2. PENALIZACIÓN POR CÉSPED:
        // En lugar de restar 1 cada frame (que lo deja en 0),
        // limitamos la velocidad máxima a 2 mientras esté fuera de la calle.
        if (!cambioEscenario && !mapa.estaDentroDeCalle(taxi.getX(), taxi.getY())) {
            if (taxi.getVelocidad() > 2) {
                taxi.setVelocidad(2);
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_A) izquierda = true;
        if (tecla == KeyEvent.VK_RIGHT || tecla == KeyEvent.VK_D) derecha = true;
        if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W) adelante = true;
        if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S) atras = true;
        if (tecla == KeyEvent.VK_SPACE) taxi.aumentarVelocidad(3); // Nitro
        if (tecla == KeyEvent.VK_SHIFT) taxi.setVelocidad(1); // Freno de mano
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();
        if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_A) izquierda = false;
        if (tecla == KeyEvent.VK_RIGHT || tecla == KeyEvent.VK_D) derecha = false;
        if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W) adelante = false;
        if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S) atras = false;
    }

    @Override public void keyTyped(KeyEvent e) {}
}