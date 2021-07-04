#include <AccelStepper.h>
#include <SoftwareSerial.h>

#include <stdio.h>
#include <string.h>

//#####################################################################################

//Joystick Threshold
#define FORWARD_THRESHOLD 550
#define REVERSE_THRESHOLD 450

//Joystick
#define vrX A0
#define vrY A1
#define jsSwitch 12

//#####################################################################################

//Motor states
#define FORWARD 1
#define REVERSE -1
#define STOP 0

//Motor commands
#define C_FORWARD 2
#define C_REVERSE 0
#define C_STOP 1

//StepperMotors
#define DIRX  2
#define STEPX 3

#define DIRY_1  7
#define STEPY_1 6
#define DIRY_2  8
#define STEPY_2 9

#define motorInterfaceType 1

//AccelStepper definition
AccelStepper stepperX = AccelStepper(motorInterfaceType, STEPX, DIRX);
AccelStepper stepperY1 = AccelStepper(motorInterfaceType, STEPY_1, DIRY_1);
AccelStepper stepperY2 = AccelStepper(motorInterfaceType, STEPY_2, DIRY_2);

//#####################################################################################

//Finales de carrera
#define FIN_CARRX 25
#define FIN_CARRY 23

//#####################################################################################

//Variables
int xValue = STOP;
int yValue = STOP;

//Current state of each motor (0=stop, 1=forward, -1=reverse)
int xMotorState = 0;
int yMotorState = 0;

int state = 0;

//#####################################################################################

//Bluetooth
#define rx 19 // => HC05: tx
#define tx 18 // => HC05: rx

SoftwareSerial BTSerial(rx, tx); // RX | TX of Arduino

char command;


//#####################################################################################
const String ASK_FOR_ACTIONS = "M";
#define MAX_ARRAY_SIZE 100
String actionsRead[MAX_ARRAY_SIZE];
int timeActionsRead[MAX_ARRAY_SIZE];
int xCoords[MAX_ARRAY_SIZE];
int yCoords[MAX_ARRAY_SIZE];
int hasToChange[MAX_ARRAY_SIZE];

//Ejercicio 5.1
int actionCounter = 0;
unsigned long startActionTime = 0;
boolean entradaLeida = false;
boolean startedFromJava = false;
boolean areActionsRemaining = false;
boolean actualPedalState = true;

String receivedString = "";

//#####################################################################################

void setup() {

  //Bluetooth try
  //Serial turns on in 1 second
  delay(1000);
  Serial1.begin(9600);
  Serial.begin(4800);

  //  pinMode(ledPin, OUTPUT);
  //  digitalWrite(ledPin, LOW);
  pinMode(FIN_CARRY, INPUT);
  pinMode(FIN_CARRX, INPUT);


  //Joystick
  pinMode(jsSwitch, INPUT);

  //Stepper motors
  stepperX.setMaxSpeed(5000);
  stepperY1.setMaxSpeed(5000);
  stepperY2.setMaxSpeed(5000);

}

void loop() {

  bluetoothRead2();

  //getJoystickValues();

  //moveMotorsLib(xMotorState, yMotorState);

  //delay(1000);

}

//#####################################################################################

//void bluetoothRead() {
//
//  if (Serial1.available() > 0) {
//    command = Serial1.read();
//    Serial.println(command);
//    if (command == 'R') {
//      moveMotorsLib4(xCoords[actionCounter] + 1, yCoords[actionCounter]);
//    }
//    else if (command == 'L') {
//      moveMotorsLib4(xCoords[actionCounter] - 1, yCoords[actionCounter]);
//    }
//    else if (command == 'U') {
//      moveMotorsLib4(xCoords[actionCounter], yCoords[actionCounter] + 1);
//    }
//    else if (command == 'D') {
//      moveMotorsLib4(xCoords[actionCounter], yCoords[actionCounter] - 1);
//    }
//
//    delay(10);
//
//  }
//
//}


void bluetoothRead2() {
  if (Serial1.available() > 0) {
    receivedString = Serial1.readString();
    createActions(receivedString);
    areActionsRemaining = true;
    delay(100);
  }

  //---------------------------------------

  if (areActionsRemaining) {

    if (actionCounter == MAX_ARRAY_SIZE) {
      //      Serial.println(">" + ASK_FOR_ACTIONS);
      Serial.println(">Pide más acciones");
      Serial1.println(ASK_FOR_ACTIONS);
      resetArrays();
      actionCounter = 0;
      entradaLeida = false;
      delay(100);
    }

    if (entradaLeida) {
      Serial.println(">Ejecuta una acción");
      executeActions();
    }
  }
}

