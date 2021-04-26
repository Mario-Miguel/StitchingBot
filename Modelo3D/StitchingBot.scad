use<SoporteMotor.scad>
use<SoporteMotorSuperior.scad>
use<SoporteMotorSuperiorV2.scad>
use<SoporteVarilla.scad>
use<SoporteVarillaSuperior.scad>
use<SoporteVarillaSuperiorV2.scad>
use<VarillaRoscada.scad>




//Lado derecho
translate([400, 0, 0]){
    union(){
        soporteMotor(false);
        translate([0,430,0])
            soporteVarilla(false);
        varillaRoscada(45, 280, 35);
        //Superior
        translate([95,200,18])
            rotate([0,0,90])
                soporteMotorSuperiorV2(false);
            }
}

//Lado izquierdo
union(){
    translate([90,135,0])
        rotate([0,0,180])
            soporteVarilla(false);
    translate([0,430,0])
        soporteVarilla(false);
    
    varillaRoscada(45, 280, 35);
    
    //Superior
    translate([0,300,18])
        rotate([0, 0, 270])    
            soporteVarillaSuperiorV2(false);
}


//Varillas superiores
rotate([0, 0, 270])
    varillaRoscada(-230, 240, 87.5);
rotate([0, 0, 270])
    varillaRoscada(-270, 240, 87.5);