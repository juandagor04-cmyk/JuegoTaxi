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
    private void dibujarRectaVertical(Graphics g, int ancho, int alto){
        int CentroX = ancho / 2;
        int anchoCalle = 200;
        int inicioCalle = CentroX - anchoCalle/2;

        //Calle asfalto
        g.setColor(ASFALTO);
        g.fillRect(inicioCalle,0,anchoCalle, alto);

        //Bordes blancos
        g.setColor(LINEA_BLANCA);
        ((Graphics2D)g).setStroke(new BasicStroke(2));
        g.drawLine(inicioCalle + 5, 0, inicioCalle + 5, alto);
        g.drawLine(inicioCalle + anchoCalle - 5, 0, inicioCalle + anchoCalle - 5, alto);

        //Linea Amarilla cnetral discontinua
        g.setColor(LINEA_AMARILLA);
        ((Graphics2D)g).setStroke(new BasicStroke(3));
        for (int y = 0; y < alto; y+= 40){
            g.fillRect(centroX - 3, y, 6, 20);
        }


    }
}