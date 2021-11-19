/**
 * @file motor_controller.ino
 *
 * @mainpage StitchingBot
 *
 * @section description Descripción
 * Documentación del programa utilizado para mover los motores del robot así 
 * como la comunicación con la aplicación móvil.
 *
 *
 * @section libraries Librerías
 * - AccelStepper (https://www.arduino.cc/reference/en/libraries/accelstepper/)
 *   - Facilita el movimiento de los motores, proveyendo una interfaz para ello.
 *   
 * - SoftwareSerial (https://www.arduino.cc/en/Reference/SoftwareSerial)
 *   - Permite enviar y recibir mensajes vía bluetooth. Se utiliza para la 
 *      comunicación con el dispositivo móvil
 *
 *
 * @section author Autor
 * - Mario Miguel Blanco
 *
 */

#include <AccelStepper.h>
#include <SoftwareSerial.h>
#include <stdio.h>
#include <string.h>


//StepperMotors
/**@{*/
/**
 * Pin necesario para el funcionamiento del motor del eje X
 */
#define DIRX  2
#define STEPX 3
/**@}*/

/**@{*/
/**
 * Pin necesario para el funcionamiento de los motores del eje Y
 */
#define DIRY_1  4
#define STEPY_1 5
#define DIRY_2  8
#define STEPY_2 9
/**@}*/

/**@{*/
/**
 * Pin necesario para el funcionamiento del motor de la polea
 */
#define DIR_PULLEY 12
#define STEP_PULLEY 11
/**@}*/

/**
 * Define la interfaz del motor. Es necesario para la librería AccelStepper
 */
#define motorInterfaceType 1

//AccelStepper definition
/**@{*/
/**
 * Definición de los motores utilizando la librería AccelStepper
 */
AccelStepper stepperX = AccelStepper(motorInterfaceType, STEPX, DIRX);
AccelStepper stepperY1 = AccelStepper(motorInterfaceType, STEPY_1, DIRY_1);
AccelStepper stepperY2 = AccelStepper(motorInterfaceType, STEPY_2, DIRY_2);
AccelStepper stepperPulley = AccelStepper(motorInterfaceType, STEP_PULLEY, DIR_PULLEY);
/**@}*/

/**
 * Pasos necesarios para que el el motor de la polea mueva la aguja un ciclo completo
 */
int stepperPulleySteps = 467;


//Finales de carrera
/**@{*/
/**
 * Pin del final de carrera
 */
#define FIN_CARRX 25
#define FIN_CARRY 23
/**@}*/

//Bluetooth
/**@{*/
/**
 * Pin necesario para el funcionamiento del módulo bluetooth
 */
#define rx 19 // => HC05: tx
#define tx 18 // => HC05: rx
/**@}*/

/**
 * Fija los pines utilizados para la comunicación bluetooth
 */
SoftwareSerial BTSerial(rx, tx); // RX | TX of Arduino

/**@{*/
/**
 * Constante utilizada para comprobar las órdenes recibidas por bluetooth.
 */
const String ASK_FOR_ACTIONS = "M";
const String START_EXECUTION = "B";
const char CONFIGURE_PULLEY = 'C';
const char PAUSE_EXECUTION = 'P';
const char RESUME_EXECUTION = 'R';
const char STOP_EXECUTION = 'T';
const char UP = 'W';
const char DOWN = 'S';
const char LEFT = 'A';
const char RIGHT = 'D';
const char START_AUTOHOME = 'H';
/**@}*/

/**
 * Tamaño máximo de los vectores de coordenadas.
 */
#define MAX_ARRAY_SIZE 50

/**@{*/
/**
 * Vector utilizado para almacenar las coordenadas a las que moverse
 */
String actionsRead[MAX_ARRAY_SIZE];
int timeActionsRead[MAX_ARRAY_SIZE];
int xCoords[MAX_ARRAY_SIZE];
int yCoords[MAX_ARRAY_SIZE];
/**@}*/

