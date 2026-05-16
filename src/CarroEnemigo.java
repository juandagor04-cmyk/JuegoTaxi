import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class CarroEnemigo {
    private int x, y;
    private int velocidad;
    private double angulo;

    // === DIMENSIONES ACTUALIZADAS PARA MATCHEAR CON EL TAXI ===
    private int ancho = 40;
    private int alto = 72;
    private static Random random = new Random();

    // Tipos de carros
    private int tipoCarro;
    private Color[] coloresCarro = {
            new Color(200, 50, 50),   // Rojo
            new Color(50, 100, 200),  // Azul
            new Color(50, 180, 80),   // Verde
            new Color(200, 150, 50),  // Naranja
            new Color(150, 50, 150),  // Morado
            new Color(180, 180, 190)  // Plateado/Gris claro
    };

    // Direccion del Carro
    private int direccion;

    // Animacion y estado
    private boolean activo = true;

    // Para la IA
    private int carrilActual;
    private int[] carrilesDisponibles;
    private boolean enInterseccion = false;
    private int tiempoEspera = 0;

    public CarroEnemigo(int x, int y, int velocidad, int direccion, int carril, int[] carriles) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.direccion = direccion;
        this.carrilActual = carril;
        this.carrilesDisponibles = carriles;
        this.tipoCarro = random.nextInt(coloresCarro.length);

        actualizarAnguloLogico();
    }

    private void actualizarAnguloLogico() {
        switch (direccion) {
            case 0: angulo = Math.PI; break;
            case 1: angulo = 0; break;
            case 2: angulo = Math.PI / 2; break;
            case 3: angulo = -Math.PI / 2; break;
        }
    }

    // Mover con IA
    public void mover(Mapa mapa, int anchoPanel, int altoPanel) {
        if (!activo) return;

        // Movimiento segun de la direccion
        switch (direccion) {
            case 0: // Abajo
                y += velocidad;
                x = carrilesDisponibles[carrilActual] - ancho / 2;
                break;
            case 1: // Arriba
                y -= velocidad;
                x = carrilesDisponibles[carrilActual] - ancho / 2;
                break;
            case 2: // Derecha
                x += velocidad;
                y = carrilesDisponibles[carrilActual] - alto / 2;
                break;
            case 3: // Izquierda
                x -= velocidad;
                y = carrilesDisponibles[carrilActual] - alto / 2;
                break;
        }

        // Verificar se esta en interseccion
        verificarInterseccion(mapa);

        // Si esta interseccion, posiblemente en esperar o girar
        if (enInterseccion) {
            tiempoEspera++;
            if (tiempoEspera > 30 && random.nextInt(100) < 20) {
                if (random.nextBoolean()) {
                    girarAleatorio();
                }
                tiempoEspera = 0;
            }
        }

        // Desaparecer si sale de la pantalla
        if (fueraDePantalla(anchoPanel, altoPanel)) {
            activo = false;
        }
    }

    private void verificarInterseccion(Mapa mapa) {
        int centroX = 400;
        int centroY = 300;
        int distancia = (int) Math.hypot(x - centroX, y - centroY);
        enInterseccion = (mapa.getTipoMapa() >= 2 && distancia < 100);
    }

    private void girarAleatorio() {
        int giro = random.nextInt(3);
        switch (direccion) {
            case 0: if (giro == 0) direccion = 2; else if (giro == 1) direccion = 3; break;
            case 1: if (giro == 0) direccion = 2; else if (giro == 1) direccion = 3; break;
            case 2: if (giro == 0) direccion = 0; else if (giro == 1) direccion = 1; break;
            case 3: if (giro == 0) direccion = 0; else if (giro == 1) direccion = 1; break;
        }
        actualizarAnguloLogico();
    }

    private boolean fueraDePantalla(int ancho, int alto) {
        // Le damos un poco de margen para que no desaparezcan de golpe
        return (x + this.ancho + 100 < 0 || x > ancho + 100 || y + this.alto + 100 < 0 || y > alto + 100);
    }

    public void moverSimple(int altoPanel, int[] carriles) {
        y += velocidad;
        if (y > altoPanel + 100) {
            activo = false;
        }
        if (carriles != null && carriles.length > carrilActual) {
            x = carriles[carrilActual] - ancho / 2;
        }
    }

    // ==========================================
    // NUEVO DIBUJADO (ESTILO TAXI)
    // ==========================================
    public void dibujar(Graphics g) {
        if (!activo) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform oldTransform = g2d.getTransform();

        int centroX = x + ancho / 2;
        int centroY = y + alto / 2;
        g2d.translate(centroX, centroY);

        // Ajustamos la rotación visual según la dirección
        double anguloVisual = 0;
        switch (direccion) {
            case 0: anguloVisual = Math.PI; break;       // Abajo
            case 1: anguloVisual = 0; break;             // Arriba
            case 2: anguloVisual = Math.PI / 2; break;   // Derecha
            case 3: anguloVisual = -Math.PI / 2; break;  // Izquierda
        }
        g2d.rotate(anguloVisual);
        g2d.translate(-ancho / 2, -alto / 2);

        // === 1. RUEDAS ===
        int rAncho = 10;
        int rAlto = 18;
        dibujarRueda(g2d, 0, alto - 20, rAncho, rAlto);       // Trasera Izquierda
        dibujarRueda(g2d, ancho, alto - 20, rAncho, rAlto);   // Trasera Derecha
        dibujarRueda(g2d, 0, 18, rAncho, rAlto);              // Delantera Izquierda
        dibujarRueda(g2d, ancho, 18, rAncho, rAlto);          // Delantera Derecha

        // === 2. CARROCERÍA PRINCIPAL ===
        g2d.setColor(coloresCarro[tipoCarro]);
        g2d.fillRoundRect(0, 0, ancho, alto, 16, 16);

        // Borde negro grueso
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(0, 0, ancho, alto, 16, 16);

        // === 3. VENTANAS GRISES ===
        g2d.setColor(new Color(60, 75, 85));
        g2d.setStroke(new BasicStroke(2.0f));

        // Parabrisas Delantero
        g2d.fillArc(4, 14, 32, 24, 0, 180);
        g2d.setColor(Color.BLACK);
        g2d.drawArc(4, 14, 32, 24, 0, 180);

        // Parabrisas Trasero
        g2d.setColor(new Color(60, 75, 85));
        g2d.fillArc(6, alto - 32, 28, 20, 180, 180);
        g2d.setColor(Color.BLACK);
        g2d.drawArc(6, alto - 32, 28, 20, 180, 180);

        // Ventanas Laterales
        g2d.setColor(new Color(60, 75, 85));
        g2d.fillRoundRect(2, 28, 4, 16, 2, 2);
        g2d.fillRoundRect(ancho - 6, 28, 4, 16, 2, 2);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(2, 28, 4, 16, 2, 2);
        g2d.drawRoundRect(ancho - 6, 28, 4, 16, 2, 2);

        // === 4. ZONA TRASERA (Luces y maletero) ===
        g2d.drawLine(0, alto - 12, ancho, alto - 12);

        // Luces de freno (Rojas)
        g2d.setColor(new Color(200, 0, 0));
        g2d.fillRect(4, alto - 10, 6, 4);
        g2d.fillRect(ancho - 10, alto - 10, 6, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(4, alto - 10, 6, 4);
        g2d.drawRect(ancho - 10, alto - 10, 6, 4);

        // Parachoques trasero
        g2d.fillRoundRect(8, alto - 2, 6, 4, 2, 2);
        g2d.fillRoundRect(ancho - 14, alto - 2, 6, 4, 2, 2);

        // === 5. ZONA DELANTERA (Faros) ===
        // Luces delanteras (Amarillo claro/blanco)
        g2d.setColor(new Color(255, 255, 200));
        g2d.fillRoundRect(4, 2, 8, 4, 2, 2);
        g2d.fillRoundRect(ancho - 12, 2, 8, 4, 2, 2);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(4, 2, 8, 4, 2, 2);
        g2d.drawRoundRect(ancho - 12, 2, 8, 4, 2, 2);

        g2d.setTransform(oldTransform);
    }

    // Método simplificado para dibujar las ruedas (sin radios complejos)
    private void dibujarRueda(Graphics2D g, int rx, int ry, int w, int h) {
        g.setColor(new Color(30, 30, 30));
        g.fillRoundRect(rx - w / 2, ry - h / 2, w, h, 4, 4);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(rx - w / 2, ry - h / 2, w, h, 4, 4);
    }

    public void colisionar() {
        velocidad = Math.max(1, velocidad - 1);
    }

    public Rectangle getBounds() {
        // Ajustamos la caja de colisión para que sea un poco más permisiva (5 px más pequeña)
        return new Rectangle(x + 5, y + 5, ancho - 10, alto - 10);
    }

    public boolean isActivo() { return activo; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getVelocidad() { return velocidad; }
    public void setActivo(boolean activo) { this.activo = activo; }
}