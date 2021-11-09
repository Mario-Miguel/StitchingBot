/**
 * @file StitchingBot.scad
 *
 * @mainpage StitchingBot
 *
 * @section description Descripción
 * Documentación de todas las piezas del modelo 3D del robot StitchingBot.
 *
 * @section author Autor
 * - Mario Miguel Blanco
 *
 */

use<SoporteMotor.scad>
use<SoporteMotorSuperior.scad>
use<SoporteVarilla.scad>
use<SoporteVarillaSuperior.scad>
use<VarillaRoscada.scad>
use<SoporteTela.scad>


//Lado derecho
translate([390, 0, 0]){
    union(){
        soporteMotor(false);
        translate([5,395,0])
            soporteVarilla(false);
        varillaRoscada(50, 245, 35);
        //Superior
        translate([100,200,18])
            rotate([0,0,90])
                soporteMotorSuperior(false);
            }
}

//Lado izquierdo 135-90= 45
union(){
    soporteMotor(false);
    translate([5,395,0])
        soporteVarilla(false);
    
    varillaRoscada(50, 245, 35);
    
    //Superior
    translate([5,300,18])
        rotate([0, 0, 270])    
            soporteVarillaSuperior(false);
}


//Varillas superiores
rotate([0, 0, 270])
    varillaRoscada(-230, 240, 87.5);
rotate([0, 0, 270])
    varillaRoscada(-270, 240, 87.5);



//Soporte para la tela
translate([200,260,73])
    sujeccionTela();