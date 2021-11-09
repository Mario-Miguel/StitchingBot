//Medidas
ancho = 90;
largo = 90;
alto = 65;
grosor_caja = 5;
ancho_interior = ancho-(grosor_caja*2);
largo_interior = largo-(grosor_caja*2);
alto_interior = alto-grosor_caja+1;
radio_tornillo = 1.2;
radio_eje_rodamiento = 13;

/**
* Crea la caja principal donde se colocará el rodamiento
*/
module cajaRodamiento(){
    difference(){
        union(){
            difference(){
                cube([ancho,largo, alto]); 
                translate([5,5,5])
                    cube([ancho_interior, largo_interior, alto_interior]);
            }
            
            //Sujecciones tornillos
            sujecciones_tornillos_tapa();
        }
        
        tornillos_tapa(60);
        //Hueco para el eje del motor -> 21.25+23,75
        huecoRodamiento(45, 0, 35);
    }
}

/**
* Crea los soportes que tiene la caja para poder atornillar la tapa
*/
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

/**
* Crea los huecos para los tornillos que fijan la tapa.
*
* @param alto altura de los huecos de los tornillos.
*/
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

/**
* Crea la tapa de la pieza
*/
module tapa(){
    cube([ancho,largo, 5]); 
}

/**
* Crea los huecos para los tornillos para fijar la tapa
* 
* @params x,y,z coordenadas utilizadas para colocar el hueco del tornillo.
*/
module huecoTornillo(x, y, z){
    translate([x, y, z])
        cylinder(h=10.5, r=radio_tornillo, center=true, $fn=360);
}

/**
* Genera los huecos necesarios para sujetar el rodamiento a la caja
*
* @params x,y,z coordenadas utilizadas para centrar el hueco del eje del motor.
*/
module huecoRodamiento(x, y, z){
    translate([x, y, z]){
        rotate([90,0,0])
            cylinder(h=20, r=radio_eje_rodamiento, center=true, $fn=360);
    
        //Hueco para los tornillos del rodamiento
        translate([-19, 0, 0])
            rotate([90,0,0])
                cylinder(h=20, r=2, center=true, $fn=360);
        translate([19,0,0])
            rotate([90,0,0])
                cylinder(h=20, r=2, center=true, $fn=360);
        
    }
}



/**
* Módulo principal del archivo.
* 
* Genera la pieza completa creada en este script.
*
* @param conTapa Boolean que indica si se desea mostrar la tapa de la pieza o no
*/
module soporteVarilla(conTapa){
    cajaRodamiento();
              
    if(conTapa){
        translate([0,0,65]){
            difference(){
                color("Aqua") tapa();
                //Huecos para los tornillos
                tornillos_tapa(3);

            }
        }
    }
}

//Mostrar la pieza
soporteVarilla(false);

