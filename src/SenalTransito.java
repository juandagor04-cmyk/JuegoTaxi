import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class SenalTransito {
    // Tipos Generales (No cambiar)
    public static final int REGLAMENTARIA = 0;
    public static final int PREVENTIVA = 1;
    public static final int INFORMATIVA = 2;

    // Subtipos (No cambiar)
    public static final int SEMAFORO = 0;
    public static final int PARE = 1;
    public static final int CURVA = 2;
    public static final int CRUCE = 3;
    public static final int CLIENTE = 4;

    // Estado del semaforo (No cambiar)
    public static final int ROJO = 0;
    public static final int AMARILLO = 1;
    public static final int VERDE = 2;

    private int x, y;
    private int tipo;
    private int subTipo;

    // Variables del semaforo (No cambiar lógica)
    private int estadoSemaforo = ROJO;
    private int tiempoCambio = 0;
    private Random random;

    private int duracionRojo;
    private int duracionAmarillo;
    private int duracionVerde;

    private boolean enRojo = true;
    private int tiempo = 0;

    public SenalTransito(int x, int y, int tipo, int subTipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.subTipo = subTipo;
        this.random = new Random();

        iniciarTiemposAleatorios();
    }

    private void iniciarTiemposAleatorios() {
        duracionRojo = 100 + random.nextInt(200);
        duracionAmarillo = 40 + random.nextInt(30);
        duracionVerde = 100 + random.nextInt(200);
    }

    // =========================================================
    // DIBUJAR (Diseños mejorados aquí)
    // =========================================================
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // Activar anti-aliasing para bordes suaves y profesionales
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // -----------------------------------------------------
        // REGLAMENTARIAS
        // -----------------------------------------------------
        if (tipo == REGLAMENTARIA) {

            if (subTipo == SEMAFORO) {
                // 1. Poste estilizado con degradado
                g2d.setPaint(new GradientPaint(x + 8, y, Color.GRAY, x + 12, y, Color.DARK_GRAY));
                g2d.fillRect(x + 8, y + 45, 4, 35);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x + 8, y + 45, 4, 35);

                // 2. Cuerpo del semáforo (Caja redondeada negra)
                g2d.setColor(new Color(30, 30, 30)); // Negro mate
                g2d.fillRoundRect(x, y, 20, 45, 10, 10);

                // Borde de la caja
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(x, y, 20, 45, 10, 10);

                // 3. Pequeñas "viseras" sobre las luces para dar profundidad
                g2d.setColor(Color.BLACK);
                g2d.fillArc(x + 2, y - 2, 16, 10, 0, 180); // Visera roja
                g2d.fillArc(x + 2, y + 13, 16, 10, 0, 180); // Visera amarilla
                g2d.fillArc(x + 2, y + 28, 16, 10, 0, 180); // Visera verde

                int diametro = 12;
                int posX = x + 4;

                // --- Luz Roja ---
                if (estadoSemaforo == ROJO) {
                    // Degradado radial para efecto de lente encendido
                    Point2D center = new Point2D.Float(posX + 6, y + 6 + 1);
                    float radius = 8;
                    float[] dist = {0.0f, 1.0f};
                    Color[] colors = {Color.WHITE, Color.RED};
                    g2d.setPaint(new RadialGradientPaint(center, radius, dist, colors));
                    g2d.fillOval(posX, y + 1, diametro, diametro);

                    // Brillo exterior (Glow)
                    g2d.setColor(new Color(255, 0, 0, 70));
                    g2d.fillOval(posX - 2, y + 1 - 2, diametro + 4, diametro + 4);
                } else {
                    g2d.setColor(new Color(60, 0, 0)); // Rojo oscuro apagado
                    g2d.fillOval(posX, y + 1, diametro, diametro);
                    // Efecto cristal apagado
                    g2d.setColor(new Color(255,255,255, 30));
                    g2d.drawArc(posX+2, y+3, 8, 8, 40, 80);
                }

                // --- Luz Amarilla ---
                if (estadoSemaforo == AMARILLO) {
                    Point2D center = new Point2D.Float(posX + 6, y + 16 + 1);
                    float radius = 8;
                    float[] dist = {0.0f, 1.0f};
                    Color[] colors = {Color.WHITE, Color.YELLOW};
                    g2d.setPaint(new RadialGradientPaint(center, radius, dist, colors));
                    g2d.fillOval(posX, y + 16, diametro, diametro);

                    // Brillo
                    g2d.setColor(new Color(255, 255, 0, 70));
                    g2d.fillOval(posX - 2, y + 16 - 2, diametro + 4, diametro + 4);
                } else {
                    g2d.setColor(new Color(60, 60, 0)); // Amarillo oscuro apagado
                    g2d.fillOval(posX, y + 16, diametro, diametro);
                    g2d.setColor(new Color(255,255,255, 30));
                    g2d.drawArc(posX+2, y+18, 8, 8, 40, 80);
                }

                // --- Luz Verde ---
                if (estadoSemaforo == VERDE) {
                    Point2D center = new Point2D.Float(posX + 6, y + 31 + 1);
                    float radius = 8;
                    float[] dist = {0.0f, 1.0f};
                    Color[] colors = {new Color(200, 255, 200), Color.GREEN};
                    g2d.setPaint(new RadialGradientPaint(center, radius, dist, colors));
                    g2d.fillOval(posX, y + 31, diametro, diametro);

                    // Brillo
                    g2d.setColor(new Color(0, 255, 0, 70));
                    g2d.fillOval(posX - 2, y + 31 - 2, diametro + 4, diametro + 4);
                } else {
                    g2d.setColor(new Color(0, 60, 0)); // Verde oscuro apagado
                    g2d.fillOval(posX, y + 31, diametro, diametro);
                    g2d.setColor(new Color(255,255,255, 30));
                    g2d.drawArc(posX+2, y+33, 8, 8, 40, 80);
                }
            }

            if (subTipo == PARE) {
                // 1. Poste metálico mejorado
                g2d.setPaint(new GradientPaint(x + 13, y, new Color(180,180,180), x + 17, y, new Color(100,100,100)));
                g2d.fillRect(x + 13, y + 25, 4, 45);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(x + 13, y + 25, 4, 45);

                // 2. Octágono con degradado sutil para volumen
                Polygon parePolygon = new Polygon();
                parePolygon.addPoint(x + 10, y + 7);
                parePolygon.addPoint(x + 20, y + 7);
                parePolygon.addPoint(x + 27, y + 14);
                parePolygon.addPoint(x + 27, y + 23);
                parePolygon.addPoint(x + 20, y + 30);
                parePolygon.addPoint(x + 10, y + 30);
                parePolygon.addPoint(x + 3, y + 23);
                parePolygon.addPoint(x + 3, y + 14);

                g2d.setPaint(new GradientPaint(x+3, y+7, new Color(255, 50, 50), x+27, y+30, new Color(150, 0, 0)));
                g2d.fillPolygon(parePolygon);

                // 3. Borde Blanco grueso y borde negro fino exterior
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2.5f)); // Borde blanco grueso
                g2d.drawPolygon(parePolygon);

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(0.5f)); // Línea negra final
                g2d.drawPolygon(parePolygon);

                // 4. Texto "PARE" centrado y mejorado
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 9));
                FontMetrics fm = g2d.getFontMetrics();
                int anchoTexto = fm.stringWidth("PARE");
                // Centrar texto en X respecto al octágono (ancho aprox 24, de x+3 a x+27)
                g2d.drawString("PARE", x + 15 - (anchoTexto/2), y + 22);
            }
        }

        // -----------------------------------------------------
        // PREVENTIVAS (Diamantes Amarillos)
        // -----------------------------------------------------
        if (tipo == PREVENTIVA) {
            int[] diamanteX = {x + 15, x + 30, x + 15, x};
            int[] diamanteY = {y, y + 15, y + 30, y + 15};
            Polygon shapeDiamante = new Polygon(diamanteX, diamanteY, 4);

            // 1. Sombra base sutil redondeada
            g2d.setColor(new Color(0, 0, 0, 40));
            g2d.fillPolygon(new int[]{x+17, x+32, x+17, x+2}, new int[]{y+2, y+17, y+32, y+17}, 4);

            // 2. Fondo amarillo con degradado
            g2d.setPaint(new GradientPaint(x, y, new Color(255, 240, 100), x + 30, y + 30, new Color(255, 200, 0)));
            g2d.fillPolygon(shapeDiamante);

            // 3. Borde negro grueso y profesional
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawPolygon(shapeDiamante);

            // Un borde interno fino para detalle
            g2d.setStroke(new BasicStroke(0.5f));
            g2d.setColor(new Color(0,0,0, 50));
            g2d.drawPolygon(new int[]{x+15, x+27, x+15, x+3}, new int[]{y+3, y+15, y+27, y+15}, 4);


            // 4. Símbolos (Negro mate)
            g2d.setColor(new Color(20, 20, 20));
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (subTipo == CURVA) {
                // Curva vectorizada más suave
                Path2D path = new Path2D.Double();
                path.moveTo(x + 10, y + 22);
                path.curveTo(x + 10, y + 15, x + 15, y + 10, x + 22, y + 10);
                g2d.draw(path);

                // Flecha estilizada
                Polygon flecha = new Polygon();
                flecha.addPoint(x + 20, y + 7);
                flecha.addPoint(x + 26, y + 10);
                flecha.addPoint(x + 20, y + 13);
                g2d.fillPolygon(flecha);
            }

            if (subTipo == CRUCE) {
                // Cruz de cruce de vías más gruesa y centrada
                g2d.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                // Línea Vertical
                g2d.drawLine(x + 15, y + 7, x + 15, y + 23);
                // Línea Horizontal
                g2d.drawLine(x + 7, y + 15, x + 23, y + 15);
            }

            // Texto inferior más limpio
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 6));
            if(subTipo == CURVA) g2d.drawString("CURVA", x+7, y+27);
            if(subTipo == CRUCE) g2d.drawString("CRUCE", x+7, y+27);
        }

        // -----------------------------------------------------
        // INFORMATIVAS (Azules)
        // -----------------------------------------------------
        if (tipo == INFORMATIVA) {
            if (subTipo == CLIENTE) {
                // 1. Sombra drop sutil
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(x + 2, y + 2, 28, 28, 8, 8);

                // 2. Fondo Azul con degradado radial (efecto cristalino sutil)
                Point2D centerPoint = new Point2D.Float(x + 10, y + 10);
                float radiusPoint = 30;
                Color[] colorsPoint = {new Color(50, 150, 255), new Color(0, 70, 180)};
                g2d.setPaint(new RadialGradientPaint(centerPoint, radiusPoint, new float[]{0f, 1f}, colorsPoint));
                g2d.fillRoundRect(x, y, 28, 28, 8, 8);

                // 3. Borde blanco interior
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(x + 2, y + 2, 24, 24, 6, 6);

                // Borde negro exterior fino
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(0.5f));
                g2d.drawRoundRect(x, y, 28, 28, 8, 8);

                // 4. Icono de Persona Pictograma estilizado
                g2d.setColor(Color.WHITE);
                // Cabeza
                g2d.fillOval(x + 11, y + 7, 6, 6);
                // Cuerpo (RoundRect para hombros suaves)
                g2d.fillRoundRect(x + 9, y + 14, 10, 8, 3, 3);
                // Cortar piernas
                g2d.setColor(g2d.getColor()); // (Técnicamente ya es blanco, pero para lógica de dibujo)
                g2d.fillRect(x+12, y+20, 4, 5);

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(x+12, y+20, 1, 6, 1, 1);
                g2d.fillRoundRect(x+15, y+20, 1, 6, 1, 1);

                // 5. Letra "C" moderna
                g2d.setColor(new Color(255, 255, 100)); // Amarillo brillante para la C
                g2d.setFont(new Font("Futura", Font.BOLD, 10));
                g2d.drawString("C", x + 20, y + 12);
            }
        }

        // Restaurar stroke por defecto
        g2d.setStroke(new BasicStroke(1));
    }

    // =========================================================
    // LÓGICA (No tocar, se mantiene igual)
    // =========================================================
    public void actualizar() {
        tiempo++;
        tiempoCambio++;

        if (subTipo == SEMAFORO) {
            switch (estadoSemaforo) {
                case ROJO:
                    if (tiempoCambio >= duracionRojo) {
                        estadoSemaforo = VERDE;
                        tiempoCambio = 0;
                        duracionVerde = 100 + random.nextInt(200);
                        enRojo = false;
                    }
                    break;
                case VERDE:
                    if (tiempoCambio >= duracionVerde) { // Corregido tiempo -> tiempoCambio para consistencia
                        estadoSemaforo = AMARILLO;
                        tiempoCambio = 0;
                        duracionAmarillo = 40 + random.nextInt(30);
                    }
                    break;
                case AMARILLO:
                    if (tiempoCambio >= duracionAmarillo) {
                        estadoSemaforo = ROJO;
                        tiempoCambio = 0;
                        duracionRojo = 100 + random.nextInt(200);
                        enRojo = true;
                    }
                    break;
            }
        }
    }

    // Zona de colisión (No tocar)
    public Rectangle getZona() {
        return new Rectangle(x - 10, y - 10, 60, 80);
    }

    // Reglas (No tocar)
    public void aplicarReglas(Taxi taxi) {
        if (!taxi.getBounds().intersects(getZona())) return;

        // Semaforo
        if (subTipo == SEMAFORO) {
            if (estadoSemaforo == ROJO) {
                taxi.setMulta(true);
                taxi.setPuntos(-10);
                System.out.println("Semaforo en rojo, -10 puntos ");
            } else if (estadoSemaforo == AMARILLO) {
                taxi.setPrecaucion(true);
                taxi.reducirVelocidad();
                System.out.println("Semaforo en amarillo, precaucion");
            } else if (estadoSemaforo == VERDE) {
                taxi.setPuntos(2);
                System.out.println("Semaforo verde tienes 2 puntos");
            }
        }
        // Pare
        if (subTipo == PARE) {
            taxi.setMulta(true);
        }
        // Curva
        if (subTipo == CURVA) {
            taxi.reducirVelocidad();
        }
        // Cruce
        if (subTipo == CRUCE) {
            taxi.setPrecaucion(true);
        }
        // Cliente
        if (subTipo == CLIENTE) {
            taxi.recogerCliente();
        }
    }

    // Getters y Setters lógicos (No tocar)
    public int getEstadoSemaforo() {
        return estadoSemaforo;
    }

    public String getEstadoTexto() {
        switch (estadoSemaforo) {
            case ROJO: return "ROJO";
            case AMARILLO: return "AMARILLO";
            case VERDE: return "VERDE";
            default: return "DESCONOCIDO";
        }
    }

    public void cambiarEstado(int nuevoEstado) {
        if (subTipo == SEMAFORO && nuevoEstado >= 0 && nuevoEstado <= 2) {
            estadoSemaforo = nuevoEstado;
            tiempoCambio = 0;
            enRojo = (nuevoEstado == ROJO);
        }
    }
}