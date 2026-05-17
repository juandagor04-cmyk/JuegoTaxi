import java.awt.*;
import java.awt.geom.AffineTransform;

public class Taxi {

    private double x, y;
    private double angulo;

    // Dimensiones
    private int ancho = 40;
    private int alto = 72;

    // === SISTEMA UNIFICADO DE CINEMÁTICA ===
    private double velocidadActual = 0.0;
    private final double VELOCIDAD_MAXIMA = 60.0;
    private final double ACELERACION = 1.5;
    private final double FRICCION = 0.8;

    private int puntos = 100;
    private boolean multa = false;
    private boolean precaucion = false;
    private double steerAngle = 0;
    private int framesNitrogeno = 0;
    private int framesMulta = 0;

    public Taxi(double x, double y, int velInicial) {
        this.x = x;
        this.y = y;
        this.velocidadActual = velInicial;
        this.angulo = Math.PI / 2;
    }

    // ============ MÉTODOS DE ACELERACIÓN Y FÍSICAS ============
    public void acelerar() {
        if (velocidadActual < VELOCIDAD_MAXIMA){
            velocidadActual += ACELERACION;
        }
    }

    public void frenar() {
        velocidadActual -= ACELERACION;
        if (velocidadActual < -20) velocidadActual = -20;
    }


    public void aplicarFriccion() {
        if (velocidadActual > 0) {
            velocidadActual -= FRICCION;
            if (velocidadActual < 0) velocidadActual = 0;
        } else if (velocidadActual < 0) {
            velocidadActual += FRICCION;
            if (velocidadActual > 0) velocidadActual = 0;
        }
    }

    // ============ MÉTODOS REQUERIDOS ============
    public void setMulta(boolean m) {
        this.multa = m;
        if (m) framesMulta = 8;
    }
    public void setPrecaucion(boolean p) { this.precaucion = p; }

    public void reducirVelocidad() {
        velocidadActual = Math.max(1, velocidadActual - 1);
    }

    public void recogerCliente() { puntos += 50; }
    public void setPuntos(int cambio) { puntos += cambio; if (puntos < 0) puntos = 0; }
    public int getPuntos() { return puntos; }

    public Rectangle getBounds() { return new Rectangle((int)x, (int)y, ancho, alto); }

    // ============ MOVIMIENTO ============
    public void aumentarVelocidad(int inc) {
        velocidadActual += inc;
        if (velocidadActual > VELOCIDAD_MAXIMA) velocidadActual = VELOCIDAD_MAXIMA;
        framesNitrogeno = 5;
    }

    //Movimiento actual
    public void moverAdelante() {
        x += Math.cos(angulo) * velocidadActual;
        y += Math.sin(angulo) * velocidadActual;
        steerAngle *= 0.95;
    }
    public void moverAtras() {
        x -= Math.cos(angulo) * (velocidadActual / 2.0);
        y -= Math.sin(angulo) * (velocidadActual / 2.0);
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
    public int getX() { return (int)x; }
    public int getY() { return (int)y; }


    public int getVelocidad() { return (int) velocidadActual; }

    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public double getAngulo() { return angulo; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setVelocidad(int v) { velocidadActual = v; }

    //Sumar los puntos
   public void sumarPuntos (int cantidad){
        this.puntos += cantidad;
   }
   //Metodo de restar los puntos
    public void restarPuntos (int cantidad){
        this.puntos -= cantidad;
        if (this.puntos < 0) this.puntos = 0;
    }
    public void actualizarPosicion(){
        moverAdelante();
    }




    // ============ DIBUJADO ============
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform old = g2d.getTransform();

        int cx = (int)x + ancho / 2;
        int cy = (int)y + alto / 2;
        g2d.translate(cx, cy);

        g2d.rotate(angulo + Math.PI / 2);
        g2d.translate(-ancho / 2, -alto / 2);

        int rAncho = 10;
        int rAlto = 18;

        dibujarIndicador(g2d, 0, alto - 20, rAncho, rAlto, 0);
        dibujarIndicador(g2d, ancho, alto - 20, rAncho, rAlto, 0);
        dibujarIndicador(g2d, 0, 18, rAncho, rAlto, steerAngle);
        dibujarIndicador(g2d, ancho, 18, rAncho, rAlto, steerAngle);

        if (framesNitrogeno > 0) {
            g2d.setColor(new Color(255, 100, 0, 180));
            g2d.fillPolygon(new int[]{ancho/2 - 8, ancho/2 + 8, ancho/2}, new int[]{alto, alto, alto + 16}, 3);
            framesNitrogeno--;
        }

        Color colorBase = (multa && framesMulta > 0) ? Color.RED : new Color(250, 215, 0);
        g2d.setColor(colorBase);
        g2d.fillRoundRect(0, 0, ancho, alto, 16, 16);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(0, 0, ancho, alto, 16, 16);
        if (framesMulta > 0) framesMulta--;

        g2d.setColor(new Color(60, 75, 85));
        g2d.setStroke(new BasicStroke(2.0f));

        g2d.fillArc(4, 14, 32, 24, 0, 180);
        g2d.setColor(Color.BLACK);
        g2d.drawArc(4, 14, 32, 24, 0, 180);

        g2d.setColor(new Color(60, 75, 85));
        g2d.fillArc(6, alto - 32, 28, 20, 180, 180);
        g2d.setColor(Color.BLACK);
        g2d.drawArc(6, alto - 32, 28, 20, 180, 180);

        g2d.setColor(new Color(60, 75, 85));
        g2d.fillRoundRect(2, 28, 4, 16, 2, 2);
        g2d.fillRoundRect(ancho - 6, 28, 4, 16, 2, 2);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(2, 28, 4, 16, 2, 2);
        g2d.drawRoundRect(ancho - 6, 28, 4, 16, 2, 2);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("TAXI", ancho/2 - 11, 11);

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(ancho/2 - 10, 32, 20, 10, 4, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(ancho/2 - 10, 32, 20, 10, 4, 4);
        g2d.setFont(new Font("Arial", Font.BOLD, 7));
        g2d.drawString("TAXI", ancho/2 - 8, 40);

        g2d.drawLine(0, alto - 12, ancho, alto - 12);

        g2d.setColor(Color.RED);
        g2d.fillRect(4, alto - 10, 6, 4);
        g2d.fillRect(ancho - 10, alto - 10, 6, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(4, alto - 10, 6, 4);
        g2d.drawRect(ancho - 10, alto - 10, 6, 4);

        g2d.fillRoundRect(8, alto - 2, 6, 4, 2, 2);
        g2d.fillRoundRect(ancho - 14, alto - 2, 6, 4, 2, 2);

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(ancho/2 - 7, alto - 10, 14, 6, 1, 1);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(ancho/2 - 7, alto - 10, 14, 6, 1, 1);
        g2d.setFont(new Font("Arial", Font.PLAIN, 4));
        g2d.drawString("TAXI", ancho/2 - 5, alto - 6);

        g2d.setTransform(old);
    }

    private void dibujarIndicador(Graphics2D g, int x, int y, int w, int h, double anguloDir) {
        AffineTransform old = g.getTransform();
        g.translate(x, y);
        g.rotate(anguloDir);
        g.setColor(new Color(30, 30, 30));
        g.fillRoundRect(-w / 2, -h / 2, w, h, 4, 4);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(-w / 2, -h / 2, w, h, 4, 4);
        g.setTransform(old);
    }
}