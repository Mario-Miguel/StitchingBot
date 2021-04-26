#include <AccelStepper.h>
#include <SoftwareSerial.h>

#include <stdio.h>
#include <string.h>

//Joystick Threshold
#define FORWARD_THRESHOLD 550
#define REVERSE_THRESHOLD 450

//Motor states
#define FORWARD 1
#define REVERSE -1
#define STOP 0

//Motor commands
#define C_FORWARD 2
#define C_REVERSE 0
#define C_STOP 1

//Joystick
#define vrX A0
#define vrY A1
#define jsSwitch 12

//StepperMotors
#define DIRX  2
#define STEPX 3
#define DIRY  8
#define STEPY 9

#define motorInterfaceType 1


//AccelStepper definition
AccelStepper stepperX = AccelStepper(motorInterfaceType, STEPX, DIRX);
AccelStepper stepperY = AccelStepper(motorInterfaceType, STEPY, DIRY);



//Variables
int xValue = STOP;
int yValue = STOP;

//Current state of each motor (0=stop, 1=forward, -1=reverse)
int xMotorState = 0;
int yMotorState = 0;

int state = 0;


//Bluetooth
#define rx 19 // => HC05: tx
#define tx 18 // => HC05: rx

SoftwareSerial BTSerial(rx, tx); // RX | TX of Arduino

char command[100];
char delimeter[] = ",;:";


int index = 0;

char c;
boolean flag = false;

void setup() {

  //Bluetooth try
  //Serial turns on in 1 second
  delay(1000);
  Serial1.begin(9600);
  
//  pinMode(ledPin, OUTPUT);
//  digitalWrite(ledPin, LOW);


  //Joystick
  pinMode(jsSwitch, INPUT);

  //Stepper motors
  stepperX.setMaxSpeed(1000);
  stepperY.setMaxSpeed(1000);

}

void loop() {

  bluetoothRead();

  //getJoystickValues();

  //moveMotorsLib(xMotorState, yMotorState);

  //delay(1000);

}

void bluetoothRead() {

  

if (Serial1.available()>0) {
    command = Serial1.read();
    //V,1
    //V,-1
    //H,1
    //H,-1
    //H,0
    //V,0

    //<- H,-1;V,0
    //-> H,1;V,0
    //^ H,0;V,1
    //_ H,0;V,-1

    //First motor order 
    char* ptr = strtok(string, delimiter);

    while(ptr != NULL) {
      
        Serial1.println(">Motor: "+ ptr);
        // create next part
        ptr = strtok(NULL, delimiter);

        Serial1.println(">Direction: "+ ptr);
        String horizontalDir =ptr;

        ptr = strtok(NULL, delimiter);
        Serial1.println(">Motor: "+ ptr);
        
        ptr = strtok(NULL, delimiter);
        Serial1.println(">Direction: "+ ptr);
        String verticalDir =ptr;

        //Next order
        ptr = strtok(NULL, delimiter);
    }

    
//    Serial1.println("Arduino dice: hey");
//    if (command == "abrir") {
//      Serial1.println("Arduino dice: puerta abierta");
//      //digitalWrite(ledPin, HIGH);
//    }
//    if (command == "cerrar") {
//      Serial1.println("Arduino dice: puerta cerrada");
//      //digitalWrite(ledPin, LOW);
//    }
  }
  
}

void processCommand() {
//  char motor = datos[0];
//  char inst = datos[1];

//  int motorInstruction = 
  
//  switch (command) {
//    case 'H':
//      if (inst == C_) {
//        //digitalWrite(ledPin, HIGH);
//        Serial1.println("Light: ON");
//      }
//      else if (inst == 'N') {
//        //digitalWrite(ledPin, LOW);
//        Serial1.println("Light: OFF");
//      }
//      break;
//  }
  //  char command = datos[0];
  //  char inst = datos[1];
  //  switch (command) {
  //    case 'L':
  //      if (inst == 'Y') {
  //        digitalWrite(LIGHT, HIGH);
  //        btm.println("Light: ON");
  //      }
  //      else if (inst == 'N') {
  //        digitalWrite(LIGHT, LOW);
  //        btm.println("Light: OFF");
  //      }
  //      break;
  //  }
}

//################################################################################

void getJoystickValues() {
  xValue = analogRead(vrX);
  yValue = analogRead(vrY);

  xMotorState = getCurrentState(xValue);
  yMotorState = getCurrentState(yValue);
}

//################################################################################

void moveMotorsLib(int xState, int yState) {
  stepperX.setSpeed(500 * xState);
  stepperX.runSpeed();

  stepperY.setSpeed(500 * yState);
  stepperY.runSpeed();
}

//################################################################################

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
