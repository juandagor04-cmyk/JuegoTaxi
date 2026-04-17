import java.awt.*;
import java.util.Random;


public class Mapa {
    private int tipoMapa; // 0= Recta horizontal, 1= Recta vertical, 2=T, 3=+, 4=glorieta
    private int [] carriles = {150,250,350};

    private Random random = new Random();

    //Dimension del mapa
    private int anchoMapa , altoMapa;
    private int offsetX = 0, offsetY = 0; //Centrar el mapa

    //Bordes de la calle
    private int calleLimiteSuperior, calleLimiteInferior, calleLimiteIzquierdo, calleLimiteDerecho;

    //Constante Colores
    private final Color ASFALTO = new Color(40,40,45);
    private final Color LINEA_AMARILLA = new Color(255,220,0);
    private final Color LINEA_BLANCA = new Color(255,255,255);
    private final Color ANDEN = new Color (80,80,85);
    private final Color BORDE_ANDEN =new Color(100,100,105);
    private final Color CESPED = new Color (50,100,50);
    private final Color TIERRA = new Color (101,67,33);

    public Mapa(){
        generarMapa();
    }
    public void generarMapa(){
        tipoMapa = random.nextInt(5); //5  Tipos Diefrentes
    }
    public void dibujar (Graphics g, int ancho, int alto){
        anchoMapa= ancho;
        altoMapa = alto;

        //Fondo de tierra / cesped
        g.setColor(TIERRA);
        g.fillRect(0,0,ancho, alto);

        g.setColor(CESPED);
        for (int i = 0; i < 200; i++){
            g.fillRect(random.nextInt(ancho), random.nextInt(alto),2,2);
        }
        switch (tipoMapa){
            case 0:
                dibujarRectaHorizontal(g,ancho,alto);
                break;
            case 1:
                dibujarRectaVertical(g,ancho,alto);
                break;
            case 2:
                dibujarInterseccionT(g,ancho,alto);
                break;
            case 3:
                dibujarInterseccionMas(g,ancho,alto);
                break;
            case 4:
                dibujarGlorieta(g,ancho,alto);
                break;
        }
    }
    //Recta Horizontal
    private void dibujarRectaHorizontal(Graphics g, int ancho, int alto){

        int centroY = alto /2;
        int anchoCalle = 200;
        int inicioCalle = centroY - anchoCalle/2;

        //Calle Asfalto
        g.setColor(ASFALTO);
        g.fillRect(0,inicioCalle,ancho,anchoCalle);

        //Linea de la calle
        g.drawLine(0, inicioCalle + 5, ancho, inicioCalle + 5);
        g.drawLine(0,inicioCalle + anchoCalle - 5,ancho,inicioCalle + anchoCalle - 5);

        //Linea amarilla central discontinua
        g.setColor(LINEA_AMARILLA);
        ((Graphics2D)g).setStroke(new BasicStroke(3));
        for (int x = 0; x < ancho; x += 40){
            g.fillRect(x, centroY - 3, 20, 6);
        }
        //Carriles
        g.setColor(LINEA_BLANCA);
        ((Graphics2D)g).setStroke(new BasicStroke(1));
        for (int i=1; i<=2; i++){
            int yCarril = inicioCalle + (anchoCalle /3)*i;
            for (int x = 0; x < ancho; x +=30){
                g.fillRect(x, yCarril - 1, 15, 2);
            }
        }
        //Andenes
        dibujarAnden(g,0,inicioCalle - 30, ancho, 30,true);
        dibujarAnden(g,0,inicioCalle + anchoCalle, ancho, 30,false);

        //Establecer limites de la calle
        calleLimiteSuperior = inicioCalle;
        calleLimiteInferior = inicioCalle + anchoCalle;
        calleLimiteIzquierdo = 0;
        calleLimiteDerecho = ancho;
    }
    //Recta vertical
    private void dibujarRectaVertical(Graphics g, int ancho, int alto) {
        int CentroX = ancho / 2;
        int anchoCalle = 200;
        int inicioCalle = CentroX - anchoCalle / 2;

        //Calle asfalto
        g.setColor(ASFALTO);
        g.fillRect(inicioCalle, 0, anchoCalle, alto);

        //Bordes blancos
        g.setColor(LINEA_BLANCA);
        ((Graphics2D) g).setStroke(new BasicStroke(2));
        g.drawLine(inicioCalle + 5, 0, inicioCalle + 5, alto);
        g.drawLine(inicioCalle + anchoCalle - 5, 0, inicioCalle + anchoCalle - 5, alto);

        //Linea Amarilla cnetral discontinua
        g.setColor(LINEA_AMARILLA);
        ((Graphics2D) g).setStroke(new BasicStroke(3));
        for (int y = 0; y < alto; y += 40) {
            g.fillRect(CentroX - 3, y, 6, 20);
        }
        //Carriles
        g.setColor(LINEA_BLANCA);
        ((Graphics2D) g).setStroke(new BasicStroke(1));
        for (int i = 1; i <= 2; i++) {
            int xCarril = inicioCalle + (anchoCalle / 3) * i;
            for (int y = 0; y < alto; y += 30) {
                g.fillRect(xCarril - 1, y, 2, 15);
            }
        }
        //Andenes
        dibujarAnden(g, inicioCalle - 30, 0, 30, alto, true);
        dibujarAnden(g, inicioCalle + anchoCalle, 0, 30, alto, false);

        //Establecer limites de la calle
        calleLimiteSuperior = 0;
        calleLimiteInferior = alto;
        calleLimiteIzquierdo = inicioCalle;
        calleLimiteDerecho = inicioCalle + anchoCalle;
    }
    //Interseccion T
    private void dibujarInterseccionT(Graphics g, int alto, int ancho){
        int centroX = ancho /  2;
        int centroY = alto / 2;
        int anchoCalle = 200;

        //Calle Vertical
        g.setColor(ASFALTO);
        g.fillRect(centroX - anchoCalle/2, 0, anchoCalle, alto);

        //Calle Horinzontal
        g.fillRect(0,centroY - anchoCalle/2, ancho,anchoCalle);

        //Lineas y detalles
        dibujarLineasInterseccion(g, ancho, alto, centroX, centroY, anchoCalle, true);

        //Andenes
        dibujarAnden(g, 0, centroY + anchoCalle/2, ancho, 30, false);
        dibujarAnden(g, centroX + anchoCalle/2, 0, 30, alto, true);
        dibujarAnden(g, centroX - anchoCalle/2 - 30, 0, 30, alto, true);

        //Limites
        calleLimiteSuperior = centroY - anchoCalle/2;
        calleLimiteInferior = centroY + anchoCalle/2;
        calleLimiteIzquierdo = centroX - anchoCalle/2;
        calleLimiteDerecho = centroX + anchoCalle/2;
    }
    //Interseccion +
    private void dibujarInterseccionMas(Graphics g, int ancho, int alto){
        int centroX = ancho / 2;
        int centroY = alto / 2;
        int anchoCalle = 200;

        //Calle Vertical
        g.setColor(ASFALTO);
        g.fillRect(centroX - anchoCalle/2, 0, anchoCalle, alto);
        // Calle horizontal
        g.fillRect(0, centroY - anchoCalle/2, ancho, anchoCalle);

        // Líneas y detalles
        dibujarLineasInterseccion(g, ancho, alto, centroX, centroY, anchoCalle, false);

        // Andenes (4 esquinas)
        dibujarAnden(g, 0, 0, centroX - anchoCalle/2, centroY - anchoCalle/2, true);
        dibujarAnden(g, centroX + anchoCalle/2, 0, ancho - (centroX + anchoCalle/2), centroY - anchoCalle/2, true);
        dibujarAnden(g, 0, centroY + anchoCalle/2, centroX - anchoCalle/2, alto - (centroY + anchoCalle/2), true);
        dibujarAnden(g, centroX + anchoCalle/2, centroY + anchoCalle/2, ancho - (centroX + anchoCalle/2), alto - (centroY + anchoCalle/2), true);

        // Límites
        calleLimiteSuperior = centroY - anchoCalle/2;
        calleLimiteInferior = centroY + anchoCalle/2;
        calleLimiteIzquierdo = centroX - anchoCalle/2;
        calleLimiteDerecho = centroX + anchoCalle/2;
    }