/**@{*/
/**
 * Variable necesaria para mover los motores a la posición necesaria
 */
int actionCounter = 0;
unsigned long startActionTime = 0;
boolean entradaLeida = false;
boolean areActionsRemaining = false;
boolean isPaused = false;
/**@}*/

/**
 * Almacena la cadena recibida por bluetooth
 */
String receivedString = "";


/**
 * Función estándar de Arduino que se ejecuta al iniciar el programa. Se utiliza para 
 * iniciar variables, entradas y salidas.
 */
void setup() {

  delay(1000);
  Serial1.begin(9600);
  Serial.begin(4800);

  pinMode(FIN_CARRY, INPUT);
  pinMode(FIN_CARRX, INPUT);

  //Stepper motors
  stepperX.setMaxSpeed(500);
  stepperY1.setMaxSpeed(500);
  stepperY2.setMaxSpeed(500);
  stepperPulley.setMaxSpeed(1000);
}


/**
 * Función estándar de arduino que se ejecuta en bucle hasta que el Arduino se desconecta.
 * 
 * Intenta leer una entrada en el SerialMonitor destinado para la comunicación bluetooth.
 * En caso de que reciba una entrada, comprueba qué señal es y ejecuta un comando u otro.
 * En caso de no tener ninguna entrada, intenta ejecutar las acciones que se le ha enviado 
 * previamente.
 */
void loop() {

  if (Serial1.available() > 0) {
    receivedString = Serial1.readString();
    delay(100);
    
    if (receivedString.charAt(0) == CONFIGURE_PULLEY ) {
      int steps = receivedString.substring(2).toInt();
      configurePulleyMotor(steps);
    }
    else if (receivedString.indexOf(PAUSE_EXECUTION) >= 0 ) {
      Serial1.println(PAUSE_EXECUTION);
      isPaused = true;
    }
    else if (receivedString.indexOf(RESUME_EXECUTION) >= 0) {
      Serial1.println(RESUME_EXECUTION);
      isPaused = false;
    }
    else if (receivedString.indexOf(STOP_EXECUTION) >= 0) {
      Serial1.println(STOP_EXECUTION);
      Serial.println("EXECUTION STOPPED");
      actionCounter = 0;
      resetArrays();
      entradaLeida = false;
      receivedString = "";
      areActionsRemaining = false;
      isPaused = false;
      delay(100);
    }
    else if(receivedString.indexOf(UP) >= 0){
      moveUp();
    }
    else if(receivedString.indexOf(LEFT) >= 0){
      moveLeft();
    }
    else if(receivedString.indexOf(DOWN) >= 0){
      moveDown();
    }
    else if(receivedString.indexOf(RIGHT) >= 0){
      moveRight();
    }
    else if(receivedString.indexOf(START_AUTOHOME) >= 0){
      startAutohome();
    }
    else if (receivedString == START_EXECUTION) {
      startAutohome();
      Serial1.println(ASK_FOR_ACTIONS);
      actionCounter = 0;
      resetArrays();
      entradaLeida = false;
      receivedString = "";
      
      delay(100);
    }   
    else {
      createActions(receivedString);
      areActionsRemaining = true;
      delay(100);
    }

  }


  if (areActionsRemaining && !isPaused) {

    if (actionCounter == MAX_ARRAY_SIZE) {
      Serial1.println(ASK_FOR_ACTIONS);
      resetArrays();
      actionCounter = 0;
      entradaLeida = false;
      delay(100);
    }

    if (entradaLeida) {
      executeActions();
    }
  }

}


/**
 * Mueve los motores para colocar el robot en su posición inicial.
 */
