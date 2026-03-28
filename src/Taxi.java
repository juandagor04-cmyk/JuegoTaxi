import java.awt.*;
public class Taxi {
    private int x;
    private int y;
    private int velocidad;

    private int ancho = 50;
    private int alto = 30;

    public Taxi(int x, int y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
    }

    //Movimiento
    public void moverIzquierda(){
        x -= velocidad;
    }
    public void moverDerecha(){
        x += velocidad;
    }
    public void moverArriba(){
        y -= velocidad;
    }
    public void moverAbajo(){
        y += velocidad;
    }
    //Limites

    public void limitarMovimiento(int anchoPanel, int altoPanel){
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        if (x + ancho > anchoPanel) x = anchoPanel - ancho;
        if (y + alto > altoPanel) y = altoPanel - alto;

    }
    //Modelo Taxi
    public void dibujar(Graphics g){
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, ancho, alto);

        //Detalles
        g.setColor(Color.BLACK);
        g.fillRect(x + 10, y +5, 30, 10);
    }
    //Colision
    public Rectangle getBounds(){
        return new Rectangle(x, y, ancho, alto);
    }
    //Getters
    public int getX(){return x;}
    public int getY(){return y;}

}
