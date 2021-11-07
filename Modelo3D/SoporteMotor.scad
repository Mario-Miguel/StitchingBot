//Medidas
ancho = 100;
largo = 100;
alto = 65;
grosor_caja = 5;
ancho_interior = ancho-(grosor_caja*2);
largo_interior = largo-(grosor_caja*2);
alto_interior = alto-grosor_caja +1;
radio_tornillo = 1.2;
radio_eje_motor = 12;
alto_eje_motor = 35;

//Caja para colocar el motor
module cajaMotor(){
    difference(){
        union(){
            difference(){
                cube([ancho,largo, alto]); 
                translate([grosor_caja, grosor_caja, grosor_caja])
                    cube([ancho_interior, largo_interior, alto_interior]);
            }
            sujecciones_tornillos_tapa();
        }
        tornillos_tapa(65);
        
        //Hueco para el eje del motor
        huecoMotor(ancho/2, largo, alto_eje_motor);
        
        //Sujección del motor
        translate([37.5,57,0]){
            sujeccionMotor();
        }
    }
}

module sujecciones_tornillos_tapa(){
    //Sujecciones tornillos
    pos_cubos_tornillos = [
        [grosor_caja,grosor_caja], 
        [ancho_interior,grosor_caja], 
        [grosor_caja,largo_interior], 
        [ancho_interior,largo_interior]
    ];
    for(i=[0:len(pos_cubos_tornillos)-1]){
        translate([pos_cubos_tornillos[i][0], pos_cubos_tornillos[i][1], 0])
            cube([grosor_caja, grosor_caja, alto]);
    }
}

module tornillos_tapa(alto){
    //Huecos para los tornillos de la tapa
    pos_tornillos_tapa = [
        [0,0],
        [ancho, 0],
        [0,largo],
        [ancho,largo]
    ];
    for(c=[0:len(pos_tornillos_tapa)-1]){
        huecoTornillo(
            abs(grosor_caja-pos_tornillos_tapa[c][0]),       
            abs(grosor_caja-pos_tornillos_tapa[c][1]), 
            alto
        );
    }
}

//Tapa de la caja
module tapa(){
    cube([ancho, largo, grosor_caja]); 
}

//Hueco para los tornillos para fijar la tapa
module huecoTornillo(x, y, z){
    translate([x, y, z])
        cylinder(h=11, r=radio_tornillo, center=true, $fn=360);
}

//Huego para sacar el eje del motor
module huecoMotor(x, y, z){
    translate([x, y, z])
        rotate([90,0,0])
            cylinder(h=30, r=radio_eje_motor, center=true, $fn=360);
    
    pos_tornillos_motor=[
        [1,1], 
        [1,-1], 
        [-1, 1], 
        [-1,-1]
    ];
    
    for(a=[0:len(pos_tornillos_motor)-1]){
        huecoTornilloEje(
            x+15*pos_tornillos_motor[a][0],
            y, 
            z+15*pos_tornillos_motor[a][1]
        );
    }
}

module huecoTornilloEje(x, y, z){
    translate([x, y, z]){
            rotate([90,0,0])
                cylinder(h=60, r=4, center=true, $fn=360);
        }
}

module sujeccionMotor(){
    pos_tornillos_sujeccion=[
        [0,0], 
        [0,1], 
        [1, 0], 
        [1,1]
    ];
    
    for(b=[0:len(pos_tornillos_sujeccion)-1]){
        huecoTornillo(
            pos_tornillos_sujeccion[b][0]*(radio_eje_motor*2)+1, 
            pos_tornillos_sujeccion[b][1]*(radio_eje_motor*2)-1, 
            0
        );
    }
}


//Módulo principal
module soporteMotor(conTapa){
    cajaMotor();
    
    if(conTapa){
        translate([0,0,65]){
            difference(){
                color("Aqua") tapa();
                //Huecos para los tornillos
                tornillos_tapa(0);
            }
        }
    }
}

//Mostrar la pieza
soporteMotor(false);

