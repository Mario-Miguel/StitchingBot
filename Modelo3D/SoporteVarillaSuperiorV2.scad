//Medidas
ancho = 100;
largo = 90;
alto = 65;
grosor_caja = 5;
ancho_interior = ancho-(grosor_caja*2);
largo_interior = largo-(grosor_caja*2);
alto_interior = alto-grosor_caja;
radio_tornillo = 1.2;
radio_eje_rodamiento = 13;

//Caja para colocar el motor
module cajaMotor(){
    difference(){
        cube([ancho,largo, alto]); 
        translate([5,5,5])
            cube([ancho_interior, largo_interior, alto_interior+1]);
    }
    
    //Sujecciones tornillos
    translate([grosor_caja, grosor_caja, 0])
        cube([grosor_caja, grosor_caja, alto]);
    translate([ancho_interior, grosor_caja, 0])
        cube([grosor_caja, grosor_caja, alto]);
    translate([grosor_caja, largo_interior, 0])
        cube([grosor_caja, grosor_caja, alto]);
    translate([ancho_interior, largo_interior, 0])
        cube([grosor_caja, grosor_caja, alto]);
}

//Tapa de la caja
module tapa(){
    difference(){
        cube([ancho,largo, grosor_caja]); 
        huecoTornillo(grosor_caja, grosor_caja, 3);
        huecoTornillo(ancho-grosor_caja, grosor_caja, 3);
        huecoTornillo(grosor_caja, largo-grosor_caja, 3);
        huecoTornillo(ancho-grosor_caja, largo-grosor_caja, 3);
    }
}

//Hueco para los tornillos para fijar la tapa
module huecoTornillo(x, y, z){
    translate([x, y, z])
        cylinder(h=10.5, r=radio_tornillo, center=true, $fn=360);
}



//Lugar para atornillar la tuerca de la varilla
module sujeccionTuerca(){
    difference(){
        cube([20,40,35]);
        
        //Hueco varilla
        translate([0,20,17])
            rotate([0,90,0])
                cylinder(h=60, r=5.1, center=true, $fn=360);
        
        //Hueco para meter la tuerca
        translate([5, 20, 13.4])
            cube([5, 22, 27], center=true);
        
        //Hueco para la extension de la tuerca
        translate([7.5, 15, 0])
            cube([20, 10, 18]);
        
        //Huecos para los tornillos 
        translate([0, 20, 9])
            rotate([0,90,0])
                cylinder(h=60, d=3.6, center=true, $fn=360);
        translate([0,20,25])
            rotate([0,90,0])
                cylinder(h=60, d=3.6, center=true, $fn=360);
        translate([0,12,17])
            rotate([0,90,0])
                cylinder(h=60, d=3.6, center=true, $fn=360);
        translate([0,28,17])
            rotate([0,90,0])
                cylinder(h=60, d=3.6, center=true, $fn=360);
    }
    
}

//Huego para sacar el rodamiento
module huecoRodamiento(x, y, z){
    translate([x, y, z]){
        rotate([90,0,0])
            cylinder(h=20, r=radio_eje_rodamiento, center=true, $fn=360);
    
        //Hueco para los tornillos del rodamiento
        rotate([0,90,0]){
            translate([-19, 0, 0])
                rotate([90,0,0])
                    cylinder(h=20, r=2, center=true, $fn=360);
            translate([19,0,0])
                rotate([90,0,0])
                    cylinder(h=20, r=2, center=true, $fn=360);
            }
        
    }
}

//Módulo principal
module soporteVarillaSuperiorV2(conTapa){
    translate([0,0,35]){
        difference(){
            cajaMotor();
            
            //Huecos para los tornillos
            huecoTornillo(grosor_caja, grosor_caja, 60);
            huecoTornillo(ancho-grosor_caja, grosor_caja, 60);
            huecoTornillo(grosor_caja, largo-grosor_caja, 60);
            huecoTornillo(ancho-grosor_caja, largo-grosor_caja, 60);
            
            //Hueco para el eje del motor -> 21.25+23,75
            //Desplazado -> ancho/2 + 5.5
            huecoRodamiento(ancho/2+20, largo, 35);
            
            huecoRodamiento(ancho/2-20, largo, 35);
            
            
        }
        if(conTapa){
            translate([0,0,65]){
                difference(){
                    color("Aqua") tapa();
                    //Huecos para los tornillos
                    
    
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
soporteVarillaSuperiorV2(false);
//tapa();

