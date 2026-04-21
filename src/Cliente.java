import java.awt.*;
import java.util.Random;

public class Cliente {
    private int x, y;
    private int tamaño = 25;
    private boolean recogido;
    private boolean llamando = true;
    private int animacionLlamada = 0;
    private Random random;

    //Tipos de clientes
    private int tipoCliente;
    private Color[] coloresRopa = {
            new Color(70, 130, 200),  // Azul
            new Color(200, 70, 70),   // Rojo
            new Color(70, 200, 70),   // Verde
            new Color(200, 150, 70),  // Naranja
            new Color(150, 70, 200)   // Morado


    };
    public Cliente (int x, int y){
        this.x = x;
        this.y = y;
        this.recogido = false;
        this.random = new Random();
        this.tipoCliente = random.nextInt(coloresRopa.length);
    }
    //Constructor con tipo especifico
    public Cliente(int x, int y, int tipo){
        this.x = x;
        this.y = y;
        this.recogido = false;
        this.random = new Random();
        this.tipoCliente = tipo;

    }
    //Dibujo mejorado con cabeza y hombros
    public void dibujar(Graphics g){
        if (recogido) return;

        Graphics2D g2d = (Graphics2D) g;

        //Animacionde llamado
        if (llamando) {
            animacionLlamada++;
            if (animacionLlamada > 60) animacionLlamada = 0;
        }
        //Sombra del cliente
        g.setColor(new Color(0,0,0,50));
        g.fillOval(x+5, y+25, 20, 0);

        //Cuerpo(camisa)
        g.setColor(coloresRopa[tipoCliente]);
        g.fillRect(x+8, y+15, 14, 18);

        //Pantalon
        g.setColor(new Color (60, 60, 80));
        g.fillRect(x + 8, y + 28, 6, 10);
        g.fillRect(x + 16, y + 28, 6, 10);

        // Hombros (más anchos)
        g.setColor(coloresRopa[tipoCliente]);
        g.fillRect(x + 5, y + 14, 20, 6);

        // Cuello
        g.setColor(new Color(255, 220, 180));
        g.fillRect(x + 13, y + 12, 4, 5);

        // Cabeza
        g.setColor(new Color(255, 220, 180));
        g.fillOval(x + 8, y + 2, 14, 14);

        // Cabello
        g.setColor(new Color(80, 60, 40));
        g.fillArc(x + 7, y, 16, 10, 0, 180);

        // Ojos
        g.setColor(Color.BLACK);
        g.fillOval(x + 11, y + 7, 2, 2);
        g.fillOval(x + 17, y + 7, 2, 2);

        // Sonrisa
        g.setColor(Color.BLACK);
        g.drawArc(x + 12, y + 10, 6, 4, 0, -180);

        // Brazos (con animación de llamada)
        g.setColor(coloresRopa[tipoCliente]);

        // Brazo izquierdo (quieto)
        g.fillRect(x + 3, y + 17, 5, 8);

        // Brazo derecho (animado - llamando al taxi)
        if (llamando){
            double anguloBrazo = Math.sin(animacionLlamada * 0.2)*20;
            int offsetX = (int)(Math.cos(Math.toRadians(anguloBrazo))*4);
            int offsetY = (int)(Math.sin(Math.toRadians(anguloBrazo))*3);
            g.fillRect(x+22+offsetX, y + 15 + offsetY, 6,8);

            //Mano
            g.setColor(new Color(255, 220, 180));
            g.fillOval(x + 27 + offsetX, y + 14 + offsetY, 5, 5);
        }else {
            g.fillRect(x + 22, y + 17, 5, 8);
            g.setColor(new Color(255, 220, 180));
            g.fillOval(x + 26, y + 16, 5, 5);
        }
        //Burbuja de texto "¡Taxi!"
        if (llamando && !recogido) {
            // Burbuja
            g.setColor(Color.WHITE);
            g.fillRoundRect(x - 30, y - 25, 50, 22, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRoundRect(x - 30, y - 25, 50, 22, 10, 10);

            // Texto "¡TAXI!"
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.setColor(new Color(255, 100, 0));
            g.drawString("¡TAXI!", x - 25, y - 10);

            // Triángulo de la burbuja
            int[] triX = {x - 5, x, x + 5};
            int[] triY = {y - 5, y - 2, y - 5};
            g.fillPolygon(triX, triY, 3);
        }
        //Maletin o bolso (Detalle)
        g.setColor(new Color(139, 69, 19));
        g.fillRect(x+1, y+20, 6, 8);
        g.setColor(Color.BLACK);
        g.drawLine(x+4, y+20, x+4, y+18);
    }
    //Colision
    public Rectangle getBounds(){
        return new Rectangle(x,y,tamaño+10, tamaño + 5);
    }
    //Recoger cliente
    public void recoger (){
        recogido =  true;
        llamando = false;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isLlamando() {
        return llamando;
    }

    public void setLlamando(boolean llamando) {
        this.llamando = llamando;
    }
    public boolean fueRecogido() {
        return recogido;
    }


}
