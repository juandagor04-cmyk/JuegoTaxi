import java.awt.*;
public class Taxi {
    private int x;
    private int y;
    private int velocidad;

    private int ancho = 50;
    private int alto = 30;

    private boolean multa = false;
    private boolean precaucion = false;

    //Modificaciones
    public void setMulta(boolean m){
        multa = m;
    }
    public void setPrecaucion(boolean p){
        precaucion = p;
    }
    public void reducirVelocidad(){
        velocidad = Math.max(1, velocidad -1);
    }
    public void recogerCliente(){
        System.out.println("Cliente recogido");
    }

    public Taxi(int x, int y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
    }

    //Movimiento
 public void mover (int dx, int dy){
        x += dx;
        y += dy;
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

    public int getVelocidad(){
        return velocidad;
    }
}

