//Medidas
ancho = 100;
largo = 90;
alto = 65;
grosor_caja = 5;
ancho_interior = ancho-(grosor_caja*2);
largo_interior = largo-(grosor_caja*2);
alto_interior = alto-grosor_caja+1;
radio_tornillo = 1.2;
radio_eje_rodamiento = 13;

ancho_sujeccion_tuerca = 20;
largo_sujeccion_tuerca = 40;
alto_sujeccion_tuerca = 35;

/**
* Crea la caja principal donde se colocará el rodamiento
*/
module cajaRodamiento(){
    difference(){
        union(){
            difference(){
                cube([ancho,largo, alto]); 
                translate([grosor_caja,grosor_caja,grosor_caja])
                    cube([ancho_interior, largo_interior, alto_interior]);
            }
            
            sujecciones_tornillos_tapa();
        }
        
        //Huecos para los tornillos
        tornillos_tapa(60);
            
        //Hueco para el eje del motor
        huecoRodamiento(ancho/2+20, largo, 35);
            
        huecoRodamiento(ancho/2-20, largo, 35);
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
    difference(){
        cube([ancho, largo, grosor_caja]); 
        tornillos_tapa(3);
    }
}

/**
* Crea los huecos para los tornillos para fijar la tapa
* 
* @params x,y,z coordenadas utilizadas para colocar el hueco del tornillo.
*/
module huecoTornillo(x, y, z){
    translate([x, y, z])
        cylinder(
            h=10.5, 
            r=radio_tornillo, 
            center=true, 
            $fn=360
        );
}


/**
* Genera la extensión de la pieza donde se atornillará la tuerca de la varilla roscada.
*/
module sujeccionTuerca(){
    difference(){
        cube([
            ancho_sujeccion_tuerca,
            largo_sujeccion_tuerca,
            alto_sujeccion_tuerca
        ]);
        
        //Hueco varilla
        translate([0,20,17])
            rotate([0,90,0])
                cylinder(
                    h=60, 
                    r=5.1, 
                    center=true, 
                    $fn=360
                );
        
        //Hueco para meter la tuerca
        translate([5, 20, 14])
            cube([5, 22, 35], center=true);
        
        //Hueco para la extension de la tuerca
        translate([7.5, 15, 0])
            cube([20, 10, 18]);
        
        //Huecos para los tornillos 
        pos_tornillos_varilla = [[20,25], [12,17], [28,17]];
        for(i=[0:len(pos_tornillos_varilla)-1]){
            huecoTornilloVarilla(
                0, 
                pos_tornillos_varilla[i][0],
                pos_tornillos_varilla[i][1]);
        }
    }
}

/**
* Genera el hueco para colocar un tornillo para fijar la tuerca de la varilla roscada
*
* @params x,y,z coordenadas en las que debe estar el hueco.
*/
module huecoTornilloVarilla(x, y, z){
    translate([x,y,z])
            rotate([0,90,0])
                cylinder(
                    h=60, 
                    d=3.6, 
                    center=true, 
                    $fn=360
                );
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
        rotate([0,90,0]){
            translate([-19, 0, 0])
                rotate([90,0,0])
                    cylinder(
                        h=20, 
                        r=2, 
                        center=true, 
                        $fn=360
                    );
            translate([19,0,0])
                rotate([90,0,0])
                    cylinder(
                        h=20, 
                        r=2, 
                        center=true, 
                        $fn=360
                    );
            }
    }
}

/**
* Genera la extensión de la pieza donde se atornillará el final de carrera.
*
* @params x,y,z coordenadas utilizadas para centrar el hueco del eje del motor.
*/
module sujeccionFinalDeCarrera(){
    cube([15, 35, 40]);
}


/**
* Módulo principal del archivo.
* 
* Genera la pieza completa creada en este script.
*
* @param conTapa Boolean que indica si se desea mostrar la tapa de la pieza o no
*/
module soporteVarillaSuperior(conTapa){
    translate([0,0,alto_sujeccion_tuerca]){
        cajaRodamiento();

        translate([-15, 55, 5])
            sujeccionFinalDeCarrera();
        if(conTapa){
            translate([0,0,65]){
                difference(){
                    color("Aqua") tapa();
                }
            }
        }
    }
    
    translate([5,25,0])
        sujeccionTuerca();
    translate([95,65,0])
        rotate([0,0,180])
            sujeccionTuerca();
    
}

//Mostrar la pieza
soporteVarillaSuperior(false);

