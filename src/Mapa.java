import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Mapa {
    private int tipoMapa; // 0=Horizontal, 1=Vertical, 2=T, 3=+, 4=Glorieta
    private int[] carriles = {150, 250, 350};
    private Random random = new Random();
    private int anchoMapa = 800, altoMapa = 600;

    private int calleLimiteSuperior, calleLimiteInferior, calleLimiteIzquierdo, calleLimiteDerecho;

    // Colores
    private final Color ASFALTO = new Color(55, 55, 60);
    private final Color ASFALTO_RUIDO = new Color(60, 60, 65);
    private final Color LINEA_AMARILLA = new Color(255, 210, 0);
    private final Color LINEA_BLANCA = new Color(240, 240, 240);
    private final Color ANDEN = new Color(160, 160, 165);
    private final Color BORDE_ANDEN = new Color(130, 130, 135);
    private final Color CESPED = new Color(60, 140, 60);
    private final Color CESPED_VARIACION = new Color(70, 150, 70);
    private final Color TIERRA = new Color(120, 90, 60);

    private final Color[] COLORES_TECHOS = {new Color(180, 70, 50), new Color(100, 100, 110), new Color(150, 120, 90), new Color(80, 120, 80)};

    private ArrayList<Point> arbolesPos = new ArrayList<>();
    private ArrayList<Point> arbustosPos = new ArrayList<>();
    private ArrayList<Rectangle> casasPos = new ArrayList<>();
    private ArrayList<Color> casasColores = new ArrayList<>();
    private ArrayList<Rectangle> edificiosPos = new ArrayList<>();
    private ArrayList<SenalTransito> senalesCache = new ArrayList<>();

    public Mapa() {
        generarMapa(-1);
    }

    public void generarMapa(int lado) {
        if (lado == 0 || lado == 1) {
            int[] opciones = {1, 2, 3, 4};
            tipoMapa = opciones[random.nextInt(opciones.length)];
        } else if (lado == 2 || lado == 3) {
            int[] opciones = {0, 2, 3, 4};
            tipoMapa = opciones[random.nextInt(opciones.length)];
        } else {
            tipoMapa = random.nextInt(5);
        }

        arbolesPos.clear();
        arbustosPos.clear();
        casasPos.clear();
        casasColores.clear();
        edificiosPos.clear();
    }

    // =========================================================
    // LÓGICA DE UBICACIÓN SEGURA (CALLE Y ANDÉN)
    // =========================================================


    public Point generarPuntoEnCalle() {
        int x, y;
        int margen = 35; // Distancia desde el eje de la calle al andén
        int centroX = anchoMapa / 2;
        int centroY = altoMapa / 2;

        switch (tipoMapa) {
            case 0: // Horizontal
                x = 150 + random.nextInt(anchoMapa - 300);
                y = random.nextBoolean() ? (calleLimiteSuperior - 15) : (calleLimiteInferior + 15);
                break;
            case 1: // Vertical
                x = random.nextBoolean() ? (calleLimiteIzquierdo - 15) : (calleLimiteDerecho + 15);
                y = 150 + random.nextInt(altoMapa - 300);
                break;
            case 4: // Glorieta (puntos en las esquinas de entrada)
                double ang = random.nextDouble() * Math.PI * 2;
                x = centroX + (int)(Math.cos(ang) * 220);
                y = centroY + (int)(Math.sin(ang) * 220);
                break;
            default: // Cruces T y +
                if (random.nextBoolean()) {
                    x = centroX + (random.nextBoolean() ? 140 : -140);
                    y = 100 + random.nextInt(altoMapa - 200);
                } else {
                    x = 100 + random.nextInt(anchoMapa - 200);
                    y = centroY + (random.nextBoolean() ? 140 : -140);
                }
                break;
        }
        return new Point(x, y);
    }

    public Cliente generarClienteSeguro() {
        Point p = generarPuntoEnCalle();
        return new Cliente(p.x, p.y);
    }

    public Rectangle generarDestinoSeguro() {
        Point p = generarPuntoEnCalle();
        return new Rectangle(p.x - 25, p.y - 25, 50, 50);
    }

    // =========================================================
    // GESTIÓN DE SEÑALES
    // =========================================================
    public ArrayList<SenalTransito> generarSenalesSeguras(int cantidad) {
        ArrayList<SenalTransito> senales = new ArrayList<>();
        int centroX = anchoMapa / 2;
        int centroY = altoMapa / 2;

        switch (tipoMapa) {
            case 0:
                senales.add(new SenalTransito(200, calleLimiteSuperior - 35, SenalTransito.PREVENTIVA, SenalTransito.CURVA));
                senales.add(new SenalTransito(anchoMapa - 200, calleLimiteInferior + 5, SenalTransito.REGLAMENTARIA, SenalTransito.PARE));
                break;
            case 1:
                senales.add(new SenalTransito(calleLimiteIzquierdo - 35, 200, SenalTransito.PREVENTIVA, SenalTransito.CRUCE));
                senales.add(new SenalTransito(calleLimiteDerecho + 5, altoMapa - 200, SenalTransito.REGLAMENTARIA, SenalTransito.PARE));
                break;
            case 3: // Semáforos en la intersección
                senales.add(new SenalTransito(centroX - 140, centroY - 140, SenalTransito.REGLAMENTARIA, SenalTransito.SEMAFORO));
                senales.add(new SenalTransito(centroX + 120, centroY + 120, SenalTransito.REGLAMENTARIA, SenalTransito.SEMAFORO));
                break;
            case 4:
                senales.add(new SenalTransito(centroX + 110, centroY - 190, SenalTransito.REGLAMENTARIA, SenalTransito.PARE));
                senales.add(new SenalTransito(centroX - 140, centroY + 160, SenalTransito.REGLAMENTARIA, SenalTransito.PARE));
                break;
            default:
                senales.add(new SenalTransito(100, 100, SenalTransito.PREVENTIVA, SenalTransito.CURVA));
                break;
        }
        this.senalesCache = senales;
        return senales;
    }

    // =========================================================
    // DIBUJO Y COLISIONES
    // =========================================================

    public boolean chocaConEstructura(Rectangle limitesTaxi) {
        for (Rectangle casa : casasPos) if (casa.intersects(limitesTaxi)) return true;
        for (Rectangle edificio : edificiosPos) if (edificio.intersects(limitesTaxi)) return true;
        for (Point arbol : arbolesPos) {
            Rectangle hitBoxArbol = new Rectangle(arbol.x - 20, arbol.y - 20, 40, 40);
            if (hitBoxArbol.intersects(limitesTaxi)) return true;
        }
        if (tipoMapa == 4) {
            int radioInterior = 70;
            double dx = limitesTaxi.getCenterX() - (anchoMapa / 2);
            double dy = limitesTaxi.getCenterY() - (altoMapa / 2);
            if (Math.sqrt(dx*dx + dy*dy) < radioInterior) return true;
        }
        return false;
    }

    public void dibujar(Graphics g, int ancho, int alto) {
        anchoMapa = ancho; altoMapa = alto;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(CESPED);
        g.fillRect(0, 0, ancho, alto);
        dibujarBaseCesped(g2d, ancho, alto);

        switch (tipoMapa) {
            case 0: dibujarRectaHorizontal(g2d, ancho, alto); break;
            case 1: dibujarRectaVertical(g2d, ancho, alto); break;
            case 2: dibujarInterseccionT(g2d, ancho, alto); break;
            case 3: dibujarInterseccionMas(g2d, ancho, alto); break;
            case 4: dibujarGlorietaRediseñada(g2d, ancho, alto); break;
        }

        dibujarDecoraciones(g2d);
        for (SenalTransito s : senalesCache) s.dibujar(g);
    }

    private void dibujarBaseCesped(Graphics2D g, int ancho, int alto) {
        Random pajarito = new Random(42);
        g.setColor(CESPED_VARIACION);
        for (int i = 0; i < 60; i++) {
            g.fillOval(pajarito.nextInt(ancho), pajarito.nextInt(alto), pajarito.nextInt(20)+10, pajarito.nextInt(10)+5);
        }
    }

    private void dibujarTexturaAsfalto(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(ASFALTO);
        g.fillRect(x, y, width, height);
        g.setColor(ASFALTO_RUIDO);
        Random noise = new Random(1337);
        for (int i = 0; i < (width * height) / 100; i++) {
            g.fillRect(noise.nextInt(width) + x, noise.nextInt(height) + y, 1, 1);
        }
    }

    private void dibujarRectaHorizontal(Graphics2D g, int ancho, int alto) {
        int centroY = alto / 2;
        int anchoCalle = 240;
        int inicioCalle = centroY - anchoCalle / 2;
        calleLimiteSuperior = inicioCalle;
        calleLimiteInferior = inicioCalle + anchoCalle;
        calleLimiteIzquierdo = 0;
        calleLimiteDerecho = ancho;

        dibujarTexturaAsfalto(g, 0, inicioCalle, ancho, anchoCalle);
        g.setColor(LINEA_BLANCA);
        g.setStroke(new BasicStroke(3));
        g.drawLine(0, inicioCalle + 2, ancho, inicioCalle + 2);
        g.drawLine(0, inicioCalle + anchoCalle - 2, ancho, inicioCalle + anchoCalle - 2);

        dibujarAnden(g, 0, inicioCalle - 30, ancho, 30);
        dibujarAnden(g, 0, inicioCalle + anchoCalle, ancho, 30);

        if (arbolesPos.isEmpty()) {
            generarLineaDecoracion(0, 0, ancho, inicioCalle - 40, true);
            generarLineaDecoracion(0, inicioCalle + anchoCalle + 40, ancho, alto - (inicioCalle + anchoCalle + 40), false);
        }
    }

    private void dibujarRectaVertical(Graphics2D g, int ancho, int alto) {
        int centroX = ancho / 2;
        int anchoCalle = 240;
        int inicioCalle = centroX - anchoCalle / 2;
        calleLimiteIzquierdo = inicioCalle;
        calleLimiteDerecho = inicioCalle + anchoCalle;
        calleLimiteSuperior = 0;
        calleLimiteInferior = alto;

        dibujarTexturaAsfalto(g, inicioCalle, 0, anchoCalle, alto);
        dibujarAnden(g, inicioCalle - 30, 0, 30, alto);
        dibujarAnden(g, inicioCalle + anchoCalle, 0, 30, alto);

        if (edificiosPos.isEmpty()) {
            generarBloqueDecoracion(0, 0, inicioCalle - 40, alto, true);
            generarBloqueDecoracion(inicioCalle + anchoCalle + 40, 0, ancho - (inicioCalle + anchoCalle + 40), alto, false);
        }
    }

    private void dibujarInterseccionT(Graphics2D g2d, int ancho, int alto) {
        int centroX = ancho / 2; int centroY = alto / 2; int anchoCalle = 200;
        int gap = anchoCalle/2;
        calleLimiteSuperior = 0; calleLimiteInferior = alto; calleLimiteIzquierdo = 0; calleLimiteDerecho = ancho;

        dibujarTexturaAsfalto(g2d, 0, centroY - gap, ancho, anchoCalle);
        dibujarTexturaAsfalto(g2d, centroX - gap, 0, anchoCalle, centroY + gap);

        dibujarAnden(g2d, 0, centroY + gap, ancho, 25);
        if (arbolesPos.isEmpty()) generarParque(20, 20, 200, 200);
    }

    private void dibujarInterseccionMas(Graphics2D g2d, int ancho, int alto) {
        int centroX = ancho / 2; int centroY = alto / 2; int anchoCalle = 220;
        int gap = anchoCalle / 2;
        calleLimiteSuperior = 0; calleLimiteInferior = alto; calleLimiteIzquierdo = 0; calleLimiteDerecho = ancho;

        dibujarTexturaAsfalto(g2d, 0, centroY - gap, ancho, anchoCalle);
        dibujarTexturaAsfalto(g2d, centroX - gap, 0, anchoCalle, alto);

        if (casasPos.isEmpty()) {
            casasPos.add(new Rectangle(50, 50, 100, 100));
            casasColores.add(COLORES_TECHOS[0]);
        }
    }

    private void dibujarGlorietaRediseñada(Graphics2D g, int ancho, int alto) {
        int centroX = ancho / 2; int centroY = alto / 2;
        int radioExt = 160; int radioInt = 70; int anchoC = 180;
        calleLimiteSuperior = 0; calleLimiteInferior = alto; calleLimiteIzquierdo = 0; calleLimiteDerecho = ancho;

        dibujarTexturaAsfalto(g, centroX - anchoC/2, 0, anchoC, alto);
        dibujarTexturaAsfalto(g, 0, centroY - anchoC/2, ancho, anchoC);

        g.setColor(ASFALTO);
        g.fillOval(centroX - radioExt, centroY - radioExt, radioExt*2, radioExt*2);
        g.setColor(CESPED);
        g.fillOval(centroX - radioInt, centroY - radioInt, radioInt*2, radioInt*2);

        if(arbolesPos.isEmpty()) arbolesPos.add(new Point(centroX, centroY));
    }

    private void dibujarAnden(Graphics g, int x, int y, int ancho, int alto) {
        g.setColor(ANDEN); g.fillRect(x, y, ancho, alto);
        g.setColor(BORDE_ANDEN); g.drawRect(x, y, ancho, alto);
    }

    private void dibujarDecoraciones(Graphics2D g) {
        for (int i = 0; i < casasPos.size(); i++) dibujarCasa(g, casasPos.get(i), casasColores.get(i));
        for (Rectangle rect : edificiosPos) dibujarEdificio(g, rect);
        for (Point p : arbolesPos) dibujarArbol(g, p.x, p.y, 25);
    }

    private void dibujarArbol(Graphics2D g, int x, int y, int radio) {
        g.setColor(new Color(100, 70, 40)); g.fillOval(x - 5, y - 5, 10, 10);
        g.setColor(new Color(30, 100, 30)); g.fillOval(x - radio, y - radio, radio * 2, radio * 2);
    }

    private void dibujarCasa(Graphics2D g, Rectangle r, Color colorTecho) {
        g.setColor(new Color(220, 220, 200)); g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(colorTecho); g.fillRect(r.x + 5, r.y + 5, r.width - 10, r.height - 10);
    }

    private void dibujarEdificio(Graphics2D g, Rectangle r) {
        g.setColor(new Color(100, 100, 110)); g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.WHITE);
        for(int i=10; i<r.height-20; i+=30) g.fillRect(r.x + 10, r.y + i, 15, 15);
    }

    private void generarLineaDecoracion(int x, int y, int ancho, int alto, boolean norte) {
        for (int ax = x + 20; ax < x + ancho - 60; ax += 80) {
            casasPos.add(new Rectangle(ax, y + (norte ? alto - 50 : 10), 50, 50));
            casasColores.add(COLORES_TECHOS[random.nextInt(COLORES_TECHOS.length)]);
        }
    }

    private void generarBloqueDecoracion(int x, int y, int ancho, int alto, boolean oeste) {
        for (int ay = 20; ay < alto - 100; ay += 130) {
            edificiosPos.add(new Rectangle(x + 5, ay, ancho - 10, 100));
        }
    }

    private void generarParque(int x, int y, int ancho, int alto){
        for(int i=0; i<3; i++) arbolesPos.add(new Point(x + random.nextInt(ancho), y + random.nextInt(alto)));
    }

    public ArrayList<CarroEnemigo> generarEnemigosSeguros(int cantidad) {
        ArrayList<CarroEnemigo> enemigos = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            int vel = 4 + random.nextInt(3);
            int dir = 0, spawnX = 0, spawnY = 0;
            int carrilIdx = random.nextInt(carriles.length);
            if (tipoMapa == 0 || tipoMapa == 2) {
                dir = random.nextBoolean() ? 2 : 3;
                spawnX = (dir == 2) ? -100 : anchoMapa + 100;
                spawnY = calleLimiteSuperior + 40 + (carrilIdx * 50);
            } else {
                dir = random.nextBoolean() ? 0 : 1;
                spawnY = (dir == 0) ? -100 : altoMapa + 100;
                spawnX = calleLimiteIzquierdo + 40 + (carrilIdx * 50);
            }
            enemigos.add(new CarroEnemigo(spawnX, spawnY, vel, dir, carrilIdx, carriles));
        }
        return enemigos;
    }

    public int getTipoMapa() { return tipoMapa; }
    public void actualizar() { for (SenalTransito s : senalesCache) s.actualizar(); }
}