void startAutohome() {
  stepperX.setSpeed(-250);
  stepperY1.setSpeed(-250);
  stepperY2.setSpeed(-250);

  while (!endOfPathX() || !endOfPathY()) {
    if (!endOfPathX())
      stepperX.runSpeed();

    if (!endOfPathY()) {
      stepperY1.runSpeed();
      stepperY2.runSpeed();
    }
  }

  stepperX.setCurrentPosition(0);
  stepperY1.setCurrentPosition(0);
  stepperY2.setCurrentPosition(0);

  stepperX.moveTo(2000);
  stepperY1.moveTo(50);
  stepperY2.moveTo(50);

  stepperX.setSpeed(250);
  stepperY1.setSpeed(250);
  stepperY2.setSpeed(250);

  while (stepperX.distanceToGo() != 0 || stepperY1.distanceToGo() != 0) {
    if (stepperX.distanceToGo() != 0)
      stepperX.runSpeed();

    if (stepperY1.distanceToGo() != 0) {
      stepperY1.runSpeed();
      stepperY2.runSpeed();
    }
  }

  stepperX.setCurrentPosition(0);
  stepperY1.setCurrentPosition(0);
  stepperY2.setCurrentPosition(0);

}


/**
 * Convierte una cadena de acciones en dos vectores de enteros, uno para las 
 * coordenadas X y otro para las coordenadas Y.
 * 
 * @param actionsString cadena conteniendo todas las acciones.
 */
void createActions(String actionsString) {
  String entrada = actionsString;
  Serial.println(">Empieza a crear acciones con: " + entrada);
  String entradaPartida = "";
  int i = 0;
  do {
    entradaPartida = getValue(entrada, ';', i);

    if (entradaPartida != "") {
      xCoords[i] = getValue(entradaPartida, ',', 0).toInt();
      yCoords[i] = getValue(entradaPartida, ',', 1).toInt();
    }
    i = i + 1;
  } while (entradaPartida != "");
  entradaLeida = true;
}


/**
 * Parte una cadena en función del separador y devuelve el elemento que se indica en el index
 * 
 * @param datos Cadena que se desea dividir
 * @param separator Separador por el que se va a dividir la cadena
 * @param index posición del elemento que se desea obtener
 * @return Trozo de la cadena pasada por parámetro.
 */
String getValue(String datos, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = datos.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (datos.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }
  return found > index ? datos.substring(strIndex[0], strIndex[1]) : "";
}


/**
 * Mueve los motores a las coordenadas que se le han enviado. 
 * En caso de no tener más acciones, envía un mensaje vía bluetooth pidiendo más.
 */
void executeActions() {

  if (xCoords[actionCounter] == 0 && yCoords[actionCounter] == 0) {
    Serial1.println(ASK_FOR_ACTIONS);
    actionCounter = 0;
  }
  else {
    //Hacer que se mueva a la coordenada indicada
    moveMotorsLib(xCoords[actionCounter], yCoords[actionCounter]);
    actionCounter++;
  }
}


/**
 * Pone los valores de los vectores donde se almacenan las coordenadas a 0.
 */
void resetArrays() {
  memset(xCoords, 0, sizeof(xCoords));
  memset(yCoords, 0, sizeof(yCoords));
}


/**
 * Mueve los motores a unas coordenadas específicas que se les pasa por parámetro
 * 
 * @param xCoord,yCoord Coordenadas X e Y a las que se desea mover el motor.
 */