    // GLORIETA
    private void dibujarGlorieta(Graphics g, int ancho, int alto) {
        int centroX = ancho / 2;
        int centroY = alto / 2;
        int radioExterior = 120;
        int radioInterior = 60;

        // Calle que rodea la glorieta
        g.setColor(ASFALTO);
        g.fillRect(0, centroY - 100, ancho, 200);
        g.fillRect(centroX - 100, 0, 200, alto);

        // Círculo exterior (calle)
        g.setColor(ASFALTO);
        g.fillOval(centroX - radioExterior, centroY - radioExterior, radioExterior * 2, radioExterior * 2);

        // Círculo interior (jardín)
        g.setColor(CESPED);
        g.fillOval(centroX - radioInterior, centroY - radioInterior, radioInterior * 2, radioInterior * 2);

        // Borde blanco de la glorieta
        g.setColor(LINEA_BLANCA);
        ((Graphics2D)g).setStroke(new BasicStroke(3));
        g.drawOval(centroX - radioExterior, centroY - radioExterior, radioExterior * 2, radioExterior * 2);
        g.drawOval(centroX - radioInterior, centroY - radioInterior, radioInterior * 2, radioInterior * 2);

        // Flechas direccionales
        g.setColor(LINEA_BLANCA);
        for (int i = 0; i < 4; i++) {
            double angulo = Math.toRadians(i * 90);
            int x = (int)(centroX + Math.cos(angulo) * (radioExterior - 20));
            int y = (int)(centroY + Math.sin(angulo) * (radioExterior - 20));
            g.fillOval(x - 5, y - 5, 10, 10);
        }

        // Andenes
        dibujarAnden(g, 0, 0, centroX - radioExterior, centroY - radioExterior, true);
        dibujarAnden(g, centroX + radioExterior, 0, ancho - (centroX + radioExterior), centroY - radioExterior, true);
        dibujarAnden(g, 0, centroY + radioExterior, centroX - radioExterior, alto - (centroY + radioExterior), true);
        dibujarAnden(g, centroX + radioExterior, centroY + radioExterior, ancho - (centroX + radioExterior), alto - (centroY + radioExterior), true);

        // Límites de la calle
        calleLimiteSuperior = centroY - 100;
        calleLimiteInferior = centroY + 100;
        calleLimiteIzquierdo = centroX - 100;
        calleLimiteDerecho = centroX + 100;
    }

