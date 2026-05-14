import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

public class Mapa {
    private int tipoMapa; // 0= Recta horizontal, 1= Recta vertical, 2=T, 3=+, 4=glorieta
    // Se mantienen los carriles originales para lógica de spawn de otros autos si aplica
    private int[] carriles = {150, 250, 350};

    private Random random = new Random();

    // Dimension del mapa
    private int anchoMapa, altoMapa;

    // Bordes de la calle (para lógica de colisión del taxi)
    private int calleLimiteSuperior, calleLimiteInferior, calleLimiteIzquierdo, calleLimiteDerecho;

    // --- Paleta de Colores Refinada y Nuevas ---
    private final Color ASFALTO = new Color(55, 55, 60);
    private final Color ASFALTO_RUIDO = new Color(60, 60, 65); // Para textura
    private final Color LINEA_AMARILLA = new Color(255, 210, 0);
    private final Color LINEA_BLANCA = new Color(240, 240, 240);
    private final Color ANDEN = new Color(160, 160, 165);
    private final Color BORDE_ANDEN = new Color(130, 130, 135);
    private final Color CESPED = new Color(60, 140, 60);
    private final Color CESPED_VARIACION = new Color(70, 150, 70); // Para textura
    private final Color TIERRA = new Color(120, 90, 60);

    // Colores para Edificios/Casas
    private final Color[] COLORES_TECHOS = {
            new Color(180, 70, 50),  // Teja roja
            new Color(100, 100, 110), // Gris moderno
            new Color(150, 120, 90),  // Marrón claro
            new Color(80, 120, 80)    // Verde oscuro
    };
    private final Color COLOR_VENTANA = new Color(200, 230, 255, 150); // Azul claro traslúcido

    // --- Estructuras para guardar decoraciones aleatorias fijas por mapa ---
    private ArrayList<Point> arbolesPos = new ArrayList<>();
    private ArrayList<Point> arbustosPos = new ArrayList<>();
    private ArrayList<Rectangle> casasPos = new ArrayList<>();
    private ArrayList<Color> casasColores = new ArrayList<>();
    private ArrayList<Rectangle> edificiosPos = new ArrayList<>();

    public Mapa() {
        generarMapa();
    }

    public void generarMapa() {
        tipoMapa = random.nextInt(5); // 5 Tipos Diferentes

        // Limpiar decoraciones del mapa anterior
        arbolesPos.clear();
        arbustosPos.clear();
        casasPos.clear();
        casasColores.clear();
        edificiosPos.clear();

        // Las decoraciones específicas se generarán dentro de cada método dibujarRecta...
        // la primera vez que se ejecute para ese mapa.
    }

    public void dibujar(Graphics g, int ancho, int alto) {
        anchoMapa = ancho;
        altoMapa = alto;
        Graphics2D g2d = (Graphics2D) g;

        // Activar Anti-aliasing para gráficos suaves
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Fondo Base (Tierra)
        g.setColor(TIERRA);
        g.fillRect(0, 0, ancho, alto);

        // 2. Capa de Césped base con variación de textura
        dibujarBaseCesped(g2d, ancho, alto);

        // 3. Dibujar la infraestructura vial según el tipo (Asfalto, líneas, andenes)
        switch (tipoMapa) {
            case 0: dibujarRectaHorizontal(g2d, ancho, alto); break;
            case 1: dibujarRectaVertical(g2d, ancho, alto); break;
            case 2: dibujarInterseccionT(g2d, ancho, alto); break;
            case 3: dibujarInterseccionMas(g2d, ancho, alto); break;
            case 4: dibujarGlorieta(g2d, ancho, alto); break;
        }

        // 4. Dibujar Decoraciones (Vegetación y Construcciones) sobre el césped y fuera de las calles
        dibujarDecoraciones(g2d);
    }

    // --- MÉTODOS DE TEXTURA BASE ---

    private void dibujarBaseCesped(Graphics2D g, int ancho, int alto) {
        g.setColor(CESPED);
        g.fillRect(0, 0, ancho, alto);

        // Manchas de variación de césped aleatorias pero fijas (seed constante)
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

        // Efecto de ruido/grano en el asfalto (seed constante)
        Random noise = new Random(1337);
        g.setColor(ASFALTO_RUIDO);
        for (int i = 0; i < (width * height) / 50; i++) {
            int px = noise.nextInt(width) + x;
            int py = noise.nextInt(height) + y;
            g.fillRect(px, py, 1, 1);
        }
    }

