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

    // === DIBUJO MEJORADO PARA VISTA SUPERIOR (TOP-DOWN) ===
    public void dibujar(Graphics g){
        if (recogido) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Animación de llamado
        if (llamando) {
            animacionLlamada++;
            if (animacionLlamada > 60) animacionLlamada = 0;
        }

        // 1. Sombra general (Círculo difuminado debajo)
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillOval(x + 2, y + 2, 24, 24);

        // 2. Hombros / Torso (Visto desde arriba es un rectángulo redondeado u óvalo)
        g2d.setColor(coloresRopa[tipoCliente]);
        g2d.fillRoundRect(x + 4, y + 6, 18, 14, 8, 8);

        // 3. Brazos
        // Brazo izquierdo (quieto junto al cuerpo)
        g2d.fillRoundRect(x + 4, y + 4, 6, 16, 4, 4);

        // Brazo derecho (animado - extendiéndose para llamar al taxi)
        if (llamando) {
            // El brazo se mueve hacia adelante y hacia atrás
            int offsetBrazo = (int) (Math.sin(animacionLlamada * 0.2) * 4);

            // Manga extendida
            g2d.setColor(coloresRopa[tipoCliente]);
            g2d.fillRoundRect(x + 16 + offsetBrazo, y + 10, 12, 6, 3, 3);

            // Mano
            g2d.setColor(new Color(255, 220, 180));
            g2d.fillOval(x + 25 + offsetBrazo, y + 10, 6, 6);
        } else {
            // Brazo derecho quieto
            g2d.setColor(coloresRopa[tipoCliente]);
            g2d.fillRoundRect(x + 14, y + 16, 6, 6, 3, 3);
        }

        // 4. Cabeza (Un círculo visto desde arriba)
        // Pelo
        g2d.setColor(new Color(60, 40, 20)); // Castaño oscuro
        g2d.fillOval(x + 6, y + 5, 14, 14);

        // Piel (Cara apuntando ligeramente hacia la calle / derecha)
        g2d.setColor(new Color(255, 220, 180));
        g2d.fillArc(x + 6, y + 5, 14, 14, -70, 140);

        // 5. Maletín o bolso (Visto desde arriba, apoyado en el suelo)
        g2d.setColor(new Color(100, 50, 10));
        g2d.fillRoundRect(x, y + 18, 6, 8, 2, 2);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y + 18, 6, 8, 2, 2);

        // 6. Burbuja de texto "¡Taxi!" (La dejamos como interfaz 2D, se ve perfecta)
        if (llamando && !recogido) {
            // Burbuja
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(x - 30, y - 25, 50, 22, 10, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(x - 30, y - 25, 50, 22, 10, 10);

            // Texto "¡TAXI!"
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.setColor(new Color(255, 100, 0));
            g2d.drawString("¡TAXI!", x - 25, y - 10);

            // Triángulo de la burbuja
            int[] triX = {x - 5, x, x + 5};
            int[] triY = {y - 5, y - 2, y - 5};
            g2d.fillPolygon(triX, triY, 3);
        }
    }

    //Colision
    public Rectangle getBounds(){
        return new Rectangle(x, y, tamaño + 10, tamaño + 5);
    }

    //Recoger cliente
    public void recoger (){
        recogido = true;
        llamando = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isLlamando() { return llamando; }
    public void setLlamando(boolean llamando) { this.llamando = llamando; }
    public boolean fueRecogido() { return recogido; }
}