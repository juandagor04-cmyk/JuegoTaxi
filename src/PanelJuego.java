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


    public PanelJuego() {
        setFocusable(true);
        addKeyListener(this);
        setBackground(new Color(30,30,35));
        random = new Random();


        taxi = new Taxi(400, 300, 5);
        mapa = new Mapa();

        //Posicion del Cliente
        generarClienteEnAnden();

        senales = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int tipo = random.nextInt(3);
            int subtipo = random.nextInt(5);
            int x = 50 + random.nextInt(700);
            int y = 50 + random.nextInt(500);
            senales.add(new SenalTransito(x, y, tipo, subtipo));




        }

        enemigos = new ArrayList<>();
        generarEnemigos();



        timer = new Timer(40, this);
        timer.start();
    }
    //Generar Cliente
    private void generarClienteEnAnden(){
        int [] carriles = mapa.getCarriles();
        int tipoMapa = mapa.getTipoMapa();
        int x, y;

        switch (tipoMapa){
            case 0:
                int andenY = random.nextBoolean() ? mapa.getCalleLimiteSuperior() - 40 : mapa.getCalleLimiteInferior() + 10;
                int carril = carriles [random.nextInt(carriles.length)];
                x = carril - 12;
                y = andenY;
                break;

            case 1: //Recta vertical
                int andenX = random.nextBoolean() ? mapa.getCalleLimiteIzquierdo() - 40 : mapa.getCalleLimiteDerecho() + 10;
                carril = carriles [random.nextInt(carriles.length)];
                x = andenX;
                y = carril - 15;
                break;

            default:
                x = 100 + random.nextInt(600);
                y = 100 + random.nextInt(400);
                break;
        }
        x = Math.max(20, Math.min(x, 760));
        y = Math.max(20, Math.min(y, 560));

        cliente = new Cliente (x, y);
    }
    // Generar enemigos
    private  void generarEnemigos(){
        enemigos.clear();
        int[] carriles = mapa.getCarriles();

        for (int  i = 0; i < 4; i++){
            int carril = carriles[random.nextInt(carriles.length)];
            int velocidad = 2 + random.nextInt(3);
            //Usarel constructor que recibe direccion
            int direccion = 0; //Hacia abajo
            enemigos.add(new CarroEnemigo(carril - 20, -50 - random.nextInt(200), velocidad, direccion, 0, carriles));
        }
    }
    @Override
    protected void paintComponent (Graphics g){
        super.paintComponent(g);

        mapa.dibujar(g, getWidth(), getHeight());
        if (cliente != null && ! cliente.fueRecogido()){
            cliente.dibujar(g);
        }
        for (SenalTransito s : senales){
            s.dibujar(g);
        }
        for (CarroEnemigo e : enemigos) {
            e.dibujar(g);
        }
        taxi.dibujar(g);

        // Dibujar Interfaz
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


    @Override
    public void actionPerformed(ActionEvent e) {
        // Movimiento continuo con teclas presionadas
        if (adelante) taxi.moverAdelante();
        if (atras) taxi.moverAtras();
        if (izquierda) taxi.girarIzquierda();
        if (derecha) taxi.girarDerecha();

        // Actualizar enemigos
        int[] carriles = mapa.getCarriles();
        for (int i = 0; i < enemigos.size(); i++) {
            CarroEnemigo enemigo = enemigos.get(i);
            enemigo.mover(mapa, getWidth(), getHeight());

            if (taxi.getBounds().intersects(enemigo.getBounds())) {
                taxi.setVelocidad(Math.max(1, taxi.getVelocidad() - 2));
                JOptionPane.showMessageDialog(this, " ¡CHOQUE! \nVelocidad reducida");
            }
        }

        // Actualizar señales
        for (SenalTransito s : senales) {
            s.actualizar();
            s.aplicarReglas(taxi);
        }

        // Recoger cliente
        if (cliente != null && !cliente.fueRecogido() && taxi.getBounds().intersects(cliente.getBounds())) {
            cliente.recoger();
            taxi.setPuntos(50);
            JOptionPane.showMessageDialog(this, " ¡CLIENTE RECOGIDO! +50 puntos ");
            generarClienteEnAnden(); // Nuevo cliente
        }

        // Limitar movimiento del taxi
        taxi.limitarMovimiento(getWidth(), getHeight());

        // Verificar de salir a la calle
        if (!mapa.estaDentroDeCalle(taxi.getX(), taxi.getY())){
            mapa.generarMapa();
            taxi.setX(400);
            taxi.setY(300);
            generarClienteEnAnden();
            generarEnemigos();
            taxi.setPuntos(25);
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();

        switch (tecla) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                izquierda = true;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                derecha = true;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                adelante = true;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                atras = true;
                break;
            case KeyEvent.VK_SPACE:
                taxi.aumentarVelocidad(2);
                break;
            case KeyEvent.VK_SHIFT:
                taxi.reducirVelocidad();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();

        switch (tecla) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                izquierda = false;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                derecha = false;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                adelante = false;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                atras = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}