    // --- MÉTODOS DE DIBUJO DE MAPAS ESPECÍFICOS E INTEGRACIÓN DE DECORACIÓN ---

    private void dibujarRectaHorizontal(Graphics2D g, int ancho, int alto) {
        int centroY = alto / 2;
        int anchoCalle = 240; // Ajustado para 3 carriles cómodos
        int inicioCalle = centroY - anchoCalle / 2;

        dibujarTexturaAsfalto(g, 0, inicioCalle, ancho, anchoCalle);

        // Líneas de borde sólidas
        g.setColor(LINEA_BLANCA);
        g.setStroke(new BasicStroke(3));
        g.drawLine(0, inicioCalle + 2, ancho, inicioCalle + 2);
        g.drawLine(0, inicioCalle + anchoCalle - 2, ancho, inicioCalle + anchoCalle - 2);

        // Línea amarilla central doble continua
        g.setColor(LINEA_AMARILLA);
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, centroY - 2, ancho, centroY - 2);
        g.drawLine(0, centroY + 2, ancho, centroY + 2);

        // Carriles discontinuos blancos
        g.setColor(LINEA_BLANCA);
        // Stroke discontinuo
        float[] dash = {15.0f, 15.0f};
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

        int yCarril1 = inicioCalle + (anchoCalle / 4);
        int yCarril2 = inicioCalle + (anchoCalle / 4) * 3;
        g.drawLine(0, yCarril1, ancho, yCarril1);
        g.drawLine(0, yCarril2, ancho, yCarril2);

        // Restaurar stroke básico
        g.setStroke(new BasicStroke(1));

        // Andenes con textura mejorada
        dibujarAnden(g, 0, inicioCalle - 30, ancho, 30, true);
        dibujarAnden(g, 0, inicioCalle + anchoCalle, ancho, 30, false);

        // Establecer limites de la calle
        calleLimiteSuperior = inicioCalle;
        calleLimiteInferior = inicioCalle + anchoCalle;
        calleLimiteIzquierdo = 0;
        calleLimiteDerecho = ancho;

