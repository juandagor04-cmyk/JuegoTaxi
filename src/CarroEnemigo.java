import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;
public class CarroEnemigo {
    private int x, y;
    private int velocidad;
    private double angulo;

    private int ancho = 40;
    private int alto = 30;
    private static Random random = new Random();

    //Tipos de carros
    private int tipoCarro;
    private Color[] coloresCarro = {
            new Color(200, 50, 50),   // Rojo
            new Color(50, 100, 200),  // Azul
            new Color(50, 180, 80),   // Verde
            new Color(200, 150, 50),  // Naranja
            new Color(150, 50, 150),  // Morado
            new Color(80, 80, 90)     // Gris
    };
    //Direccion del Carro
    private int direccion;

    //Animacion
    private double rotacionLlantas = 0;
    private boolean activo = true;

    //Para la IA
    private int carrilActual;
    private int [] carrilesDisponibles;
    private boolean enInterseccion = false;
    private int tiempoEspera = 0;

    public CarroEnemigo(int x, int y, int velocidad, int direccion, int carril, int [] carriles){
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.direccion = direccion;
        this.carrilActual = carril;
        this.carrilesDisponibles = carriles;
        this.tipoCarro = random.nextInt(coloresCarro.length);

        //Establecer angulo segun direccion
        switch (direccion){
            case 0: angulo = Math.PI; break;
            case 1: angulo = 0; break;
            case 2: angulo =  Math.PI/2; break;
            case 3: angulo = -Math.PI/2; break;
        }
    }
    // Mover con IA
    public void mover(Mapa mapa, int anchoPanel, int altoPanel){
        if (!activo) return;

        //Movimeinto segun de la direccion
        switch (direccion){
            case 0: //Abajo
                y += velocidad;
                //Mantener en el carril
                x = carrilesDisponibles[carrilActual]- ancho /  2;
                break;
            case 1: //Arriba
                y -= velocidad;
                x = carrilesDisponibles [carrilActual] - ancho / 2;
                break;
            case 2: //Derecha
                x += velocidad;
                y = carrilesDisponibles [carrilActual ] - alto/2;
            case 3: //Izquierda
                x -= velocidad;
                y = carrilesDisponibles [carrilActual] - alto /2;
                break;
        }
        //Animar Llanta
        rotacionLlantas +=0.2 * (velocidad /3.0);

        //Verificar se esta en interseccion
        verificarInterseccion(mapa);

        //Si esta interseccion, posiblemente en esperar o girar
        if (enInterseccion){
            tiempoEspera ++;
            if (tiempoEspera > 30 && random.nextInt(100)< 20){
                //Probabilidad de girar en la interseccion
                if (random.nextBoolean()){
                    girarAleatorio();
                }
                tiempoEspera = 0;
            }
        }
        //Desaparecer si sale en la pantalla
        if (fueraDePatalla(anchoPanel, altoPanel)){
            activo = false;
        }
    }
    private void verificarInterseccion (Mapa mapa){
        //Detectar si esta cerca del centro de una interseccion
        int centroX = 400;
        int centroY = 300;

        int distancia = (int)Math.hypot(x-centroX, y-centroY);
        enInterseccion = (mapa.getTipoMapa()>=2 && distancia < 100);
    }
    private void girarAleatorio(){
        //Cmabiar direccion aleatorio
        int giro = random.nextInt(3);

        switch (direccion){
            case 0: //Abajo
                if (giro == 0) direccion = 2; //Derecha
                else if (giro == 1) direccion = 3; //Izquierda
                break;
            case 1: //Arriba
                if (giro == 0) direccion = 2;
                else if (giro == 1) direccion = 3;
                break;
            case 2://Derecha
                if (giro == 0) direccion = 0;
                else if (giro == 1) direccion = 1;
                break;
            case 3: //Izquierda
                if (giro == 0) direccion = 0;
                else if (giro == 1) direccion = 1;
                break;
        }
        //Actualizar angulo
        switch (direccion){
            case 0: angulo = Math.PI; break;
            case 1: angulo = 0; break;
            case 2: angulo = Math.PI / 2; break;
            case 3: angulo = -Math.PI /2; break;
        }
    }
    private boolean fueraDePatalla(int ancho, int alto){
        return (x + ancho < 0 || x > ancho || y + alto < 0 || y > alto);
    }
    //Metodo de simplficado de movimiento
    public void moverSimple (int altoPanel, int [] carriles ){
        y += velocidad;
        if (y > altoPanel + 100){
            activo = false;
        }
        //Matener en el carril
        if (carriles != null && carriles.length > carrilActual){
            x = carriles[carrilActual] - ancho /2;
        }
        rotacionLlantas+= 0.2;
    }
    public void dibujar (Graphics g){
        if (!activo) return;

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();

        int centroX = x + ancho /2;
        int centroY = y + alto  /2;
        g2d.translate(centroX, centroY);
        g2d.rotate(angulo);
        g2d.translate(-ancho/ 2,-alto/2);

        //sombra
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillRoundRect(2, 2, ancho, alto, 8, 8);

        // Cuerpo principal
        g2d.setColor(coloresCarro[tipoCarro]);
        g2d.fillRoundRect(0, 0, ancho, alto, 8, 8);

        // Techo
        g2d.setColor(coloresCarro[tipoCarro].darker());
        g2d.fillRoundRect(5, -3, ancho - 10, 8, 4, 4);

        // Ventanas
        g2d.setColor(new Color(80, 120, 180, 220));
        g2d.fillRect(6, 3, 10, 10);
        g2d.fillRect(24, 3, 10, 10);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(6, 3, 10, 10);
        g2d.drawRect(24, 3, 10, 10);

        // Parachoques
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(ancho - 6, alto / 2 - 3, 6, 6);
        g2d.fillRect(0, alto / 2 - 3, 6, 6);

        // Faros
        g2d.setColor(new Color(255, 220, 100));
        g2d.fillRect(ancho - 4, alto / 2 - 5, 4, 3);
        g2d.fillRect(ancho - 4, alto / 2 + 2, 4, 3);

        // Luces traseras
        g2d.setColor(Color.RED);
        g2d.fillRect(0, alto / 2 - 5, 3, 3);
        g2d.fillRect(0, alto / 2 + 2, 3, 3);

        // Llantas
        dibujarLlanta(g2d, 5, alto - 7, 9, 7, rotacionLlantas);
        dibujarLlanta(g2d, ancho - 14, alto - 7, 9, 7, rotacionLlantas);
        dibujarLlanta(g2d, 5, 0, 9, 7, rotacionLlantas);
        dibujarLlanta(g2d, ancho - 14, 0, 9, 7, rotacionLlantas);

        g2d.setTransform(oldTransform);
    }

