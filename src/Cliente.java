import java.awt.*;
import java.util.Random;

public class Cliente {
    private int x, y;
    private int tamaño = 25;
    private boolean recogido;
    private boolean llamado = true;
    private int animacioLlamada = 0;
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

    }
}