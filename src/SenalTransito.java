import java.awt.*;
import java.util.Random;
public class SenalTransito {
    //Tipos Generales
    public static final int REGLAMENTARIA = 0;
    public static final int PREVENTIVA = 1;
    public static final int INFORMATIVA = 2;

    //Subtipos
    public static final int SEMAFORO = 0;
    public static final int PARE = 1;
    public static final int CURVA = 2;
    public static final int CRUCE = 3;
    public static final int CLIENTE = 4;

    //Estado del semaforo
    public static final int ROJO = 0;
    public static final int AMARILLO = 1;
    public static final int VERDE  = 2;

    private int x, y;
    private int tipo;
    private int subTipo;

    //Variables del semaforo mejorado
    private int estadoSemaforo = ROJO;
    private int tiempoCambio = 0;
    private  Random random;

    // Tiempo de duracion de cada color (ciclos)
    private int duracionRojo;
    private int duracionAmarillo;
    private int duracionVerde;

    private boolean enRojo = true;
    private int tiempo  = 0;

    public SenalTransito(int x, int y, int tipo, int subTipo){
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.subTipo = subTipo;
        this.random = new Random();

        iniciarTiemposAleatorios();
    }
    private void iniciarTiemposAleatorios(){

        duracionRojo = 100 + random.nextInt(200);
        duracionAmarillo = 40 + random.nextInt(30);
        duracionVerde = 100 + random.nextInt(200);
    }
    //Dibujar
    public void dibujar(Graphics g){

        //Reglamentaria
        if (tipo == REGLAMENTARIA){

            if (subTipo == SEMAFORO){
                //Dibujar el poste del semaforo
                g.setColor(Color.DARK_GRAY);
                g.fillRect(x+8,y+50,4,30);

                //Luz Roja
                if (estadoSemaforo == ROJO){
                    g.setColor(Color.RED);
                    g.fillOval(x+5, y+5, 10, 10);
                    //Efecto
                    g.setColor(new Color(255, 100, 100, 100));
                    g.fillOval(x+3, y+3, 14, 14);
                }else {
                    g.setColor(new Color(80, 0, 0));
                    g.fillOval(x + 5, y + 5, 10, 10);
                }
                //Luz Amarilla
                if (estadoSemaforo == AMARILLO){
                    g.setColor(Color.YELLOW);
                    g.fillOval(x+5,y+20,10,10);

                    //Efecto
                    g.setColor(new Color(255,255,100,100));
                    g.fillOval(x+3, y+18, 14,14);
                }else{
                    g.setColor(new Color(80,80,0));
                    g.fillOval(x+5, y+20, 10,10);
                }
                //Luz verde
                if (estadoSemaforo == VERDE){
                    g.setColor(Color.GREEN);
                    g.fillOval(x+5, y+35, 10, 10);
                    // Efecto de brillo
                    g.setColor(new Color(100, 255, 100, 100));
                    g.fillOval(x+3, y+33, 14, 14);
                } else {
                    g.setColor(new Color(0, 80, 0));
                    g.fillOval(x+5, y+35, 10, 10);
                }



            }
            if (subTipo == PARE){
                g.setColor(Color.RED);
                g.fillRect(x,y,30,30);
                g.setColor(Color.WHITE);
                g.drawString("PARE", x,y + 20);
            }
        }
        //Preventivas
        if (tipo == PREVENTIVA){
            g.setColor(Color.YELLOW);
            g.fillRect(x,y,30,30);
            g.setColor(Color.BLACK);

            if (subTipo == CURVA){
                g.drawString("↷", x+10, y+20);
            }
            if (subTipo == CRUCE){
                g.drawString("+", x+10,y+20);
            }
        }
        //Informativas
        if (tipo == INFORMATIVA){
            g.setColor(Color.BLUE);
            g.fillRect(x,y,30,30);
            g.setColor(Color.WHITE);
            if (subTipo == CLIENTE){
                g.drawString("C", x+10,y+20);
            }
        }
    }
    //Vida
    public void actualizar(){
        tiempo++;
        tiempoCambio++;

        if (subTipo == SEMAFORO){
            switch (estadoSemaforo){
                case ROJO:
                    if (tiempoCambio >= duracionRojo){
                        estadoSemaforo  = VERDE;
                        tiempoCambio = 0;
                        duracionVerde = 100 + random.nextInt(200);
                        enRojo = false;
                    }
                    break;
                case VERDE:
                    if (tiempo >= duracionVerde){
                        estadoSemaforo = AMARILLO;
                        tiempoCambio = 0;
                        duracionAmarillo = 40 + random.nextInt(30);
                    }
                    break;
                case AMARILLO:
                    if (tiempoCambio >= duracionAmarillo){
                        estadoSemaforo = AMARILLO;
                        tiempoCambio = 0;
                        duracionRojo = 100 + random.nextInt(200);
                        enRojo = true;
                    }
                    break;

            }
        }


    }
    //Zona
    public Rectangle getZona(){
        return new Rectangle(x-10, y-10, 60,80);
    }

    //Reglas
    public void aplicarReglas(Taxi taxi){
        if (!taxi.getBounds().intersects(getZona()))return;

        //Semaforo
        if (subTipo == SEMAFORO ){
            if (estadoSemaforo == ROJO){
                taxi.setMulta(true);
                taxi.setPuntos(-10);
                System.out.println("Semaforo en rojo, -10 puntos ");
            }else if (estadoSemaforo == AMARILLO){
                taxi.setPrecaucion(true);
                taxi.reducirVelocidad();
                System.out.println("Semaforo en amarillo, precaucion");
            } else if (estadoSemaforo == VERDE) {
                taxi.setPuntos(2);
                System.out.println("Semaforo verde tienes 2 puntos");

            }
        }
        //Pare
        if (subTipo == PARE){
            taxi.setMulta(true);
        }
        //Curva
        if (subTipo == CURVA){
            taxi.reducirVelocidad();
        }
        //Cruce
        if (subTipo == CRUCE){
            taxi.setPrecaucion(true);
        }
        //Cliente
        if (subTipo == CLIENTE){
            taxi.recogerCliente();
        }
    }
    public int getEstadoSemaforo(){
        return estadoSemaforo;
    }
    public String getEstadoTexto(){
        switch (estadoSemaforo){
            case ROJO: return "ROJO";
            case AMARILLO: return "AMARILLO";
            case VERDE: return "VERDE";
            default: return "DESCONOCIDO";
        }
    }
    public void cambiarEstado(int nuevoEstado){
        if (subTipo == SEMAFORO && nuevoEstado >= 0 && nuevoEstado <= 2){
            estadoSemaforo = nuevoEstado;
            tiempoCambio = 0;
            enRojo = (nuevoEstado == ROJO);
        }
        }
}


