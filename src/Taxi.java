import java.awt.*;
import java.awt.geom.AffineTransform;

public class Taxi {
    private int x, y;
    private int velocidad;
    private double angulo;

    private int ancho = 40;
    private int alto = 25;

    private int puntos = 100;
    private boolean multa = false;
    private boolean precaucion = false;

    // Animación de llantas
    private double rotacionLlantas = 0;
    private boolean moviendoLlantas = false;
    private double direccionLlantas = 1;

    // Efectos visuales
    private int framesChoque = 0;
    private int framesNitrogeno = 0;

    public Taxi(int x, int y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.angulo = Math.PI / 2;
    }

    // ============ MÉTODOS REQUERIDOS POR LAS SEÑALES ============

    public void setMulta(boolean m) {
        this.multa = m;
        if (m) framesChoque = 10;
    }

    public void setPrecaucion(boolean p) {
        this.precaucion = p;
    }

    public void reducirVelocidad() {
        velocidad = Math.max(1, velocidad - 1);
    }

    public void recogerCliente() {
        System.out.println("Cliente recogido");
        puntos += 50;
    }

    public void setPuntos(int cambio) {
        this.puntos += cambio;
        if (this.puntos < 0) this.puntos = 0;
    }

    public int getPuntos() {
        return puntos;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    // ============ MÉTODOS DE MOVIMIENTO ============

    public void aumentarVelocidad(int incremento) {
        velocidad += incremento;
        if (velocidad > 12) velocidad = 12;
        framesNitrogeno = 5;
    }

    public void moverAdelante() {
        double dx = Math.cos(angulo) * velocidad;
        double dy = Math.sin(angulo) * velocidad;
        x += dx;
        y += dy;

        if (velocidad > 0) {
            rotacionLlantas += 0.3 * (velocidad / 3.0);
            moviendoLlantas = true;
        }
    }

    public void moverAtras() {
        double dx = -Math.cos(angulo) * (velocidad / 2);
        double dy = -Math.sin(angulo) * (velocidad / 2);
        x += dx;
        y += dy;
        rotacionLlantas -= 0.2;
        moviendoLlantas = true;
    }

    public void girarIzquierda() {
        angulo -= Math.toRadians(5);
        direccionLlantas = -0.5;
    }

    public void girarDerecha() {
        angulo += Math.toRadians(5);
        direccionLlantas = 0.5;
    }

    // Método de movimiento simplificado (para compatibilidad)
    public void mover(int dx, int dy) {
        if (dx > 0) {
            if (Math.abs(Math.toDegrees(angulo) % 360 - 90) < 10) {
                angulo = Math.toRadians(90);
            } else {
                angulo = angulo * 0.95 + Math.toRadians(90) * 0.05;
            }
        } else if (dx < 0) {
            if (Math.abs(Math.toDegrees(angulo) % 360 - 270) < 10) {
                angulo = Math.toRadians(270);
            } else {
                angulo = angulo * 0.95 + Math.toRadians(270) * 0.05;
            }
        } else if (dy > 0) {
            if (Math.abs(Math.toDegrees(angulo) % 360 - 180) < 10) {
                angulo = Math.toRadians(180);
            } else {
                angulo = angulo * 0.95 + Math.toRadians(180) * 0.05;
            }
        } else if (dy < 0) {
            if (Math.abs(Math.toDegrees(angulo) % 360 - 0) < 10 ||
                    Math.abs(Math.toDegrees(angulo) % 360 - 360) < 10) {
                angulo = Math.toRadians(0);
            } else {
                angulo = angulo * 0.95;
            }
        }

        moverAdelante();
    }

    public void limitarMovimiento(int anchoPanel, int altoPanel) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + ancho > anchoPanel) x = anchoPanel - ancho;
        if (y + alto > altoPanel) y = altoPanel - alto;
    }

    // ============ GETTERS Y SETTERS ============

    public int getX() { return x; }
    public int getY() { return y; }
    public int getVelocidad() { return velocidad; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public double getAngulo() { return angulo; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }

    // ============ DIBUJADO ============

    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();

        int centroX = x + ancho / 2;
        int centroY = y + alto / 2;
        g2d.translate(centroX, centroY);
        g2d.rotate(angulo);
        g2d.translate(-ancho / 2, -alto / 2);

        // Efecto de choque
        if (framesChoque > 0) {
            int temblorX = (int)(Math.random() * 6) - 3;
            int temblorY = (int)(Math.random() * 6) - 3;
            g2d.translate(temblorX, temblorY);
            framesChoque--;
        }

        // Efecto nitro
        if (framesNitrogeno > 0) {
            g2d.setColor(new Color(255, 100, 0, 100));
            for (int i = 1; i <= 3; i++) {
                g2d.fillRect(-i * 5, alto / 2 - 5, 5, 10);
            }
            framesNitrogeno--;
        }

        // Sombra
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRoundRect(2, 2, ancho, alto, 8, 8);

        // Cuerpo principal
        if (multa && framesChoque % 2 == 0) {
            g2d.setColor(Color.RED);
        } else {
            g2d.setColor(new Color(255, 220, 0));
        }
        g2d.fillRoundRect(0, 0, ancho, alto, 8, 8);

        // Techo
        g2d.setColor(new Color(255, 200, 0));
        g2d.fillRoundRect(5, -5, ancho - 10, 10, 5, 5);

        // Ventanas
        g2d.setColor(new Color(100, 150, 200, 200));
        g2d.fillRect(8, 3, 10, 12);
        g2d.fillRect(22, 3, 10, 12);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(8, 3, 10, 12);
        g2d.drawRect(22, 3, 10, 12);

        // Parachoques
        g2d.setColor(Color.GRAY);
        g2d.fillRect(ancho - 8, alto / 2 - 4, 8, 8);
        g2d.fillRect(0, alto / 2 - 4, 8, 8);

        // Faros
        g2d.setColor(new Color(255, 255, 150));
        g2d.fillRect(ancho - 5, alto / 2 - 6, 5, 4);
        g2d.fillRect(ancho - 5, alto / 2 + 2, 5, 4);

        // Luces traseras
        g2d.setColor(Color.RED);
        g2d.fillRect(0, alto / 2 - 6, 4, 4);
        g2d.fillRect(0, alto / 2 + 2, 4, 4);

        // Llantas
        dibujarLlanta(g2d, 6, alto - 8, 10, 8, rotacionLlantas);
        dibujarLlanta(g2d, ancho - 16, alto - 8, 10, 8, rotacionLlantas);
        dibujarLlanta(g2d, 6, 0, 10, 8, rotacionLlantas);
        dibujarLlanta(g2d, ancho - 16, 0, 10, 8, rotacionLlantas);

        // Puerta
        g2d.setColor(new Color(180, 150, 0));
        g2d.drawLine(ancho / 2, 3, ancho / 2, alto - 3);

        // Manija
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(ancho / 2 + 2, alto / 2 - 2, 6, 3);

        // Logo TAXI
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString("TAXI", ancho / 2 - 6, alto / 2 + 3);

        g2d.setTransform(oldTransform);

        if (!moviendoLlantas) {
            rotacionLlantas *= 0.95;
        }
        moviendoLlantas = false;
        direccionLlantas *= 0.9;
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
        g.fillRoundRect(2, 1, anchoLlanta - 4, altoLlanta - 2, 3, 3);

        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 4; i++) {
            int radio = Math.min(anchoLlanta, altoLlanta) / 3;
            double anguloRayo = i * Math.PI / 2;
            int xCentro = anchoLlanta / 2;
            int yCentro = altoLlanta / 2;
            int x1 = xCentro + (int)(Math.cos(anguloRayo) * radio * 0.5);
            int y1 = yCentro + (int)(Math.sin(anguloRayo) * radio * 0.5);
            int x2 = xCentro + (int)(Math.cos(anguloRayo) * radio);
            int y2 = yCentro + (int)(Math.sin(anguloRayo) * radio);
            g.drawLine(x1, y1, x2, y2);
        }

        g.setColor(Color.GRAY);
        g.fillOval(anchoLlanta / 2 - 2, altoLlanta / 2 - 2, 4, 4);
        g.setTransform(old);
    }
}