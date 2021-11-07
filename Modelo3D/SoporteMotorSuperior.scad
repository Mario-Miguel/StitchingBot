//Medidas
ancho = 100;
largo = 100;
alto = 65;
grosor_caja = 5;
ancho_interior = ancho-(grosor_caja*2);
largo_interior = largo-(grosor_caja*2);
alto_interior = alto-grosor_caja+1;
radio_tornillo = 1.2;
radio_eje_motor = 12;
radio_eje_rodamiento = 13;

ancho_sujeccion_tuerca = 20;
largo_sujeccion_tuerca = 40;
alto_sujeccion_tuerca = 35;

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
        
        //Huecos para los tornillos
        tornillos_tapa(60);
            
        //Hueco para el eje del motor
        huecoMotor(ancho/2+20, largo, 35);
        
        huecoRodamiento(ancho/2-20, largo, 35);
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
    difference(){
        cube([ancho,largo, grosor_caja]); 
        tornillos_tapa(3);
    }
}

//Hueco para los tornillos para fijar la tapa
module huecoTornillo(x, y, z){
    translate([x, y, z])
        cylinder(
            h=10.5, 
            r=radio_tornillo, 
            center=true, 
            $fn=360
        );
}

//Huego para sacar el eje del motor
module huecoMotor(x, y, z){
    translate([x, y, z]){
        rotate([90,0,0])
            cylinder(
                h=60, 
                r=radio_eje_motor, 
                center=true, 
                $fn=360
            );
    }
    
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
    
    //Sujección del motor
    translate([49.5,40,0]){
        color("Blue")sujeccionMotor();
    }
}

module huecoTornilloEje(x, y, z){
    translate([x, y, z]){
            rotate([90,0,0])
                cylinder(
                    h=60, 
                    r=4, 
                    center=true, 
                    $fn=360
                );
        }
}

module sujeccionMotor(){
    huecoTornillo(7.5,12,0);
    huecoTornillo(7.5, 35, 0);
}


//Lugar para atornillar la tuerca de la varilla
module sujeccionTuerca(){
    difference(){
        cube([
            ancho_sujeccion_tuerca,
            largo_sujeccion_tuerca,
            alto_sujeccion_tuerca]);
        
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

//Huego para sacar el rodamiento
module huecoRodamiento(x, y, z){
    translate([x, y, z]){
        rotate([90,0,0])
            cylinder(
                h=20, 
                r=radio_eje_rodamiento, 
                center=true, 
                $fn=360
            );
    
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

//Módulo principal
module soporteMotorSuperiorV2(conTapa){
    translate([0,0,alto_sujeccion_tuerca]){
        cajaMotor();

        if(conTapa){
            translate([0,0,alto]){
                difference(){
                    color("Aqua") tapa();
                }
            }
        }
    }
    
    translate([5,30,0])
        sujeccionTuerca();
    translate([95,70,0])
        rotate([0,0,180])
            sujeccionTuerca();
    
}

//Mostrar la pieza
soporteMotorSuperiorV2(false);

