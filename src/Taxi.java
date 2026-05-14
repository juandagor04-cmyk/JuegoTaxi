import java.awt.*;
import java.awt.geom.AffineTransform;

public class Taxi {
    // === SOLUCIÓN: Cambiar 'int' a 'double' para precisión de movimiento ===
    private double x, y; // Antes eran int
    private int velocidad;
    private double angulo;

    // Dimensiones actualizadas para que el diseño sea vertical como en la imagen
    private int ancho = 40;
    private int alto = 72;

    private int puntos = 100;
    private boolean multa = false;
    private boolean precaucion = false;
    private double steerAngle = 0;
    private int framesNitrogeno = 0;
    private int framesMulta = 0;

    // Constructor actualizado para aceptar doubles (aunque puedes pasarle ints)
    public Taxi(double x, double y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.angulo = Math.PI / 2; // Inicia apuntando hacia abajo
    }

    // ============ MÉTODOS REQUERIDOS ============
    public void setMulta(boolean m) {
        this.multa = m;
        if (m) framesMulta = 8;
    }
    public void setPrecaucion(boolean p) { this.precaucion = p; }
    public void reducirVelocidad() { velocidad = Math.max(1, velocidad - 1); }
    public void recogerCliente() { puntos += 50; }
    public void setPuntos(int cambio) { puntos += cambio; if (puntos < 0) puntos = 0; }
    public int getPuntos() { return puntos; }

    //getBounds necesita ints, así que casteamos
    public Rectangle getBounds() { return new Rectangle((int)x, (int)y, ancho, alto); }

    // ============ MOVIMIENTO ============
    public void aumentarVelocidad(int inc) {
        velocidad += inc;
        if (velocidad > 12) velocidad = 12;
        framesNitrogeno = 5;
    }

    // === AHORA EL MOVIMIENTO ES SUAVE ===
    public void moverAdelante() {
        // Al ser x,y doubles, ya no se pierden los decimales en el cálculo
        x += Math.cos(angulo) * velocidad;
        y += Math.sin(angulo) * velocidad;
        steerAngle *= 0.95;
    }
    public void moverAtras() {
        // Corrección aquí: velocidad/2 en ints da 0 si velocidad es 1.
        // Usamos 2.0 para forzar cálculo double.
        x -= Math.cos(angulo) * (velocidad / 2.0);
        y -= Math.sin(angulo) * (velocidad / 2.0);
        steerAngle *= 0.95;
    }
    public void girarIzquierda() {
        angulo -= Math.toRadians(5);
        steerAngle = Math.max(-Math.toRadians(35), steerAngle - Math.toRadians(4));
    }
    public void girarDerecha() {
        angulo += Math.toRadians(5);
        steerAngle = Math.min(Math.toRadians(35), steerAngle + Math.toRadians(4));
    }
    public void mover(int dx, int dy) { moverAdelante(); }