//#####################################################################################

void createActions(String actionsString) {
  String entrada = actionsString;
  Serial.println(">Empieza a crear acciones con: " + actionsString);
  String entradaPartida = "";
  int i = 0;
  do {
    entradaPartida = getValue(entrada, ';', i);

    if (entradaPartida != "") {

      xCoords[i] = getValue(entradaPartida, ',', 0).toInt();
      yCoords[i] = getValue(entradaPartida, ',', 1).toInt();
      hasToChange[i] = getValue(entradaPartida, ',', 2).toInt();

    }
    i = i + 1;
  } while (entradaPartida != "");

  Serial.println(">Termina de crear acciones");
  entradaLeida = true;
}

String getValue(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }
  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

//#####################################################################################

void executeActions() {


  if (xCoords[actionCounter] == 0 && yCoords[actionCounter] == 0) {
    Serial.println(">Se acabaron las acciones wey");
    Serial1.println(ASK_FOR_ACTIONS);
    actionCounter = 0;
  }
  else {
    //Si tiene que cambiar, parar el pedal, despues mover en los ejes que sean
    if(hasToChange[actionCounter]){
      setPedalState(false);
    }
    else{
      setPedalState(true);
    }
    //Hacer que se mueva a la coordenada indicada
    Serial.println(">Se mueve a la coordenada x: " + String(xCoords[actionCounter]) + " - y: " + String(yCoords[actionCounter]));
    moveMotorsLib4(xCoords[actionCounter], yCoords[actionCounter]);
    actionCounter++;
  }
}

//#####################################################################################

void setPedalState(boolean state){
  
  if(!state){
    //TODO mirar a ver como controlar el pedal. En este caso hay que dejar de pisarlo
    actualPedalState = state;
  }
  else if(!actualPedalState){
    //TODO Hacer que se pulse el pedal
    actualPedalState = true;
  }
}

//#####################################################################################

void resetArrays() {
  memset(xCoords, 0, sizeof(xCoords));
  memset(yCoords, 0, sizeof(yCoords));
  memset(hasToChange, 0, sizeof(hasToChange));
}

//#####################################################################################

void getJoystickValues() {
  xValue = analogRead(vrX);
  yValue = analogRead(vrY);

  xMotorState = getCurrentState(xValue);
  yMotorState = getCurrentState(yValue);
}

//#####################################################################################

//void moveMotorsLib(int xState, int yState) {
//  stepperX.setSpeed(250 * xState);
//  if (!endOfPathX())
//    stepperX.runSpeed();
//
//  stepperY1.setSpeed(250 * yState);
//  stepperY2.setSpeed(250 * yState);
//  if (!endOfPathY()) {
//    stepperY1.runSpeed();
//    stepperY2.runSpeed();
//  }
//
//}


void moveMotorsLib4(int xCoord, int yCoord) {
  int xMovement = xCoord - stepperX.currentPosition();
  int yMovement = yCoord - stepperY1.currentPosition();
  int directionX = 0;
  int directionY = 0;

  if(xMovement<0){
    directionX = -1;
  }
  else{
    directionX = 1;
  }
  if(yMovement<0){
    directionY = -1;
  }
  else{
    directionY = 1;
  }

  stepperX.moveTo(xMovement);
  stepperY1.moveTo(yMovement);
  stepperY2.moveTo(yMovement);

  stepperX.setSpeed(500 * directionX);
  stepperY1.setSpeed(500 * directionY);
  stepperY2.setSpeed(500 * directionY);

  
  while ((!endOfPathX() && stepperX.distanceToGo() != 0) || (!endOfPathY() && stepperY1.distanceToGo() != 0)){
    if(!endOfPathX() && stepperX.distanceToGo() != 0)
      stepperX.runSpeed();
      
    if(!endOfPathY() && stepperY1.distanceToGo() != 0){
      stepperY1.runSpeed();
      stepperY2.runSpeed();
    }
  }

}

//#####################################################################################

bool endOfPathY() {
  return !digitalRead(FIN_CARRY);
}
bool endOfPathX() {
  return !digitalRead(FIN_CARRX);
}

//#####################################################################################

int getCurrentState(int readValue) {
  if (readValue > FORWARD_THRESHOLD) {
    return FORWARD;
  }
  else if (readValue < REVERSE_THRESHOLD) {
    return REVERSE;
  }
  else {
    return STOP;
  }
}
