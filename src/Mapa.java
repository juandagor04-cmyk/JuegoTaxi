import java.awt.*;
import java.util.Random;


public class Mapa {
    private int tipoMapa; //0=recta, 1=T, 2=+, 3=glorieta
    private int[] carriles ={100, 200, 300};

    private Random random = new Random();

    public Mapa(){
        generarMapa();
    }
    public void generarMapa(){
        tipoMapa = random.nextInt(4);
    }
    public void dibujar(Graphics g, int ancho, int alto){

        //Fondo
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,ancho,alto);
        switch (tipoMapa) {
            case 0:
                dibujarRecta(g, ancho, alto);
                break;
            case 1:
                dibujarInterseccionT(g, ancho, alto);
                break;
            case 2:
                dibujarInterseccionMas(g, ancho, alto);
                break;
            case 3:
                dibujarGlorieta(g, ancho, alto);
                break;
        }
    }
    //Recta
    private void dibujarRecta(Graphics g, int ancho, int alto){
        g.setColor(Color.WHITE);
        for (int x : carriles){
            g.drawLine(x,0,x,alto);
        }
    }
    //Interseccion T
    private void dibujarInterseccionT(Graphics g, int ancho, int alto){
        g.setColor(Color.WHITE);

        //Vertical
        g.fillRect(180, 0, 40, alto);

        //Horinzontal
        g.fillRect(0,0,ancho,40);
    }
    //Interseccion +
    private void dibujarInterseccionMas (Graphics g, int ancho, int alto){
        g.setColor(Color.WHITE);
        g.fillRect(180,0,40,alto); //Vertical
        g.fillRect(0, 180,ancho, 40); //Horinzontal
    }
    //Glorieta
    private void dibujarGlorieta(Graphics g, int ancho, int alto){
        //Calle Fondo
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,ancho,alto);

        //Circulo
        g.setColor(Color.WHITE);
        g.drawOval(ancho/2 - 80, alto/2 - 80, 160, 160);
    }
    //Aleatorio
    public int getCarrilALetatorio(){
        return carriles [random.nextInt(carriles.length)];
    }
    public int []getCarriles (){
        return carriles;
    }
}
