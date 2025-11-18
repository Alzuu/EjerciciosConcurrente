package EjerciciosAdicionales;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class SalonDeEjercicios {
    public static void main(String[] args) {
        Salon salon = new Salon();
        Thread coordinador = new Thread(new Coordinador(salon));
        coordinador.start();
        Thread[] personas = new Thread[12];
        for (int i = 0; i < personas.length; i++) {
            personas[i] = new Thread(new Persona(i, salon));
            personas[i].start();
        }
    }

    public static class Salon {
        private final int cantActividades = 3;
        private final int cupoActividad = 4;
        private final int cupoTotal = cantActividades * cupoActividad;

        private int cantEnAct1 = 0;
        private int cantEnAct2 = 0;
        private int cantEnAct3 = 0;

        private Semaphore mutex = new Semaphore(1);
        private Semaphore espacioSalon = new Semaphore(cupoTotal);
        private Semaphore actividad1 = new Semaphore(cupoActividad);
        private Semaphore actividad2 = new Semaphore(cupoActividad);
        private Semaphore actividad3 = new Semaphore(cupoActividad);

        private Semaphore conActividad = new Semaphore(0);
        private Semaphore actividadTerminada = new Semaphore(0);
        private Semaphore iniciarActividad = new Semaphore(0);

        public int elegirActividad(int id, int actP) throws InterruptedException {
            int actividad = 0;
            System.out.println("PERSONA " + id + ": ESPERA PARA ENTRAR.");
            espacioSalon.acquire();
            // Entro en el salón.
            switch (actP) {
                case 1:
                    mutex.acquire();
                    if (cantEnAct1 < cupoActividad) {
                        cantEnAct1++;
                        actividad = 1;
                        System.out.println("PERSONA " + id + ": entra en ACTIVIDAD 1.");
                        actividad1.acquire();
                        mutex.release();
                    } else {
                        mutex.release();
                        actividad = elegirOtraActividad(id, 1);
                    }
                    break;

                case 2:
                    mutex.acquire();
                    if (cantEnAct2 < cupoActividad) {
                        cantEnAct2++;
                        actividad = 2;
                        System.out.println("PERSONA " + id + ": entra en ACTIVIDAD 2");
                        actividad2.acquire();
                        mutex.release();
                    } else {
                        mutex.release();
                        actividad = elegirOtraActividad(id, 2);
                    }
                    break;

                default:
                    mutex.acquire();
                    if (cantEnAct3 < cupoActividad) {
                        cantEnAct3++;
                        actividad = 3;
                        System.out.println("PERSONA " + id + ": entra en ACTIVIDAD 3.");
                        actividad3.acquire();
                        mutex.release();
                    } else {
                        mutex.release();
                        actividad = elegirOtraActividad(id, 3);
                    }
                    break;
            }

            return actividad;
        }

        public int elegirOtraActividad(int id, int actNoPuede) throws InterruptedException {
            // Si la actividad que quería está llena, le doy la que menos tiene.
            int actividad = actNoPuede;
            mutex.acquire();
            if (actNoPuede == 1) {
                if (cantEnAct2 <= cantEnAct3 && cantEnAct2 < cupoActividad) {
                    cantEnAct2++;
                    mutex.release();
                    actividad = 2;
                    actividad2.acquire();
                } else if (cantEnAct3 < cupoActividad) {
                    cantEnAct3++;
                    mutex.release();
                    actividad = 3;
                    actividad3.acquire();
                } else {
                    // Si los otros están llenos, va a tener que repetir si o si.
                    cantEnAct1++;
                    mutex.release();
                    actividad = 1;
                    actividad1.acquire();
                }
            } else if (actNoPuede == 2) {
                if (cantEnAct1 <= cantEnAct3 && cantEnAct1 < cupoActividad) {
                    cantEnAct1++;
                    mutex.release();
                    actividad = 1;
                    actividad1.acquire();
                } else if (cantEnAct3 < cupoActividad) {
                    cantEnAct3++;
                    mutex.release();
                    actividad = 3;
                    actividad3.acquire();
                } else {
                    // Si los otros están llenos, va a tener que repetir si o si.
                    cantEnAct2++;
                    mutex.release();
                    actividad = 2;
                    actividad2.acquire();
                }
            } else {
                if (cantEnAct1 <= cantEnAct2 && cantEnAct1 < cupoActividad) {
                    cantEnAct1++;
                    mutex.release();
                    actividad = 1;
                    actividad1.acquire();
                } else if (cantEnAct2 < cupoActividad) {
                    cantEnAct2++;
                    mutex.release();
                    actividad = 2;
                    actividad2.acquire();
                } else {
                    // Si los otros están llenos, va a tener que repetir si o si.
                    cantEnAct3++;
                    mutex.release();
                    actividad = 3;
                    actividad3.acquire();
                }
            }
            System.out.println("PERSONA " + id + ": NO entra en ACTIVIDAD " + actNoPuede + ", elige " + actividad);
            return actividad;
        }

        public void esperarIniciar() throws InterruptedException {
            conActividad.release();
            // Avisan y esperan para entrenar todas juntas.
            iniciarActividad.acquire();
        }

        public void salirDeActividad(int nroActividad) throws InterruptedException {
            actividadTerminada.release();
            mutex.acquire();
            switch (nroActividad) {
                case 1:
                    cantEnAct1--;
                    mutex.release();
                    break;
                case 2:
                    cantEnAct2--;
                    mutex.release();
                    break;
                default:
                    cantEnAct3--;
                    mutex.release();
                    break;
            }
        }

        public void coordinarInicio() throws InterruptedException {
            conActividad.acquire(cupoTotal);
            System.out.println("INICIA EL TURNO...");
            // Cuando todos están llenos libera para que empiecen a entrenar.
            iniciarActividad.release(cupoTotal);
        }

        public void coordinarFin() throws InterruptedException {
            actividadTerminada.acquire(cupoTotal);
            // Cuando todas terminen libera los cupos de actividades.
            actividad1.release(cupoActividad);
            actividad2.release(cupoActividad);
            actividad3.release(cupoActividad);
        }
    }

    public static class Coordinador implements Runnable {
        private Salon salon;

        public Coordinador(Salon salon) {
            this.salon = salon;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    salon.coordinarInicio();
                    salon.coordinarFin();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("COORDINADOR: INTERRUMPIDO.");
            }
        }
    }

    public static class Persona implements Runnable {
        private int id;
        private Salon salon;
        private final Random random = new Random();

        public Persona(int id, Salon salon) {
            this.id = id;
            this.salon = salon;
        }

        private int getUnaActividad(int anterior) {
            int numero;

            if (anterior == 0) {
                // Primer turno: puede salir 1, 2 o 3
                numero = random.nextInt(3) + 1; // 1..3
            } else {
                // Segundo turno: no puede repetir el anterior
                do {
                    numero = random.nextInt(3) + 1; // 1..3
                } while (numero == anterior);
            }

            return numero;
        }

        @Override
        public void run() {
            try {
                int primerAct = salon.elegirActividad(id, getUnaActividad(0));
                salon.esperarIniciar();
                Thread.sleep((random.nextInt(3) + 1) * 1000);
                salon.salirDeActividad(primerAct);
                int segAct = salon.elegirOtraActividad(id, primerAct);
                salon.salirDeActividad(segAct);
                salon.esperarIniciar();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("PERSONA " + id + ": INTERRUMPIDA.");
            }
        }
    }
}
