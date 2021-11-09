/**
* Genera la extensión de la pieza donde se atornillará la tuerca de la varilla roscada.
*/
module sujeccionTuerca(){
    difference(){
        cube([20,25,25]);
        
        //Hueco varilla
        translate([0,12.5,10])
            rotate([0,90,0])
                cylinder(h=60, r=5.1, center=true, $fn=360);
        
        //Hueco para meter la tuerca
        translate([5, 12.5, 10])
            cube([5.2, 23, 30], center=true);
        
        //Hueco para la extension de la tuerca
        translate([7.5, 7.4, 0])
            cube([20, 10.2, 10]);
        
        //Huecos para los tornillos 
        pos_tornillos_varilla = [[12.5,18], [4.5,10], [21.5,10]];
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
* Genera la extensión de la pieza utilizada para dar estabilidad a la pieza.
*
* Por ella pasarán las dos varillas roscadas de la parte superior.
*/
module sujeccionTuercaLarga(){
    difference(){
        cube([15,25,65]);
        
        //El de alante
        translate([0,12.5,50])
            rotate([0,90,0])
                cylinder(h=60, r=5.1, center=true, $fn=360);
        
        translate([0,12.5,10])
            rotate([0,90,0])
                cylinder(h=60, d=8.1, center=true, $fn=360);
    }
}


/**
* Genera las extensiones por las que pasarán las varillas roscadas
*/
module sujeccionesTuercas(){
    translate([5,0,25])
        rotate([270,0,0]){
            sujeccionTuerca();
            translate([90,25,0])
                rotate([0,0,180])
                    sujeccionTuerca();
            translate([37.5,0,-40])
                sujeccionTuercaLarga();
        }
}

/**
* Genera la parte que une las sujecciones para las tuercas con el bastidor donde se colocará la tela
*/
module unionTuercasBastidor(){
    hull(){
        translate([5,0,0])
            cube([90, 2, 25]);
        translate([5,8,0])
            cube([90, 5, 12.5]);
    }
}


/**
* Módulo principal del archivo.
* 
* Genera la pieza completa creada en este script.
*/
module sujeccionTela(){
    translate([0,0,0])
        sujeccionesTuercas();
    translate([0,25,0])
        unionTuercasBastidor();
}


//Mostrar la pieza
sujeccionTela();