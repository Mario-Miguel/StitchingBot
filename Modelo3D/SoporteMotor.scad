//Medidas
ancho = 110;
largo = 135;
alto = 65;
grosor_caja = 5;
ancho_interior = ancho-(grosor_caja*2);
largo_interior = largo-(grosor_caja*2);
alto_interior = alto-grosor_caja;
radio_tornillo = 1.2;
radio_eje_motor = 12;

//Caja para colocar el motor
module cajaMotor(){
    difference(){
        cube([ancho,largo, alto]); 
        translate([5,5,5])
            cube([ancho_interior, largo_interior, alto_interior+1]);
    }
    
    //Sujecciones tornillos
    translate([5, 5, 0])
        cube([5, 5, 65]);
    translate([100, 5, 0])
        cube([5, 5, 65]);
    translate([5, 125, 0])
        cube([5, 5, 65]);
    translate([100, 125, 0])
        cube([5, 5, 65]);
}

//Tapa de la caja
module tapa(){
    cube([110,135, 5]); 
}

//Hueco para los tornillos para fijar la tapa
module huecoTornillo(x, y, z){
    translate([x, y, z])
        cylinder(h=10.5, r=radio_tornillo, center=true, $fn=360);
}

//Huego para sacar el eje del motor
module huecoMotor(x, y, z){
    translate([x, y, z])
        rotate([90,0,0])
            cylinder(h=20, r=radio_eje_motor, center=true, $fn=360);
}

module sujeccionMotor(){
    huecoTornillo(7.5,12,0);
    huecoTornillo(7.5, 35, 0);
    huecoTornillo(33.5, 12, 0);
    huecoTornillo(33.5, 35, 0);
}


//Módulo principal
module soporteMotor(conTapa){
    difference(){
        cajaMotor();
        
        //Huecos para los tornillos
        huecoTornillo(5, 5, 60);
        huecoTornillo(105, 5, 60);
        huecoTornillo(5, 130, 60);
        huecoTornillo(105, 130, 60);
        
        //Hueco para el eje del motor
        huecoMotor(45, 132, 35);
        
        //Sujección del motor
        translate([21.5,75,0]){
            color("Blue")sujeccionMotor();
        }
        
    }
    if(conTapa){
        translate([0,0,65]){
            difference(){
                color("Aqua") tapa();
                //Huecos para los tornillos
                huecoTornillo(5, 5, 3);
                huecoTornillo(105, 5, 3);
                huecoTornillo(5, 130, 3);
                huecoTornillo(105, 130, 3);
            }
        }
    }
}

//Mostrar la pieza
soporteMotor(false);

