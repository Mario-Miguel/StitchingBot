module sujeccionTuerca(){
    difference(){
        cube([20,30,25]);
        
        //Hueco varilla
        translate([0,15,10])
            rotate([0,90,0])
                cylinder(h=60, r=5.1, center=true, $fn=360);
        
        //Hueco para meter la tuerca
        translate([5, 15, 10])
            cube([5.2, 23, 30], center=true);
        
        //Hueco para la extension de la tuerca
        translate([7.5, 9.9, 0])
            cube([20, 10.2, 10]);
        
        //Huecos para los tornillos 
        translate([0,15,18])
            rotate([0,90,0])
                cylinder(h=60, d=3.6, center=true, $fn=360);
        translate([0,7,10])
            rotate([0,90,0])
                cylinder(h=60, d=3.6, center=true, $fn=360);
        translate([0,23,10])
            rotate([0,90,0])
                cylinder(h=60, d=3.6, center=true, $fn=360);
    }
    
}

module sujeccionTuercaLarga(){
    difference(){
        union(){
            sujeccionTuerca();
            translate([0,0,25])
                cube([20,30,40]);
        }
        translate([0,15,50])
            rotate([0,90,0])
                cylinder(h=60, r=5.1, center=true, $fn=360);
    }
}

module sujeccionesTuercas(){
    translate([5,0,30])
        rotate([270,0,0]){
            sujeccionTuerca();
            
            translate([90,30,0])
                rotate([0,0,180])
                    sujeccionTuerca();
            translate([35,0,-40])
            sujeccionTuercaLarga();
        }
}

module pandereta(){
    hull(){
        translate([5,0,0])
            cube([90, 10, 30]);
        translate([5,16,7.5])
            cube([90, 5, 15]);
    }

    translate([0,20,7.5]){
        translate([5,5,0])
            difference(){
                cube([90,90,3]);
                translate([2.5,2.5,0])
                roundedCube([85,85,25], 3);
            }
            
        difference(){
            roundedCube([100,100,20], 2);
            translate([5,5,-1])
                roundedCube([90,90,25], 3);
            translate([-1, -1, -3])
                cube([102, 102, 3]);
            translate([-1, -1, 15])
                cube([102, 102, 3]);
        }
    }
    
}

module roundedCube(d,r) {
    minkowski() {
        translate([r,r]) cube([d[0]-2*r, d[1]-2*r, d[2]-2*r]);
        sphere(r, $fn=50);
    }
}

module sujeccionTela(){
    sujeccionesTuercas();
    
    translate([0,25,0])
        pandereta();
    
    translate([6.5,51.5,10])
        cierre();
}

module cierre(){
    difference(){
        roundedCube([87,87,17], 2);
        translate([5,5,-1])
            roundedCube([77,77,25], 3);
        translate([-1, -1, -3])
            cube([102, 102, 3]);
        translate([-1, -1, 12])
            cube([102, 102, 3]);
    }
}

sujeccionTela();