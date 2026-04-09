import java.awt.*;
public class Cliente {
    private int x, y;
    private int tamaño = 20;

    private boolean recogido;

    public Cliente(int x, int y) {
        this.x = x;
        this.y = y;
        this.recogido = false;
    }

    //Dibujo
    public void dibujar(Graphics g) {
        if (!recogido) {
            g.setColor(Color.GREEN);
            g.fillOval(x, y, tamaño, tamaño);
        }
    }

    //Colision
    public Rectangle getBounds() {
        return new Rectangle(x, y, tamaño, tamaño);
    }

    //Recoger
    public void recoger() {
        recogido = true;
    }

    public boolean fueRecogido() {
        return recogido;
    }

    //Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }





}