    // Método auxiliar para dibujar andenes
    private void dibujarAnden(Graphics g, int x, int y, int ancho, int alto, boolean esAcera) {
        if (ancho <= 0 || alto <= 0) return;

        g.setColor(ANDEN);
        g.fillRect(x, y, ancho, alto);

        g.setColor(BORDE_ANDEN);
        g.drawRect(x, y, ancho, alto);

        // Textura del andén (líneas)
        g.setColor(new Color(90, 90, 95));
        for (int i = x + 5; i < x + ancho; i += 15) {
            for (int j = y + 5; j < y + alto; j += 15) {
                g.fillRect(i, j, 3, 3);
            }
        }
    }

   //Metodo de lineas
    private void dibujarLineasInterseccion(Graphics g, int ancho, int alto, int centroX, int centroY, int anchoCalle, boolean esT) {
        ((Graphics2D)g).setStroke(new BasicStroke(2));

        // Línea amarilla central
        g.setColor(LINEA_AMARILLA);

        if (!esT) {
            // Intersección completa: líneas en cruz
            g.drawLine(centroX, 0, centroX, centroY - anchoCalle/2);
            g.drawLine(centroX, centroY + anchoCalle/2, centroX, alto);
            g.drawLine(0, centroY, centroX - anchoCalle/2, centroY);
            g.drawLine(centroX + anchoCalle/2, centroY, ancho, centroY);
        } else {
            // Intersección T: solo tres direcciones
            g.drawLine(centroX, centroY + anchoCalle/2, centroX, alto);
            g.drawLine(0, centroY, centroX - anchoCalle/2, centroY);
            g.drawLine(centroX + anchoCalle/2, centroY, ancho, centroY);
        }

        // Bordes de carriles
        g.setColor(LINEA_BLANCA);
        int desplazamiento = anchoCalle / 3;

        for (int i = 1; i <= 2; i++) {
            int offset = desplazamiento * i;
            // Líneas horizontales
            if (!esT || (centroY + offset > centroY)) {
                for (int x = 0; x < ancho; x += 30) {
                    g.fillRect(x, centroY - anchoCalle/2 + offset - 1, 15, 2);
                }
            }
            // Líneas verticales
            for (int y = 0; y < alto; y += 30) {
                g.fillRect(centroX - anchoCalle/2 + offset - 1, y, 2, 15);
            }
        }
    }

    // Verificar si el taxi está dentro de la calle
    public boolean estaDentroDeCalle(int x, int y) {
        return (x >= calleLimiteIzquierdo && x <= calleLimiteDerecho &&
                y >= calleLimiteSuperior && y <= calleLimiteInferior);
    }

    // Métodos getters
    public int getTipoMapa() {
        return tipoMapa;
    }

    public int getCarrilAleatorio() {
        return carriles[random.nextInt(carriles.length)];
    }

    public int[] getCarriles() {
        return carriles;
    }

    public int getCalleLimiteSuperior() { return calleLimiteSuperior; }
    public int getCalleLimiteInferior() { return calleLimiteInferior; }
    public int getCalleLimiteIzquierdo() { return calleLimiteIzquierdo; }
    public int getCalleLimiteDerecho() { return calleLimiteDerecho; }

    }