    private void dibujarLlanta(Graphics2D g, int x, int y, int anchoLlanta, int altoLlanta, double rotacion) {
        AffineTransform old = g.getTransform();
        int centroX = x + anchoLlanta / 2;
        int centroY = y + altoLlanta / 2;
        g.translate(centroX, centroY);
        g.rotate(rotacion);
        g.translate(-anchoLlanta / 2, -altoLlanta / 2);

        g.setColor(Color.BLACK);
        g.fillRoundRect(0, 0, anchoLlanta, altoLlanta, 4, 4);

        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(1, 1, anchoLlanta - 2, altoLlanta - 2, 3, 3);

        g.setColor(Color.GRAY);
        for (int i = 0; i < 4; i++) {
            int radio = Math.min(anchoLlanta, altoLlanta) / 3;
            double anguloRayo = i * Math.PI / 2;
            int xCentro = anchoLlanta / 2;
            int yCentro = altoLlanta / 2;
            int x1 = xCentro + (int)(Math.cos(anguloRayo) * radio * 0.4);
            int y1 = yCentro + (int)(Math.sin(anguloRayo) * radio * 0.4);
            int x2 = xCentro + (int)(Math.cos(anguloRayo) * radio);
            int y2 = yCentro + (int)(Math.sin(anguloRayo) * radio);
            g.drawLine(x1, y1, x2, y2);
        }

        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(anchoLlanta / 2 - 2, altoLlanta / 2 - 2, 4, 4);

        g.setTransform(old);
    }

    public void colisionar() {
        // Efecto visual de colisión
        velocidad = Math.max(1, velocidad - 1);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    public boolean isActivo() { return activo; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getVelocidad() { return velocidad; }

    public void setActivo(boolean activo) { this.activo = activo; }
}

