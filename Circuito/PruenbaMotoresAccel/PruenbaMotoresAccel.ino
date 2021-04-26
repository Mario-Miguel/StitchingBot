/*Example sketch to control a stepper motor with DRV8825 stepper motor driver, AccelStepper library and Arduino: continuous rotation. More info: https://www.makerguides.com */

// Include the AccelStepper library:
#include <AccelStepper.h>

// Define stepper motor connections and motor interface type. Motor interface type must be set to 1 when using a driver:
#define dirPin 2
#define stepPin 3
#define dirPin2 8
#define stepPin2 9
#define motorInterfaceType 1

//Joystick
#define vrX A0
#define vrY A1
int xValue = 0;
int yValue = 0;

// Create a new instance of the AccelStepper class:
AccelStepper stepper = AccelStepper(motorInterfaceType, stepPin, dirPin);
AccelStepper stepper2 = AccelStepper(motorInterfaceType, stepPin2, dirPin2);

void setup() {
  // Set the maximum speed in steps per second:
  stepper.setMaxSpeed(1000);
  stepper2.setMaxSpeed(1000);
}

void loop() {
  xValue = analogRead(vrX);
  yValue = analogRead(vrY);
  // Set the speed in steps per second:
  stepper.setSpeed(400);
  // Step the motor with a constant speed as set by setSpeed():
  stepper.runSpeed();
  
  stepper2.setSpeed(-400);
  // Step the motor with a constant speed as set by setSpeed():
  stepper2.runSpeed();
}
