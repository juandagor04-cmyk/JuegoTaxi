import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class SenalTransito {
    // Tipos Generales
    public static final int REGLAMENTARIA = 0;
    public static final int PREVENTIVA = 1;
    public static final int INFORMATIVA = 2;

    // Subtipos
    public static final int SEMAFORO = 0;
    public static final int PARE = 1;
    public static final int CURVA = 2;
    public static final int CRUCE = 3;
    public static final int CLIENTE = 4;
    public static final int VELOCIDAD = 5;

    // Estado del semaforo
    public static final int ROJO = 0;
    public static final int AMARILLO = 1;
    public static final int VERDE = 2;

    private int x, y;
    private int tipo;
    private int subTipo;
    private int valor;

    // Variables del semaforo
    private int estadoSemaforo = ROJO;
    private int tiempoCambio = 0;
    private Random random;

    private int duracionRojo;
    private int duracionAmarillo;
    private int duracionVerde;

    // Control de multas
    private boolean activa = true;

    public SenalTransito(int x, int y, int tipo, int subTipo, int valor) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.subTipo = subTipo;
        this.valor = valor;
        this.random = new Random();
        iniciarTiemposAleatorios();
    }

    public SenalTransito(int x, int y, int tipo, int subTipo) {
        this(x, y, tipo, subTipo, 0);
    }

    private void iniciarTiemposAleatorios() {
        duracionRojo = 100 + random.nextInt(200);
        duracionAmarillo = 40 + random.nextInt(30);
        duracionVerde = 100 + random.nextInt(200);
    }
    //====DIBUJADO=)====<<<
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (tipo == REGLAMENTARIA) {
            if (subTipo == SEMAFORO) {
                dibujarSemaforoPro(g2d);
            } else if (subTipo == PARE) {
                dibujarParePro(g2d);
            } else if (subTipo == VELOCIDAD) {
                dibujarVelocidadPro(g2d);
            }
        } else if (tipo == PREVENTIVA) {
            dibujarPreventivaPro(g2d);
        } else if (tipo == INFORMATIVA) {
            dibujarInformativaPro(g2d);
        }
        g2d.setStroke(new BasicStroke(1));
    }

    // --- DISEÑOS INDIVIDUALES ---
    private void dibujarSemaforoPro(Graphics2D g2d) {
        g2d.setPaint(new GradientPaint(x + 8, y, Color.GRAY, x + 12, y, Color.DARK_GRAY));
        g2d.fillRect(x + 8, y + 45, 4, 35);
        g2d.setColor(new Color(30, 30, 30));
        g2d.fillRoundRect(x, y, 20, 45, 10, 10);
        int diametro = 12; int posX = x + 4;
        g2d.setColor(estadoSemaforo == ROJO ? Color.RED : new Color(60, 0, 0));
        if(estadoSemaforo == ROJO) g2d.setPaint(new RadialGradientPaint(new Point2D.Float(posX+6, y+7), 8, new float[]{0f, 1f}, new Color[]{Color.WHITE, Color.RED}));
        g2d.fillOval(posX, y + 1, diametro, diametro);
        g2d.setColor(estadoSemaforo == AMARILLO ? Color.YELLOW : new Color(60, 60, 0));
        if(estadoSemaforo == AMARILLO) g2d.setPaint(new RadialGradientPaint(new Point2D.Float(posX+6, y+22), 8, new float[]{0f, 1f}, new Color[]{Color.WHITE, Color.YELLOW}));
        g2d.fillOval(posX, y + 16, diametro, diametro);
        g2d.setColor(estadoSemaforo == VERDE ? Color.GREEN : new Color(0, 60, 0));
        if(estadoSemaforo == VERDE) g2d.setPaint(new RadialGradientPaint(new Point2D.Float(posX+6, y+37), 8, new float[]{0f, 1f}, new Color[]{Color.WHITE, Color.GREEN}));
        g2d.fillOval(posX, y + 31, diametro, diametro);
    }

    private void dibujarParePro(Graphics2D g2d) {
        g2d.setPaint(new GradientPaint(x + 13, y, new Color(180,180,180), x + 17, y, new Color(100,100,100)));
        g2d.fillRect(x + 13, y + 25, 4, 45);
        Polygon pare = new Polygon();
        pare.addPoint(x + 10, y + 7); pare.addPoint(x + 20, y + 7); pare.addPoint(x + 27, y + 14); pare.addPoint(x + 27, y + 23); pare.addPoint(x + 20, y + 30); pare.addPoint(x + 10, y + 30); pare.addPoint(x + 3, y + 23); pare.addPoint(x + 3, y + 14);
        g2d.setPaint(new GradientPaint(x+3, y+7, new Color(255, 50, 50), x+27, y+30, new Color(150, 0, 0)));
        g2d.fillPolygon(pare); g2d.setColor(Color.WHITE); g2d.setStroke(new BasicStroke(2f)); g2d.drawPolygon(pare);
        g2d.setFont(new Font("Arial", Font.BOLD, 8)); g2d.drawString("PARE", x + 6, y + 22);
    }

    private void dibujarVelocidadPro(Graphics2D g2d) {
        g2d.setColor(Color.GRAY); g2d.fillRect(x + 13, y + 25, 3, 40);
        g2d.setColor(Color.WHITE); g2d.fillOval(x + 2, y + 2, 26, 26);
        g2d.setColor(Color.RED); g2d.setStroke(new BasicStroke(3f)); g2d.drawOval(x + 2, y + 2, 26, 26);
        g2d.setColor(Color.BLACK); g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String txt = String.valueOf(valor); FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(txt, x + 15 - (fm.stringWidth(txt)/2), y + 19);
    }

    private void dibujarPreventivaPro(Graphics2D g2d) {
        int[] dx = {x + 15, x + 30, x + 15, x}; int[] dy = {y, y + 15, y + 30, y + 15};
        Polygon diamante = new Polygon(dx, dy, 4);
        g2d.setPaint(new GradientPaint(x, y, Color.YELLOW, x + 30, y + 30, new Color(255, 200, 0)));
        g2d.fillPolygon(diamante); g2d.setColor(Color.BLACK); g2d.setStroke(new BasicStroke(2f)); g2d.drawPolygon(diamante);
        if (subTipo == CURVA) g2d.drawArc(x + 8, y + 10, 15, 15, 0, 150);
        else if (subTipo == CRUCE) { g2d.drawLine(x + 15, y + 8, x + 15, y + 22); g2d.drawLine(x + 8, y + 15, x + 22, y + 15); }
    }

    private void dibujarInformativaPro(Graphics2D g2d) {
        g2d.setColor(new Color(0, 70, 180)); g2d.fillRoundRect(x, y, 28, 28, 5, 5);
        g2d.setColor(Color.WHITE); g2d.setStroke(new BasicStroke(1.5f)); g2d.drawRoundRect(x + 2, y + 2, 24, 24, 3, 3);
        if(subTipo == CLIENTE) { g2d.fillOval(x + 11, y + 7, 6, 6); g2d.fillRect(x + 9, y + 14, 10, 8); }
    }

    // --- LÓGICA DE ACTUALIZACIÓN ---
    public void actualizar() {
        tiempoCambio++;
        if (subTipo == SEMAFORO) {
            if (estadoSemaforo == ROJO && tiempoCambio >= duracionRojo) {
                estadoSemaforo = VERDE; tiempoCambio = 0;
            } else if (estadoSemaforo == VERDE && tiempoCambio >= duracionVerde) {
                estadoSemaforo = AMARILLO; tiempoCambio = 0;
            } else if (estadoSemaforo == AMARILLO && tiempoCambio >= duracionAmarillo) {
                estadoSemaforo = ROJO;
                tiempoCambio = 0;
                this.activa = true;
            }
        }
    }

    public void aplicarReglas(Taxi taxi) {

        if (!taxi.getBounds().intersects(new Rectangle(x - 10, y - 10, 50, 50))) return;

        if (subTipo == SEMAFORO && estadoSemaforo == ROJO && activa) {
            if (taxi.getVelocidad() > 5) { // Si va a más de 5 px/f se considera que se lo pasó
                taxi.setMulta(true);
                taxi.restarPuntos(100);
                this.activa = false;
                System.out.println("¡Multa! Semáforo en rojo.");
            }
        } else if (subTipo == VELOCIDAD) {
            if (taxi.getVelocidad() > valor) {
                taxi.setMulta(true);
            }
        } else if (subTipo == CLIENTE) {
            taxi.recogerCliente();
        }
    }

    // --- GETTERS Y SETTERS  ---


    public int getEstado() { return estadoSemaforo; }

    public int getEstadoSemaforo() { return estadoSemaforo; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getTipo() { return tipo; }
    public int getSubTipo() { return subTipo; }
}