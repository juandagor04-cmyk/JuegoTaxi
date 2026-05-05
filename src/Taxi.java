import java.awt.*;
import java.awt.geom.AffineTransform;

public class Taxi {
    private int x, y;
    private int velocidad;
    private double angulo;
    private int ancho = 48;
    private int alto = 32;
    private int puntos = 100;
    private boolean multa = false;
    private boolean precaucion = false;
    private double steerAngle = 0;
    private int framesNitrogeno = 0;
    private int framesMulta = 0;

    public Taxi(int x, int y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.angulo = Math.PI / 2;
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
    public Rectangle getBounds() { return new Rectangle(x, y, ancho, alto); }

    // ============ MOVIMIENTO ============
    public void aumentarVelocidad(int inc) {
        velocidad += inc;
        if (velocidad > 12) velocidad = 12;
        framesNitrogeno = 5;
    }
    public void moverAdelante() {
        x += Math.cos(angulo) * velocidad;
        y += Math.sin(angulo) * velocidad;
        steerAngle *= 0.95;
    }
    public void moverAtras() {
        x -= Math.cos(angulo) * (velocidad / 2);
        y -= Math.sin(angulo) * (velocidad / 2);
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
    public void mover(int dx, int dy) { moverAdelante(); } // compatibilidad
    public void limitarMovimiento(int anchoP, int altoP) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + ancho > anchoP) x = anchoP - ancho;
        if (y + alto > altoP) y = altoP - alto;
    }

    // ============ GETTERS / SETTERS ============
    public int getX() { return x; }
    public int getY() { return y; }
    public int getVelocidad() { return velocidad; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public double getAngulo() { return angulo; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVelocidad(int v) { velocidad = v; }

    // ============ DIBUJADO ============
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform old = g2d.getTransform();
        int cx = x + ancho / 2;
        int cy = y + alto / 2;
        g2d.translate(cx, cy);
        g2d.rotate(angulo);
        g2d.translate(-ancho / 2, -alto / 2);

        // === EFECTO NITRO (llamas detrás) ===
        if (framesNitrogeno > 0) {
            g2d.setColor(new Color(255, 100, 0, 180));
            int off = (velocidad > 5) ? 12 : 6;
            g2d.fillRect(-off, alto / 2 - 4, 8, 8);
            framesNitrogeno--;
        }

        // === CARROCERÍA PRINCIPAL ===
        Color colorBase = (multa && framesMulta > 0) ? Color.RED : new Color(255, 210, 0);
        g2d.setColor(colorBase);
        g2d.fillRoundRect(0, 0, ancho, alto, 12, 12);
        g2d.setColor(new Color(200, 150, 0));
        g2d.drawRoundRect(0, 0, ancho, alto, 12, 12);
        if (framesMulta > 0) framesMulta--;

        // === TECHO Y LETRERO "TAXI" ===
        g2d.setColor(new Color(255, 190, 0));
        g2d.fillRoundRect(8, -5, ancho - 16, 9, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(ancho / 2 - 14, -12, 28, 8, 4, 4);
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString("TAXI", ancho / 2 - 11, -6);
        /*
        // === VENTANAS ===
        g2d.setColor(new Color(80, 140, 200, 220));
        g2d.fillRoundRect(6, 4, 13, 12, 5, 5);
        g2d.fillRoundRect(ancho - 19, 4, 13, 12, 5, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(6, 4, 13, 12, 5, 5);
        g2d.drawRoundRect(ancho - 19, 4, 13, 12, 5, 5);
        // Reflejo
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.drawLine(8, 6, 16, 6);
        g2d.drawLine(ancho - 17, 6, ancho - 9, 6);

         */

        // === PARACHOQUES ===
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRoundRect(ancho - 12, alto / 2 - 6, 12, 12, 4, 4);
        g2d.fillRoundRect(0, alto / 2 - 6, 12, 12, 4, 4);

        // === FAROS DELANTEROS ===
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(ancho - 8, alto / 2 - 8, 6, 4, 2, 2);
        g2d.fillRoundRect(ancho - 8, alto / 2 + 4, 6, 4, 2, 2);
        if (velocidad > 0) {
            g2d.setColor(new Color(255, 255, 150, 120));
            g2d.fillOval(ancho - 5, alto / 2 - 7, 4, 3);
            g2d.fillOval(ancho - 5, alto / 2 + 5, 4, 3);
        }

        // === LUCES TRASERAS ===
        g2d.setColor(Color.RED);
        g2d.fillRoundRect(4, alto / 2 - 8 , 5, 4, 2, 2);
        g2d.fillRoundRect(4, alto / 2 + 4, 5, 4, 2, 2);

        // === DETALLES DE PUERTAS ===
        g2d.setColor(new Color(150, 110, 20));
        g2d.drawLine(ancho / 2, 4, ancho / 2, alto - 4);
        g2d.setColor(new Color(100, 70, 10));
        g2d.fillRoundRect(ancho / 2 + 4, alto / 2 - 3, 7, 3, 2, 2);
        g2d.fillRoundRect(ancho - 22, alto / 2 - 3, 7, 3, 2, 2);

        /*
        // === LOGO "TAXI" LATERAL ===
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("TAXI", ancho / 2 - 12, alto / 2 + 4);

         */
        /*
        // === FRANJA DE AJEDREZ (checkers) ===
        int checkStart = ancho - 32;
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                g2d.fillRect(checkStart + i * 3, 3, 2, 3);
                g2d.fillRect(checkStart + i * 3, alto - 6, 2, 3);
            }
        }

         */

        // === INDICADORES DE DIRECCIÓN (RUEDAS DELANTERAS) ===
        int anchoInd = 9;
        int altoInd = 4;
        int xInd = ancho - 18;   // posición delantera
        int ySup = 5;
        int yInf = alto - 9;

        dibujarIndicador(g2d, xInd, ySup, anchoInd, altoInd, steerAngle);
        dibujarIndicador(g2d, xInd, yInf, anchoInd, altoInd, steerAngle);

        g2d.setTransform(old);
    }

    private void dibujarIndicador(Graphics2D g, int x, int y, int w, int h, double anguloDir) {
        AffineTransform old = g.getTransform();
        int cx = x + w / 2;
        int cy = y + h / 2;
        g.translate(cx, cy);
        g.rotate(anguloDir);
        g.translate(-w / 2, -h / 2);
        g.setColor(Color.BLACK);
        g.fillRoundRect(0, 0, w, h, 3, 3);
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(w / 2, 1, w / 2, h - 1);
        g.setTransform(old);
    }
}