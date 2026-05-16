import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Mapa {
    private int tipoMapa; // 0=Horizontal, 1=Vertical, 2=T, 3=+, 4=Glorieta
    private int[] carriles = {150, 250, 350};
    private Random random = new Random();
    private int anchoMapa = 800, altoMapa = 600; // Valores por defecto

    // Bordes de la calle
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

    // === ERROR CORREGIDO AQUÍ: Faltaba el return false ===
    public boolean chocaConEstructura(Rectangle limitesTaxi) {
        for (Rectangle casa : casasPos) {
            if (casa.intersects(limitesTaxi)) return true;
        }
        for (Rectangle edificio : edificiosPos) {
            if (edificio.intersects(limitesTaxi)) return true;
        }
        for (Point arbol : arbolesPos) {
            Rectangle hitBoxArbol = new Rectangle(arbol.x - 25, arbol.y - 25, 50, 50);
            if (hitBoxArbol.intersects(limitesTaxi)) return true;
        }
        return false; // <-- Esto era lo que te faltaba
    }

    public void generarMapa() {
        generarMapa(-1);
    }

    public Cliente generarClienteSeguro() {
        int x = 0, y = 0;
        int margenAnden = 35;

        if (tipoMapa == 0) {
            x = 150 + random.nextInt(anchoMapa - 300);
            y = random.nextBoolean() ? calleLimiteSuperior - margenAnden : calleLimiteInferior + 10;
        } else if (tipoMapa == 1) {
            x = random.nextBoolean() ? calleLimiteIzquierdo - margenAnden : calleLimiteDerecho + 10;
            y = 150 + random.nextInt(altoMapa - 300);
        } else {
            x = random.nextBoolean() ? 100 : anchoMapa - 100;
            y = random.nextBoolean() ? 100 : altoMapa - 100;
        }
        return new Cliente(x, y);
    }

    public ArrayList<CarroEnemigo> generarEnemigosSeguros(int cantidad) {
        ArrayList<CarroEnemigo> enemigos = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            int vel = 3 + random.nextInt(4);
            int dir = 0, spawnX = 0, spawnY = 0;
            int carrilIndex = random.nextInt(carriles.length);

            if (tipoMapa == 0 || tipoMapa == 2) {
                dir = random.nextBoolean() ? 2 : 3;
                spawnX = (dir == 2) ? -100 : anchoMapa + 100;
                spawnY = calleLimiteSuperior + 30 + (carrilIndex * 60);
            } else {
                dir = random.nextBoolean() ? 0 : 1;
                spawnY = (dir == 0) ? -100 : altoMapa + 100;
                spawnX = calleLimiteIzquierdo + 30 + (carrilIndex * 60);
            }
            enemigos.add(new CarroEnemigo(spawnX, spawnY, vel, dir, carrilIndex, carriles));
        }
        return enemigos;
    }

    public ArrayList<SenalTransito> generarSenalesSeguras(int cantidad) {
        ArrayList<SenalTransito> senales = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            int tipo = random.nextInt(3);
            int valor = random.nextInt(5);
            int x = 0, y = 0;

            if (tipoMapa == 0) {
                x = 100 + random.nextInt(anchoMapa - 200);
                y = random.nextBoolean() ? calleLimiteSuperior - 25 : calleLimiteInferior + 5;
            } else if (tipoMapa == 1) {
                x = random.nextBoolean() ? calleLimiteIzquierdo - 25 : calleLimiteDerecho + 5;
                y = 100 + random.nextInt(altoMapa - 200);
            } else {
                x = random.nextBoolean() ? 250 : anchoMapa - 250;
                y = random.nextBoolean() ? 200 : altoMapa - 200;
            }
            senales.add(new SenalTransito(x, y, tipo, valor));
        }
        return senales;
    }

    public void dibujar(Graphics g, int ancho, int alto) {
        anchoMapa = ancho; altoMapa = alto;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(TIERRA);
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
    }

    private void dibujarBaseCesped(Graphics2D g, int ancho, int alto) {
        g.setColor(CESPED);
        g.fillRect(0, 0, ancho, alto);
        Random pajarito = new Random(42);
        g.setColor(CESPED_VARIACION);
        for (int i = 0; i < 100; i++) {
            int w = pajarito.nextInt(30) + 10;
            int h = pajarito.nextInt(20) + 5;
            g.fillOval(pajarito.nextInt(ancho), pajarito.nextInt(alto), w, h);
        }
    }

    private void dibujarTexturaAsfalto(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(ASFALTO);
        g.fillRect(x, y, width, height);
        Random noise = new Random(1337);
        g.setColor(ASFALTO_RUIDO);
        for (int i = 0; i < (width * height) / 50; i++) {
            int px = noise.nextInt(width) + x;
            int py = noise.nextInt(height) + y;
            g.fillRect(px, py, 1, 1);
        }
    }

    private void dibujarRectaHorizontal(Graphics2D g, int ancho, int alto) {
        int centroY = alto / 2;
        int anchoCalle = 240;
        int inicioCalle = centroY - anchoCalle / 2;
        dibujarTexturaAsfalto(g, 0, inicioCalle, ancho, anchoCalle);
        g.setColor(LINEA_BLANCA);
        g.setStroke(new BasicStroke(3));
        g.drawLine(0, inicioCalle + 2, ancho, inicioCalle + 2);
        g.drawLine(0, inicioCalle + anchoCalle - 2, ancho, inicioCalle + anchoCalle - 2);
        g.setColor(LINEA_AMARILLA);
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, centroY - 2, ancho, centroY - 2);
        g.drawLine(0, centroY + 2, ancho, centroY + 2);
        g.setColor(LINEA_BLANCA);
        float[] dash = {15.0f, 15.0f};
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g.drawLine(0, inicioCalle + (anchoCalle / 4), ancho, inicioCalle + (anchoCalle / 4));
        g.drawLine(0, inicioCalle + (anchoCalle / 4) * 3, ancho, inicioCalle + (anchoCalle / 4) * 3);
        g.setStroke(new BasicStroke(1));
        dibujarAnden(g, 0, inicioCalle - 30, ancho, 30, true);
        dibujarAnden(g, 0, inicioCalle + anchoCalle, ancho, 30, false);
        calleLimiteSuperior = inicioCalle;
        calleLimiteInferior = inicioCalle + anchoCalle;
        calleLimiteIzquierdo = 0;
        calleLimiteDerecho = ancho;
        if (arbolesPos.isEmpty() && casasPos.isEmpty()) {
            generarLineaDecoracion(0, 0, ancho, inicioCalle - 40, true);
            generarLineaDecoracion(0, inicioCalle + anchoCalle + 40, ancho, alto - (inicioCalle + anchoCalle + 40), false);
        }
    }

    private void dibujarRectaVertical(Graphics2D g, int ancho, int alto) {
        int centroX = ancho / 2;
        int anchoCalle = 240;
        int inicioCalle = centroX - anchoCalle / 2;
        dibujarTexturaAsfalto(g, inicioCalle, 0, anchoCalle, alto);
        g.setColor(LINEA_BLANCA);
        g.setStroke(new BasicStroke(3));
        g.drawLine(inicioCalle + 2, 0, inicioCalle + 2, alto);
        g.drawLine(inicioCalle + anchoCalle - 2, 0, inicioCalle + anchoCalle - 2, alto);
        g.setColor(LINEA_AMARILLA);
        g.setStroke(new BasicStroke(2));
        g.drawLine(centroX - 2, 0, centroX - 2, alto);
        g.drawLine(centroX + 2, 0, centroX + 2, alto);
        g.setColor(LINEA_BLANCA);
        float[] dash = {15.0f, 15.0f};
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g.drawLine(inicioCalle + (anchoCalle / 4), 0, inicioCalle + (anchoCalle / 4), alto);
        g.drawLine(inicioCalle + (anchoCalle / 4) * 3, 0, inicioCalle + (anchoCalle / 4) * 3, alto);
        g.setStroke(new BasicStroke(1));
        dibujarAnden(g, inicioCalle - 30, 0, 30, alto, true);
        dibujarAnden(g, inicioCalle + anchoCalle, 0, 30, alto, false);
        calleLimiteSuperior = 0;
        calleLimiteInferior = alto;
        calleLimiteIzquierdo = inicioCalle;
        calleLimiteDerecho = inicioCalle + anchoCalle;
        if (arbolesPos.isEmpty() && edificiosPos.isEmpty()) {
            generarBloqueDecoracion(0, 0, inicioCalle - 40, alto, true);
            generarBloqueDecoracion(inicioCalle + anchoCalle + 40, 0, ancho - (inicioCalle + anchoCalle + 40), alto, false);
        }
    }

    private void dibujarInterseccionT(Graphics g, int ancho, int alto) {
        int centroX = ancho / 2; int centroY = alto / 2; int anchoCalle = 200;
        Graphics2D g2d = (Graphics2D) g;
        dibujarTexturaAsfalto(g2d, 0, 0, ancho, alto);
        g.setColor(CESPED);
        g.fillRect(0, 0, centroX - anchoCalle/2 - 30, centroY - anchoCalle/2 - 30);
        g.fillRect(centroX + anchoCalle/2 + 30, 0, ancho, centroY - anchoCalle/2 - 30);
        dibujarLineasInterseccion(g2d, ancho, alto, centroX, centroY, anchoCalle, true);
        dibujarAnden(g, 0, centroY + anchoCalle / 2, ancho, 30, false);
        dibujarAnden(g, centroX + anchoCalle / 2, 0, 30, centroY - anchoCalle/2, false);
        dibujarAnden(g, centroX - anchoCalle / 2 - 30, 0, 30, centroY - anchoCalle/2, true);
        calleLimiteSuperior = 0; calleLimiteInferior = alto; calleLimiteIzquierdo = 0; calleLimiteDerecho = ancho;
        if (arbolesPos.isEmpty()) {
            generarParque(0, 0, centroX - anchoCalle/2 - 40, centroY - anchoCalle/2 - 40);
            generarParque(centroX + anchoCalle/2 + 40, 0, ancho, centroY - anchoCalle/2 - 40);
            generarLineaDecoracion(0, centroY + anchoCalle/2 + 40, ancho, alto, false);
        }
    }

    private void dibujarInterseccionMas(Graphics g, int ancho, int alto) {
        int centroX = ancho / 2; int centroY = alto / 2; int anchoCalle = 200;
        Graphics2D g2d = (Graphics2D) g;
        dibujarTexturaAsfalto(g2d, 0, 0, ancho, alto);
        g.setColor(CESPED);
        int gap = anchoCalle/2 + 30;
        g.fillRect(0, 0, centroX - gap, centroY - gap);
        g.fillRect(centroX + gap, 0, ancho, centroY - gap);
        g.fillRect(0, centroY + gap, centroX - gap, alto);
        g.fillRect(centroX + gap, centroY + gap, ancho, alto);
        dibujarLineasInterseccion(g2d, ancho, alto, centroX, centroY, anchoCalle, false);
        dibujarAndenCruce(g2d, centroX, centroY, anchoCalle, gap);
        calleLimiteSuperior = 0; calleLimiteInferior = alto; calleLimiteIzquierdo = 0; calleLimiteDerecho = ancho;
        if (casasPos.isEmpty() && edificiosPos.isEmpty()) {
            edificiosPos.add(new Rectangle(20, 20, centroX - gap - 40, centroY - gap - 40));
            generarParque(centroX + gap + 20, 20, ancho - (centroX + gap + 40), centroY - gap - 40);
            generarLineaDecoracion(20, centroY + gap + 20, centroX - gap - 40, 150, false);
            generarBloqueDecoracion(centroX + gap + 20, centroY + gap + 20, ancho - (centroX + gap + 40), alto - (centroY + gap + 40), false);
        }
    }

    private void dibujarGlorietaRediseñada(Graphics2D g, int ancho, int alto) {
        int centroX = ancho / 2;
        int centroY = alto / 2;
        int radioExterior = 160;
        int radioInterior = 70;
        int anchoCalle = 180;

        dibujarTexturaAsfalto(g, centroX - anchoCalle/2, 0, anchoCalle, alto);
        dibujarTexturaAsfalto(g, 0, centroY - anchoCalle/2, ancho, anchoCalle);

        g.setColor(ASFALTO);
        g.fillOval(centroX - radioExterior, centroY - radioExterior, radioExterior * 2, radioExterior * 2);

        g.setColor(LINEA_BLANCA);
        Stroke oldStroke = g.getStroke();
        float[] dash = {10.0f, 10.0f};
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g.drawOval(centroX - (radioExterior-40), centroY - (radioExterior-40), (radioExterior-40) * 2, (radioExterior-40) * 2);
        g.setStroke(oldStroke);

        g.setColor(CESPED);
        g.fillOval(centroX - radioInterior, centroY - radioInterior, radioInterior * 2, radioInterior * 2);
        g.setColor(BORDE_ANDEN);
        g.setStroke(new BasicStroke(4));
        g.drawOval(centroX - radioInterior, centroY - radioInterior, radioInterior * 2, radioInterior * 2);
        g.setStroke(new BasicStroke(1));

        calleLimiteSuperior = 0;
        calleLimiteInferior = alto;
        calleLimiteIzquierdo = 0;
        calleLimiteDerecho = ancho;

        if(arbolesPos.isEmpty()){
            arbolesPos.add(new Point(centroX - 20, centroY - 20));
            arbolesPos.add(new Point(centroX + 20, centroY + 10));
            arbolesPos.add(new Point(centroX, centroY + 20));
        }
    }

    private void dibujarAnden(Graphics g, int x, int y, int ancho, int alto, boolean horizontal) {
        if (ancho <= 0 || alto <= 0) return;
        g.setColor(ANDEN); g.fillRect(x, y, ancho, alto);
        g.setColor(BORDE_ANDEN); g.drawRect(x, y, ancho, alto);
    }

    private void dibujarAndenCruce(Graphics2D g, int centroX, int centroY, int anchoCalle, int gap) {
        g.setColor(ANDEN);
        g.fillRect(centroX - gap - 30, 0, 30, altoMapa);
        g.fillRect(0, centroY - gap - 30, anchoMapa, 30);
    }

    private void dibujarLineasInterseccion(Graphics2D g, int ancho, int alto, int centroX, int centroY, int anchoCalle, boolean esT) {
        g.setColor(LINEA_BLANCA);
        g.fillRect(centroX - anchoCalle/2 - 50, centroY - anchoCalle/2, 10, anchoCalle);
        g.fillRect(centroX + anchoCalle/2 + 40, centroY - anchoCalle/2, 10, anchoCalle);
    }

    private void dibujarDecoraciones(Graphics2D g) {
        for (int i = 0; i < casasPos.size(); i++) dibujarCasa(g, casasPos.get(i), casasColores.get(i));
        for (Rectangle rect : edificiosPos) dibujarEdificio(g, rect);
        for (Point p : arbustosPos) dibujarArbusto(g, p.x, p.y);
        for (Point p : arbolesPos) dibujarArbol(g, p.x, p.y, 25);
    }

    private void dibujarArbol(Graphics2D g, int x, int y, int radio) {
        g.setColor(new Color(100, 70, 40)); g.fillOval(x - 5, y - 5, 10, 10);
        g.setColor(new Color(30, 100, 30)); g.fillOval(x - radio, y - radio, radio * 2, radio * 2);
    }

    private void dibujarArbusto(Graphics2D g, int x, int y) {
        g.setColor(new Color(20, 80, 20)); g.fillOval(x, y, 15, 15);
    }

    private void dibujarCasa(Graphics2D g, Rectangle r, Color colorTecho) {
        g.setColor(new Color(220, 220, 200)); g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(colorTecho); g.fillRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
    }

    private void dibujarEdificio(Graphics2D g, Rectangle r) {
        g.setColor(Color.GRAY); g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.BLACK); g.drawRect(r.x, r.y, r.width, r.height);
    }

    private void generarLineaDecoracion(int x, int y, int ancho, int alto, boolean norte) {
        int actualX = x + 20;
        while (actualX < x + ancho - 30) {
            int w = 50; int h = 50;
            casasPos.add(new Rectangle(actualX, y + (norte ? alto - h : 0), w, h));
            casasColores.add(COLORES_TECHOS[random.nextInt(COLORES_TECHOS.length)]);
            actualX += w + 30;
        }
    }

    private void generarBloqueDecoracion(int x, int y, int ancho, int alto, boolean oeste) {
        for (int i = 0; i < alto; i += 120) {
            edificiosPos.add(new Rectangle(x + 10, y + i + 10, ancho - 20, 100));
        }
    }

    private void generarParque(int x, int y, int ancho, int alto){
        for(int i=0; i<5; i++){
            arbolesPos.add(new Point(x + random.nextInt(ancho), y + random.nextInt(alto)));
        }
    }

    public boolean estaDentroDeCalle(int x, int y) {
        return (x >= calleLimiteIzquierdo && x <= calleLimiteDerecho &&
                y >= calleLimiteSuperior && y <= calleLimiteInferior);
    }

    public int getTipoMapa() { return tipoMapa; }
    public int[] getCarriles() { return carriles; }
    public int getCalleLimiteSuperior() { return calleLimiteSuperior; }
    public int getCalleLimiteInferior() { return calleLimiteInferior; }
    public int getCalleLimiteIzquierdo() { return calleLimiteIzquierdo; }
    public int getCalleLimiteDerecho() { return calleLimiteDerecho; }
}