void moveMotorsLib(int xCoord, int yCoord) {
  int xMovement = xCoord - (-1 * stepperX.currentPosition());
  int yMovement = yCoord - stepperY1.currentPosition();
  int directionX = 0;
  int directionY = 0;

  if (xMovement < 0) {
    directionX = 1;
  }
  else if (xMovement > 0) {
    directionX = -1;
  }
  if (yMovement < 0) {
    directionY = -1;
  }
  else if (yMovement > 0) {
    directionY = 1;
  }

  stepperX.moveTo(-xCoord);
  stepperY1.moveTo(yCoord);
  stepperY2.moveTo(yCoord);

  stepperX.setSpeed(250 * directionX);
  stepperY1.setSpeed(250 * directionY);
  stepperY2.setSpeed(250 * directionY);

  stepperPulley.setCurrentPosition(0);
  stepperPulley.moveTo(stepperPulleySteps);
  stepperPulley.setSpeed(500);


  while (
    (!endOfPathX() && stepperX.distanceToGo() != 0) 
    || (!endOfPathY() && stepperY1.distanceToGo() != 0) 
    || (stepperPulley.distanceToGo() != 0)
  ) {

    if (!endOfPathX() && stepperX.distanceToGo() != 0)
      stepperX.runSpeed();

    if (!endOfPathY() && stepperY1.distanceToGo() != 0) {
      stepperY1.runSpeed();
      stepperY2.runSpeed();
    }
    if (stepperPulley.distanceToGo() != 0) {

      stepperPulley.runSpeed();
    }
  }
  delay(100);
}


/**
 * Indica si se ha llegado al final de carrera del eje Y del robot
 * 
 * @return true si ha llegado al final de carrera, false en caso contrario.
 */
bool endOfPathY() {
  return !digitalRead(FIN_CARRY);
}


/**
 * Indica si se ha llegado al final de carrera del eje X del robot
 * 
 * @return true si ha llegado al final de carrera, false en caso contrario.
 */
bool endOfPathX() {
  return !digitalRead(FIN_CARRX);
}


/**
 * Configura los pasos del motor de la polea y lo mueve para probarlo
 * 
 * @param steps Pasos con los que se desea configurar el motor de la polea.
 */
void configurePulleyMotor(int steps) {
  stepperPulleySteps = steps;
  runPulleyMotor();
}


/**
 * Mueve el motor de la polea una serie de pasos determinados, determinados por 
 * stepperPulleySteps
 */
void runPulleyMotor() {
  stepperPulley.setCurrentPosition(0);
  stepperPulley.moveTo(stepperPulleySteps);
  stepperPulley.setSpeed(500);

  while ((stepperPulley.distanceToGo() != 0)) {
    stepperPulley.runSpeed();
  }
}

//#############################################################################################
void moveDown() {
  
  int yMovement = stepperY1.currentPosition()-16;

  stepperY1.moveTo(yMovement);
  stepperY2.moveTo(yMovement);

  stepperY1.setSpeed(-250);
  stepperY2.setSpeed(-250);



  while (!endOfPathY() && stepperY1.distanceToGo() != 0) {
    if (!endOfPathY() && stepperY1.distanceToGo() != 0) {
      stepperY1.runSpeed();
      stepperY2.runSpeed();
    }
  }
  
  delay(100);
}
//#############################################################################################

void moveLeft() {
  int xMovement = stepperX.currentPosition() - 16;

  stepperX.moveTo(xMovement);

  stepperX.setSpeed(-250);

  while (!endOfPathX() && stepperX.distanceToGo() != 0) {

    if (!endOfPathX() && stepperX.distanceToGo() != 0)
      stepperX.runSpeed();

  }
  delay(100);
}
//#############################################################################################

void moveRight() {
  int xMovement =stepperX.currentPosition()+16;


  stepperX.moveTo(xMovement);

  stepperX.setSpeed(250);



  while (!endOfPathX() && stepperX.distanceToGo() != 0) {
    if (!endOfPathX() && stepperX.distanceToGo() != 0)
      stepperX.runSpeed();
  }
  delay(100);
}
//#############################################################################################

void moveUp() {
  int yMovement = stepperY1.currentPosition()+16;

  stepperY1.moveTo(yMovement);
  stepperY2.moveTo(yMovement);

  stepperY1.setSpeed(250);
  stepperY2.setSpeed(250);



  while (!endOfPathY() && stepperY1.distanceToGo() != 0) {

    if (!endOfPathY() && stepperY1.distanceToGo() != 0) {
      stepperY1.runSpeed();
      stepperY2.runSpeed();
    }
  }
  delay(100);
}