    public void limitarMovimiento(int anchoP, int altoP) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + ancho > anchoP) x = anchoP - ancho;
        if (y + alto > altoP) y = altoP - alto;
    }

    // ============ GETTERS / SETTERS ============
    // Mantenemos los getters devolviendo int para compatibilidad con el resto del juego
    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public int getVelocidad() { return velocidad; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public double getAngulo() { return angulo; }
    public void setX(double x) { this.x = x; } // Acepta double
    public void setY(double y) { this.y = y; } // Acepta double
    public void setVelocidad(int v) { velocidad = v; }

    // ============ DIBUJADO ============
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // Mejorar la calidad de renderizado
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform old = g2d.getTransform();

        // Casteamos x e y a int solo para calcular el centro de rotación visual
        int cx = (int)x + ancho / 2;
        int cy = (int)y + alto / 2;
        g2d.translate(cx, cy);

        // El truco está aquí: Dibujamos el auto hacia ARRIBA (verticalmente).
        // Sumamos 90 grados (PI/2) a la rotación matemática para que coincida con el movimiento.
        g2d.rotate(angulo + Math.PI / 2);
        g2d.translate(-ancho / 2, -alto / 2);

        // === 1. RUEDAS (Dibujadas bajo el chasis) ===
        int rAncho = 10;
        int rAlto = 18;
        // Ruedas traseras (Fijas)
        dibujarIndicador(g2d, 0, alto - 20, rAncho, rAlto, 0);       // Izquierda
        dibujarIndicador(g2d, ancho, alto - 20, rAncho, rAlto, 0);   // Derecha
        // Ruedas delanteras (Direccionales, usan steerAngle)
        dibujarIndicador(g2d, 0, 18, rAncho, rAlto, steerAngle);     // Izquierda
        dibujarIndicador(g2d, ancho, 18, rAncho, rAlto, steerAngle); // Derecha

        // === 2. EFECTO NITRO ===
        if (framesNitrogeno > 0) {
            g2d.setColor(new Color(255, 100, 0, 180));
            // Dibuja una llama en la parte trasera
            g2d.fillPolygon(new int[]{ancho/2 - 8, ancho/2 + 8, ancho/2}, new int[]{alto, alto, alto + 16}, 3);
            framesNitrogeno--;
        }

        // === 3. CARROCERÍA PRINCIPAL ===
        Color colorBase = (multa && framesMulta > 0) ? Color.RED : new Color(250, 215, 0); // Amarillo Taxi
        g2d.setColor(colorBase);
        g2d.fillRoundRect(0, 0, ancho, alto, 16, 16);

        // Borde negro grueso
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(0, 0, ancho, alto, 16, 16);
        if (framesMulta > 0) framesMulta--;

        // === 4. VENTANAS GRISES ===
        g2d.setColor(new Color(60, 75, 85));
        g2d.setStroke(new BasicStroke(2.0f));

        // Parabrisas Delantero (Arco apuntando arriba)
        g2d.fillArc(4, 14, 32, 24, 0, 180);
        g2d.setColor(Color.BLACK);
        g2d.drawArc(4, 14, 32, 24, 0, 180);

        // Parabrisas Trasero (Arco apuntando abajo)
        g2d.setColor(new Color(60, 75, 85));
        g2d.fillArc(6, alto - 32, 28, 20, 180, 180);
        g2d.setColor(Color.BLACK);
        g2d.drawArc(6, alto - 32, 28, 20, 180, 180);

        // Ventanas Laterales
        g2d.setColor(new Color(60, 75, 85));
        g2d.fillRoundRect(2, 28, 4, 16, 2, 2);  // Izquierda
        g2d.fillRoundRect(ancho - 6, 28, 4, 16, 2, 2); // Derecha
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(2, 28, 4, 16, 2, 2);
        g2d.drawRoundRect(ancho - 6, 28, 4, 16, 2, 2);

        // === 5. TEXTO DEL CAPÓ ===
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("TAXI", ancho/2 - 11, 11);

        // === 6. LETRERO DEL TECHO ===
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(ancho/2 - 10, 32, 20, 10, 4, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(ancho/2 - 10, 32, 20, 10, 4, 4);
        g2d.setFont(new Font("Arial", Font.BOLD, 7));
        g2d.drawString("TAXI", ancho/2 - 8, 40);

        // === 7. ZONA TRASERA (Luces, matrícula y parachoques) ===
        // Línea divisoria del maletero
        g2d.drawLine(0, alto - 12, ancho, alto - 12);

        // Luces Rojas
        g2d.setColor(Color.RED);
        g2d.fillRect(4, alto - 10, 6, 4);
        g2d.fillRect(ancho - 10, alto - 10, 6, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(4, alto - 10, 6, 4);
        g2d.drawRect(ancho - 10, alto - 10, 6, 4);

        // Detalles del parachoques inferior
        g2d.fillRoundRect(8, alto - 2, 6, 4, 2, 2);
        g2d.fillRoundRect(ancho - 14, alto - 2, 6, 4, 2, 2);

        // Matrícula
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(ancho/2 - 7, alto - 10, 14, 6, 1, 1);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(ancho/2 - 7, alto - 10, 14, 6, 1, 1);
        g2d.setFont(new Font("Arial", Font.PLAIN, 4));
        g2d.drawString("TAXI", ancho/2 - 5, alto - 6);

        // Restaurar la transformación original
        g2d.setTransform(old);
    }

    // Dibuja las ruedas con capacidad de rotación
    private void dibujarIndicador(Graphics2D g, int x, int y, int w, int h, double anguloDir) {
        AffineTransform old = g.getTransform();
        g.translate(x, y);
        g.rotate(anguloDir);

        g.setColor(new Color(30, 30, 30)); // Gris muy oscuro
        g.fillRoundRect(-w / 2, -h / 2, w, h, 4, 4);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(-w / 2, -h / 2, w, h, 4, 4);

        g.setTransform(old);
    }
}