        // Generar Decoración aleatoria si es la primera vez para este mapa
        if (arbolesPos.isEmpty() && casasPos.isEmpty()) {
            // Zona Norte (sobre el andén superior)
            generarLineaDecoracion(0, 0, ancho, inicioCalle - 40, true);
            // Zona Sur (bajo el andén inferior)
            generarLineaDecoracion(0, inicioCalle + anchoCalle + 40, ancho, alto - (inicioCalle + anchoCalle + 40), false);
        }
    }

    private void dibujarRectaVertical(Graphics2D g, int ancho, int alto) {
        int centroX = ancho / 2;
        int anchoCalle = 240;
        int inicioCalle = centroX - anchoCalle / 2;

        dibujarTexturaAsfalto(g, inicioCalle, 0, anchoCalle, alto);

        // Bordes blancos
        g.setColor(LINEA_BLANCA);
        g.setStroke(new BasicStroke(3));
        g.drawLine(inicioCalle + 2, 0, inicioCalle + 2, alto);
        g.drawLine(inicioCalle + anchoCalle - 2, 0, inicioCalle + anchoCalle - 2, alto);

        // Línea Amarilla central doble continua
        g.setColor(LINEA_AMARILLA);
        g.setStroke(new BasicStroke(2));
        g.drawLine(centroX - 2, 0, centroX - 2, alto);
        g.drawLine(centroX + 2, 0, centroX + 2, alto);

        // Carriles discontinuos blancos
        g.setColor(LINEA_BLANCA);
        float[] dash = {15.0f, 15.0f};
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

        int xCarril1 = inicioCalle + (anchoCalle / 4);
        int xCarril2 = inicioCalle + (anchoCalle / 4) * 3;
        g.drawLine(xCarril1, 0, xCarril1, alto);
        g.drawLine(xCarril2, 0, xCarril2, alto);

        g.setStroke(new BasicStroke(1));

        // Andenes
        dibujarAnden(g, inicioCalle - 30, 0, 30, alto, true);
        dibujarAnden(g, inicioCalle + anchoCalle, 0, 30, alto, false);

        // Establecer limites de la calle
        calleLimiteSuperior = 0;
        calleLimiteInferior = alto;
        calleLimiteIzquierdo = inicioCalle;
        calleLimiteDerecho = inicioCalle + anchoCalle;

        // Generar Decoración
        if (arbolesPos.isEmpty() && edificiosPos.isEmpty()) {
            // Zona Oeste (Edificios grandes y árboles)
            generarBloqueDecoracion(0, 0, inicioCalle - 40, alto, true);
            // Zona Este (Mezcla)
            generarBloqueDecoracion(inicioCalle + anchoCalle + 40, 0, ancho - (inicioCalle + anchoCalle + 40), alto, false);
        }
    }

    private void dibujarInterseccionT(Graphics g, int ancho, int alto) {
        int centroX = ancho / 2;
        int centroY = alto / 2;
        int anchoCalle = 200;
        Graphics2D g2d = (Graphics2D) g;

        // Base Asfalto completa
        dibujarTexturaAsfalto(g2d, 0, 0, ancho, alto);

        // Volver a dibujar césped en las esquinas superiores donde no hay calle
        g.setColor(CESPED);
        // Esquina NO (Noroeste)
        g.fillRect(0, 0, centroX - anchoCalle/2 - 30, centroY - anchoCalle/2 - 30);
        // Esquina NE (Noreste)
        g.fillRect(centroX + anchoCalle/2 + 30, 0, ancho, centroY - anchoCalle/2 - 30);

        // Detalles de líneas y PASOS DE CEBRA
        dibujarLineasInterseccion(g2d, ancho, alto, centroX, centroY, anchoCalle, true);

        // Andenes complejos (redondeados en las esquinas interiores)
        dibujarAnden(g, 0, centroY + anchoCalle / 2, ancho, 30, false); // Sur horizontal continuo
        dibujarAnden(g, centroX + anchoCalle / 2, 0, 30, centroY - anchoCalle/2, false); // Este norte vertical
        dibujarAnden(g, centroX - anchoCalle / 2 - 30, 0, 30, centroY - anchoCalle/2, true); // Oeste norte vertical

        // Esquinas redondeadas del andén
        g.setColor(ANDEN);
        g.fillOval(centroX - anchoCalle/2 - 30, centroY - anchoCalle/2 - 30, 60, 60);
        g.fillOval(centroX + anchoCalle/2 - 30, centroY - anchoCalle/2 - 30, 60, 60);

        // Bordes de las esquinas redondeadas
        g.setColor(BORDE_ANDEN);
        g2d.setStroke(new BasicStroke(2));
        g.drawOval(centroX - anchoCalle/2 - 30, centroY - anchoCalle/2 - 30, 60, 60);
        g.drawOval(centroX + anchoCalle/2 - 30, centroY - anchoCalle/2 - 30, 60, 60);
        g2d.setStroke(new BasicStroke(1));

        // Limites (Toda la pantalla es calle en cruces para simplificar)
        calleLimiteSuperior = 0; calleLimiteInferior = alto;
        calleLimiteIzquierdo = 0; calleLimiteDerecho = ancho;

        // Generar Decoración en las zonas de césped restantes
        if (arbolesPos.isEmpty()) {
            // Decorar esquinas superiores (NO y NE) como parques
            generarParque(0, 0, centroX - anchoCalle/2 - 40, centroY - anchoCalle/2 - 40);
            generarParque(centroX + anchoCalle/2 + 40, 0, ancho, centroY - anchoCalle/2 - 40);
            // Decorar franja sur bajo el andén
            generarLineaDecoracion(0, centroY + anchoCalle/2 + 40, ancho, alto, false);
        }
    }

    private void dibujarInterseccionMas(Graphics g, int ancho, int alto) {
        int centroX = ancho / 2;
        int centroY = alto / 2;
        int anchoCalle = 200;
        Graphics2D g2d = (Graphics2D) g;

        dibujarTexturaAsfalto(g2d, 0, 0, ancho, alto);

        // Re-dibujar césped en las 4 esquinas exteriores
        g.setColor(CESPED);
        int gap = anchoCalle/2 + 30; // Espacio calle + andén
        g.fillRect(0, 0, centroX - gap, centroY - gap); // NO
        g.fillRect(centroX + gap, 0, ancho, centroY - gap); // NE
        g.fillRect(0, centroY + gap, centroX - gap, alto); // SO
        g.fillRect(centroX + gap, centroY + gap, ancho, alto); // SE

        // Líneas y PASOS DE CEBRA
        dibujarLineasInterseccion(g2d, ancho, alto, centroX, centroY, anchoCalle, false);

        // Andenes (4 esquinas con bordes redondeados hacia el centro)
        g.setColor(ANDEN);
        dibujarAndenCruce(g2d, centroX, centroY, anchoCalle, gap);

        // Limites
        calleLimiteSuperior = 0; calleLimiteInferior = alto;
        calleLimiteIzquierdo = 0; calleLimiteDerecho = ancho;

        // Generar Decoración aleatoria pesada (Edificios y Casas) en las 4 esquinas
        if (casasPos.isEmpty() && edificiosPos.isEmpty()) {
            // NO: Un edificio grande
            edificiosPos.add(new Rectangle(20, 20, centroX - gap - 40, centroY - gap - 40));

            // NE: Parque con árboles y arbustos
            generarParque(centroX + gap + 20, 20, ancho - (centroX + gap + 40), centroY - gap - 40);

            // SO: Fila de casas
            generarLineaDecoracion(20, centroY + gap + 20, centroX - gap - 40, 150, false);

            // SE: Mezcla
            generarBloqueDecoracion(centroX + gap + 20, centroY + gap + 20, ancho - (centroX + gap + 40), alto - (centroY + gap + 40), false);
        }
    }

    // GLORIETA Mejorada
    private void dibujarGlorieta(Graphics g, int ancho, int alto) {
        int centroX = ancho / 2;
        int centroY = alto / 2;
        int radioExterior = 150; // Más grande
        int radioInterior = 80; // Jardín más grande
        int anchoCalleEntrada = 160;
        Graphics2D g2d = (Graphics2D) g;

        // Fondo Asfalto base
        dibujarTexturaAsfalto(g2d, 0, 0, ancho, alto);

        // Re-dibujar Césped exterior en esquinas (simplificado)
        g.setColor(CESPED);
        int gapOut = radioExterior + 30;
        g.fillRect(0,0, centroX - gapOut, centroY - gapOut); // NO
        // ... (se podrían añadir las otras 3 esquinas igual)

        // Círculo exterior glorieta (Asfalto limpio)
        g.setColor(ASFALTO);
        g.fillOval(centroX - radioExterior, centroY - radioExterior, radioExterior * 2, radioExterior * 2);

        // Líneas de carril discontinuas DENTRO de la glorieta
        g.setColor(LINEA_BLANCA);
        float[] dashArr = {10, 10};
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashArr, 0.0f));
        int radioMedio = radioInterior + (radioExterior - radioInterior) / 2;
        g.drawOval(centroX - radioMedio, centroY - radioMedio, radioMedio * 2, radioMedio * 2);
        g2d.setStroke(new BasicStroke(1));

        // Círculo interior (Jardín central)
        g.setColor(CESPED);
        g.fillOval(centroX - radioInterior, centroY - radioInterior, radioInterior * 2, radioInterior * 2);

        // Decoración fija jardín central (Árbol grande y arbustos)
        dibujarArbol(g2d, centroX, centroY, 40);
        dibujarArbusto(g2d, centroX + 50, centroY + 30);
        dibujarArbusto(g2d, centroX - 50, centroY - 30);

        // Borde blanco grueso de la glorieta (Interior y Exterior)
        g.setColor(LINEA_BLANCA);
        g2d.setStroke(new BasicStroke(3));
        g.drawOval(centroX - radioExterior, centroY - radioExterior, radioExterior * 2, radioExterior * 2);
        g.drawOval(centroX - radioInterior, centroY - radioInterior, radioInterior * 2, radioInterior * 2);
        g2d.setStroke(new BasicStroke(1));

        // Calles de entrada/salida (quitar césped/andén exterior)
        g.setColor(ASFALTO);
        g.fillRect(0, centroY - anchoCalleEntrada/2, centroX, anchoCalleEntrada); // Oeste
        g.fillRect(centroX, centroY - anchoCalleEntrada/2, ancho - centroX, anchoCalleEntrada); // Este
        g.fillRect(centroX - anchoCalleEntrada/2, 0, anchoCalleEntrada, centroY); // Norte
        g.fillRect(centroX - anchoCalleEntrada/2, centroY, anchoCalleEntrada, alto - centroY); // Sur

        // Andenes exteriores (simplificados redondos)
        g.setColor(ANDEN);
        // ... (Código para dibujar andenes redondos exteriores si se desea)

        // Límites de la calle
        calleLimiteSuperior = 0; calleLimiteInferior = alto;
        calleLimiteIzquierdo = 0; calleLimiteDerecho = ancho;

        // Generar Decoración exterior
        if(arbolesPos.isEmpty()){
            generarParque(20, 20, centroX - gapOut - 20, centroY - gapOut - 20);
            // ... añadir decoración en otras esquinas
        }
    }

    // --- MÉTODOS AUXILIARES DE DIBUJO DE INFRAESTRUCTURA ---

    private void dibujarAnden(Graphics g, int x, int y, int ancho, int alto, boolean horizontal) {
        if (ancho <= 0 || alto <= 0) return;
        Graphics2D g2d = (Graphics2D) g;

        g.setColor(ANDEN);
        g.fillRect(x, y, ancho, alto);

        g.setColor(BORDE_ANDEN);
        g2d.setStroke(new BasicStroke(2));
        g.drawRect(x, y, ancho, alto);
        g2d.setStroke(new BasicStroke(1));

        // Textura de baldosas (líneas finas grises)
        g.setColor(new Color(140, 140, 145));
        if (horizontal) {
            for (int i = x + 20; i < x + ancho; i += 20) {
                g.drawLine(i, y, i, y + alto);
            }
        } else {
            for (int i = y + 20; i < y + alto; i += 20) {
                g.drawLine(x, i, x + ancho, i);
            }
        }
    }

    private void dibujarAndenCruce(Graphics2D g, int centroX, int centroY, int anchoCalle, int gap) {
        int r = 60; // Radio esquina redondeada
        // NO
        g.fillRoundRect(centroX - gap - 30, 0, 30, centroY - gap, r, r); // Vertical
        g.fillRoundRect(0, centroY - gap - 30, centroX - gap, 30, r, r); // Horizontal
        g.fillOval(centroX - gap - 30, centroY - gap - 30, r, r); // Esquina

        // NE
        g.fillRoundRect(centroX + gap, 0, 30, centroY - gap, r, r);
        g.fillRoundRect(centroX + gap, centroY - gap - 30, anchoMapa - centroX - gap, 30, r, r);
        g.fillOval(centroX + gap - 30, centroY - gap - 30, r, r); // Ajuste posición óvalo

        // SO
        g.fillRoundRect(centroX - gap - 30, centroY + gap, 30, altoMapa - centroY - gap, r, r);
        g.fillRoundRect(0, centroY + gap, centroX - gap, 30, r, r);
        g.fillOval(centroX - gap - 30, centroY + gap - 30, r, r);

        // SE
        g.fillRoundRect(centroX + gap, centroY + gap, 30, altoMapa - centroY - gap, r, r);
        g.fillRoundRect(centroX + gap, centroY + gap, anchoMapa - centroX - gap, 30, r, r);
        g.fillOval(centroX + gap - 30, centroY + gap - 30, r, r);
    }

    private void dibujarLineasInterseccion(Graphics2D g, int ancho, int alto, int centroX, int centroY, int anchoCalle, boolean esT) {
        // --- 1. PASOS DE CEBRA (Cebreado blanco grueso) ---
        g.setColor(LINEA_BLANCA);
        int largoCebra = 40;
        int anchoLineaCebra = 10;
        int espacioCebra = 10;
        int rectCebraY = centroY - anchoCalle/2 + 10;
        int rectCebraH = anchoCalle - 20;

        // Oeste (Horizontal)
        for (int i = rectCebraY; i < centroY + anchoCalle/2 - 10; i += (anchoLineaCebra + espacioCebra)) {
            g.fillRect(centroX - anchoCalle/2 - largoCebra - 10, i, largoCebra, anchoLineaCebra);
        }
        // Este (Horizontal)
        for (int i = rectCebraY; i < centroY + anchoCalle/2 - 10; i += (anchoLineaCebra + espacioCebra)) {
            g.fillRect(centroX + anchoCalle/2 + 10, i, largoCebra, anchoLineaCebra);
        }
        // Sur (Vertical)
        for (int i = centroX - anchoCalle/2 + 10; i < centroX + anchoCalle/2 - 10; i += (anchoLineaCebra + espacioCebra)) {
            g.fillRect(i, centroY + anchoCalle/2 + 10, anchoLineaCebra, largoCebra);
        }

        if (!esT) {
            // Norte (Vertical, solo si no es T)
            for (int i = centroX - anchoCalle/2 + 10; i < centroX + anchoCalle/2 - 10; i += (anchoLineaCebra + espacioCebra)) {
                g.fillRect(i, centroY - anchoCalle/2 - largoCebra - 10, anchoLineaCebra, largoCebra);
            }
        }

        // --- 2. Líneas Amarillas centrales discontinuas acercándose ---
        g.setColor(LINEA_AMARILLA);
        float[] dashArr = {10, 10};
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashArr, 0.0f));

        int stopLineGap = largoCebra + 20; // Espacio antes del paso de cebra

        // Sur
        g.drawLine(centroX, centroY + anchoCalle/2 + stopLineGap, centroX, alto);
        // Oeste
        g.drawLine(0, centroY, centroX - anchoCalle/2 - stopLineGap, centroY);
        // Este
        g.drawLine(centroX + anchoCalle/2 + stopLineGap, centroY, ancho, centroY);

        if (!esT) g.drawLine(centroX, 0, centroX, centroY - anchoCalle/2 - stopLineGap);

        g.setStroke(new BasicStroke(1));
    }

    // --- SISTEMA DE DIBUJO DE DECORACIONES (Vegetación y Edificios) ---

    private void dibujarDecoraciones(Graphics2D g) {
        // 1. Dibujar Casas
        for (int i = 0; i < casasPos.size(); i++) {
            dibujarCasa(g, casasPos.get(i), casasColores.get(i));
        }
        // 2. Dibujar Edificios
        for (Rectangle rect : edificiosPos) {
            dibujarEdificio(g, rect);
        }
        // 3. Dibujar Arbustos
        for (Point p : arbustosPos) {
            dibujarArbusto(g, p.x, p.y);
        }
        // 4. Dibujar Árboles (al final para que la copa tape el tronco)
        for (Point p : arbolesPos) {
            dibujarArbol(g, p.x, p.y, 25); // Radio estándar
        }
    }

    // Diseños individuales de elementos (vistos desde arriba)

    private void dibujarArbol(Graphics2D g, int x, int y, int radio) {
        // Sombra arrojada sutil
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x - radio + 5, y - radio + 5, radio * 2, radio * 2);

        // Tronco (pequeño círculo marrón central)
        g.setColor(new Color(100, 70, 40));
        g.fillOval(x - 5, y - 5, 10, 10);

        // Copa (Círculo principal verde oscuro)
        g.setColor(new Color(30, 100, 30));
        g.fillOval(x - radio, y - radio, radio * 2, radio * 2);

        // Brillo/Textura copa (Círculo más claro superpuesto desentrado)
        g.setColor(new Color(50, 130, 50));
        g.fillOval(x - radio + 5, y - radio + 5, radio, radio);
    }

    private void dibujarArbusto(Graphics2D g, int x, int y) {
        g.setColor(new Color(20, 80, 20));
        // Varios óvalos pequeños pegados para dar forma orgánica
        g.fillOval(x, y, 15, 15);
        g.fillOval(x + 8, y + 2, 12, 12);
        g.fillOval(x + 4, y + 6, 14, 14);
    }

    private void dibujarCasa(Graphics2D g, Rectangle r, Color colorTecho) {
        // Base/Paredes (ligeramente visibles en los bordes)
        g.setColor(new Color(220, 220, 200));
        g.fillRect(r.x, r.y, r.width, r.height);

        // Techo (Color principal aleatorio)
        g.setColor(colorTecho);
        g.fillRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4);

        // Línea de cumbrera (división del techo a dos aguas)
        g.setColor(colorTecho.brighter());
        g.fillRect(r.x + r.width/2 - 1, r.y + 4, 2, r.height - 8);

        // Pequeña chimenea negra
        g.setColor(Color.DARK_GRAY);
        g.fillRect(r.x + r.width - 10, r.y + 8, 5, 5);
    }

    private void dibujarEdificio(Graphics2D g, Rectangle r) {
        // Techo plano gris oscuro
        g.setColor(COLORES_TECHOS[1]); // Gris
        g.fillRect(r.x, r.y, r.width, r.height);

        // Borde pretil negro
        g.setColor(Color.BLACK);
        g.drawRect(r.x, r.y, r.width, r.height);

        // Unidades de Aire Acondicionado / Maquinaria en el techo
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(r.x + 10, r.y + 10, 15, 15);
        // Detalle rejilla AA
        g.setColor(Color.GRAY);
        g.fillRect(r.x + 12, r.y + 12, 11, 11);

        // Claraboya (ventana de techo azulada)
        g.setColor(COLOR_VENTANA);
        g.fillRect(r.x + r.width/2 - 10, r.y + r.height/2 - 10, 20, 20);
    }

    // --- MÉTODOS DE GENERACIÓN ALEATORIA DE DECORACIÓN POR ZONAS ---

    // Genera una fila de casas y árboles (para mapas rectos horizontales)
    private void generarLineaDecoracion(int x, int y, int ancho, int alto, boolean norte) {
        int actualX = x + 20;
        while (actualX < x + ancho - 30) {
            if (random.nextBoolean()) {
                // Generar Casa
                int w = random.nextInt(30) + 40; // Ancho 40-70
                int h = random.nextInt(20) + 40; // Alto 40-60
                // Posicionar pegado al andén o al borde del mapa
                int posY = y + (norte ? alto - h : 0);
                casasPos.add(new Rectangle(actualX, posY, w, h));
                casasColores.add(COLORES_TECHOS[random.nextInt(COLORES_TECHOS.length)]);
                actualX += w + 20; // Espacio para la siguiente
            } else {
                // Generar grupo de árboles
                int num = random.nextInt(3) + 1;
                for(int i=0; i<num; i++){
                    arbolesPos.add(new Point(actualX + random.nextInt(20), y + random.nextInt(alto)));
                }
                actualX += 60;
            }
        }
    }

    // Genera edificios grandes y vegetación densa (para mapas rectos verticales)
    private void generarBloqueDecoracion(int x, int y, int ancho, int alto, boolean oeste) {
        for (int i = 0; i < alto; i += 120) {
            if (random.nextInt(10) < 7) { // 70% probabilidad edificio
                int w = ancho - 20;
                int h = 100;
                edificiosPos.add(new Rectangle(x + 10, y + i + 10, w, h));
            } else {
                // Relleno vegetación densa
                arbolesPos.add(new Point(x + ancho/2, y + i + 50));
                arbustosPos.add(new Point(x + 20, y + i + 20));
                arbustosPos.add(new Point(x + ancho - 20, y + i + 80));
            }
        }
    }

    // Genera un área densa de árboles y arbustos (como un parque en esquinas de cruces)
    private void generarParque(int x, int y, int ancho, int alto){
        int densidad = (ancho * alto) / 2500; // Un elemento cada 50x50 píxeles aprox
        for(int i=0; i<densidad; i++){
            int posX = random.nextInt(ancho-40)+x+20;
            int posY = random.nextInt(alto-40)+y+20;
            if(random.nextBoolean()){
                arbolesPos.add(new Point(posX, posY));
            } else {
                arbustosPos.add(new Point(posX, posY));
            }
        }
        // Fuente central simplificada (un óvalo gris)
        if(ancho > 100 && alto > 100){
            // Usamos edificiosPos para dibujar la fuente gris plano
            edificiosPos.add(new Rectangle(x + ancho/2 - 15, y + alto/2 - 15, 30, 30));
        }
    }

    // --- LÓGICA DE JUEGO MANTENIDA ---

    public boolean estaDentroDeCalle(int x, int y) {
        // En los cruces y glorieta definidos ahora, casi todo es calle.
        // Se mantiene la lógica original basada en los límites rectangulares
        // calculados en los métodos dibujarRecta...
        // Para cruces, estos límites cubren toda la zona central.
        return (x >= calleLimiteIzquierdo && x <= calleLimiteDerecho &&
                y >= calleLimiteSuperior && y <= calleLimiteInferior);
    }

    public int getTipoMapa() { return tipoMapa; }
    public int getCarrilAleatorio() { return carriles[random.nextInt(carriles.length)]; }
    public int[] getCarriles() { return carriles; }
    public int getCalleLimiteSuperior() { return calleLimiteSuperior; }
    public int getCalleLimiteInferior() { return calleLimiteInferior; }
    public int getCalleLimiteIzquierdo() { return calleLimiteIzquierdo; }
    public int getCalleLimiteDerecho() { return calleLimiteDerecho; }
}