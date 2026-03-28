import java.awt.*;
import java.util.Random;
public class CarroEnemigo {
    private int x, y;
    private int velocidad;

    private int ancho = 40;
    private int alto = 30;
    private static Random random = new Random();
    public CarroEnemigo(int c, int y, int velocidad){
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;

    }
    //Movimiento
    public void mover (int altoPanel, int []carriles ){
        y += velocidad;

        //Carril aleatorio
        if (y > altoPanel){
            y = -50;
            x = carriles[random.nextInt(carriles.length)];
        }
    }
    //Dibujo
    public void dibujar(Graphics g){
        g.setColor(Color.RED);
        g.fillRect(x, y, ancho, alto);

        //Detalle
        g.setColor(Color.BLACK);
        g.fillRect (x+8, y+5, 25,10);
    }
    //Colision
    public Rectangle getBounds(){
        return new Rectangle(x,y,ancho,alto);
    }
    //Getters
    public int getX (){return x;}
    public int getY (){return y;}
}
